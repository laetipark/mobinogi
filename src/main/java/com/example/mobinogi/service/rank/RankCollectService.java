package com.example.mobinogi.service.rank;

import com.example.mobinogi.entity.GameClass;
import com.example.mobinogi.entity.UserRank;
import com.example.mobinogi.repository.GameClassRepository;
import com.example.mobinogi.repository.UserRankRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class RankCollectService{
	
	private final UserRankRepository userRankRepository; // JPA repository for UserRank
	private final GameClassRepository gameClassRepository; // Inject GameClassRepository
	
	@Value("${COOKIE_INFO}")
	private String cookieInfo;
	
	@Value("${NEXON_ID}")
	private String nexonId;
	
	@Value("${NEXON_PW}")
	private String nexonPw;
	
	private static final Logger logger = LoggerFactory.getLogger(RankCollectService.class);
	
	public RankCollectService(UserRankRepository userRankRepository, GameClassRepository gameClassRepository){
		this.userRankRepository = userRankRepository;
		this.gameClassRepository = gameClassRepository;
	}
	
	@Transactional
	public void rankCollect(int t, int pageno, int s, int c){
		try{
			HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2) // HTTP/2 활성화
				.build();
			
			String postData = "t=" + t + "&pageno=" + pageno + "&s=" + s + "&c=" + c + "&search=";
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://mabinogimobile.nexon.com/Ranking/List/rankdata"))
				.header("User-Agent", getRandomUserAgent()) // User-Agent 랜덤화
				.header("Origin", "https://mabinogimobile.nexon.com")
				.header("Referer", "https://mabinogimobile.nexon.com/Ranking/List")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Cookie", cookieInfo)
				.POST(HttpRequest.BodyPublishers.ofString(postData))
				.build();
			
			// 요청 보내기 (재시도 및 속도 제한 적용)
			String responseBody = sendRequestWithRetry(client, request); // 최대 3번 재시도, 500ms 대기
			processResponse(responseBody, s, c); // 응답 데이터 처리
		}catch(Exception e){
			logger.error("An error occurred during data collection for pageno {}, classId {}: {}", pageno, c, e.getMessage(), e);
		}
	}
	
	// 응답 데이터를 처리하는 로직
	private void processResponse(String responseBody, int serverId, int classId){
		try{
			Document doc = Jsoup.parse(responseBody);
			Elements rankItems = doc.select("ul.list > li.item");
			
			if(rankItems.isEmpty()){
				logger.warn("No ranking data found for serverId {} and classId {}", serverId, classId);
				return;
			}
			
			List<UserRank> rankList = new ArrayList<>();
			
			for(Element item : rankItems){
				try{
					String classAttr = item.select("dl:has(dt:contains(클래스)) dd").attr("class");
					String userName = item.select("dl:has(dt:contains(캐릭터명)) dd").attr("data-charactername");
					
					// 새로운 구조: 전투력은 type_1 클래스를 가진 span에서 가져오기
					String userPowerStr = item.select("span.type_1").text();
					
					// 데이터 유효성 검사
					if(userName.isEmpty() || userPowerStr.isEmpty() || classAttr.isEmpty()){
						logger.warn("Skipping due to missing data: {}", item.outerHtml());
						continue;
					}
					
					int userPower = Integer.parseInt(userPowerStr.replace(",", "").trim());
					int parsedClassId = parseClassId(classAttr);
					
					if(parsedClassId == 0){
						logger.warn("Skipping due to unknown classAttr: {}", classAttr);
						continue;
					}
					
					UserRank userRank = new UserRank();
					userRank.setServerId(serverId);
					userRank.setClassId(parsedClassId);
					userRank.setUserName(userName);
					userRank.setUserPower(userPower);
					
					logger.info("Extracted -> classAttr: {}, userName: {}, userPower: {}, classId: {}", classAttr, userName, userPower, parsedClassId);
					rankList.add(userRank);
				}catch(Exception e){
					logger.error("Error processing rank item: {}", item.outerHtml(), e);
				}
			}
			
			// 유효한 데이터 저장
			userRankRepository.saveAll(rankList);
			logger.info("Successfully saved {} user ranks for serverId {} and classId {}.", rankList.size(), serverId, classId);
			
		}catch(Exception e){
			logger.error("Error processing response data: {}", e.getMessage(), e);
		}
	}
	
	// 요청을 재시도하며 전송
	private String sendRequestWithRetry(HttpClient client, HttpRequest request) throws Exception{
		int attempt = 0;
		
		while(attempt < 3){
			try{
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				if(response.statusCode() == 200){
					logger.info("Request succeeded on attempt {} for URI: {}", (attempt + 1), request.uri());
					return response.body();
				}else{
					logger.warn("Unexpected status: {} on attempt {} for URI: {}", response.statusCode(), (attempt + 1), request.uri());
				}
			}catch(Exception e){
				logger.error("Request error on attempt {} for URI: {}", (attempt + 1), request.uri(), e);
			}
			
			attempt++;
			Thread.sleep(500L * attempt); // 점진적 Delay 적용
		}
		
		throw new Exception("Max retries reached for URI: " + request.uri());
	}
	
	// User-Agent 랜덤화
	private String getRandomUserAgent(){
		String[] userAgents = {
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36",
			"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0"
		};
		return userAgents[(int) (Math.random() * userAgents.length)];
	}
	
	// ClassId 파싱 로직
	private int parseClassId(String classAttr){
		GameClass gameClass = gameClassRepository.findByClassCode(classAttr);
		if(gameClass != null){
			return gameClass.getClassId().intValue();
		}else{
			return 0; // Default or fallback if classAttr not found
		}
	}
}