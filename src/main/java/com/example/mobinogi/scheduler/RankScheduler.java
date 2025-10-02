package com.example.mobinogi.scheduler;

import com.example.mobinogi.entity.GameClass;
import com.example.mobinogi.repository.GameClassRepository;
import com.example.mobinogi.service.rank.RankCollectService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RankScheduler{
	
	private final RankCollectService rankCollectService;
	private final GameClassRepository gameClassRepository;
	
	public RankScheduler(GameClassRepository gameClassRepository, RankCollectService rankCollectService){
		this.gameClassRepository = gameClassRepository;
		this.rankCollectService = rankCollectService;
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Scheduled(cron = "0 45 * * * *") // 매 시간 30분마다 실행
	public void fetchRankData(){
		try{
			List<GameClass> gameClasses = gameClassRepository.findAll(); // 전체 GameClass 목록 불러오기
			
			// gameClasses 10개씩 그룹화
			List<List<GameClass>> groupedGameClasses = partitionList(gameClasses, 10);
			
			// 각 그룹을 비동기적으로 처리
			List<CompletableFuture<Void>> futures = groupedGameClasses.stream()
				.map(this::processGameClassGroupAsync)
				.toList();
			
			// 모든 비동기 작업 완료 대기
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			
			System.out.println("All gameClasses have been processed successfully!");
		}catch(Exception e){
			throw new RuntimeException("Error occurred during scheduled task execution", e);
		}
	}
	
	// 그룹된 GameClasses를 비동기로 처리하는 함수
	private CompletableFuture<Void> processGameClassGroupAsync(List<GameClass> gameClassGroup){
		return CompletableFuture.runAsync(() -> {
			for(GameClass gameClass : gameClassGroup){
				Long classId = gameClass.getClassId();
				try{
					// classId를 동기적으로 처리하면서 속도 제한 적용
					processPagesWithRateLimit(classId); // 10000ms 지연
				}catch(Exception e){
					System.err.println("Error processing classId: " + classId + ", " + e.getMessage());
				}
			}
		});
	}
	
	// 페이지 순회를 동기적으로 처리하면서 요청 속도 제한 적용
	private void processPagesWithRateLimit(Long classId){
		for(int i = 1 ; i <= 50 ; i++){
			try{
				rankCollectService.rankCollect(1, i, 2, classId.intValue());
				
				// 지정된 지연시간만큼 대기
				TimeUnit.MILLISECONDS.sleep(2000);
			}catch(Exception e){
				System.err.println("Request failed for classId: " + classId + ", page: " + i);
				throw new RuntimeException(e);
			}
		}
		System.out.println("ClassId " + classId + " processed successfully!");
	}
	
	// 리스트를 n개씩 나누는 유틸리티 메서드
	private <T> List<List<T>> partitionList(List<T> list, int size){
		return IntStream.range(0, (list.size() + size - 1) / size)
			.mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
			.collect(Collectors.toList());
	}
}