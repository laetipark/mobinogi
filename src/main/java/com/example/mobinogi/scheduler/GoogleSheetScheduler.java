package com.example.mobinogi.scheduler;

import com.example.mobinogi.service.GoogleSheetsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleSheetScheduler{
	
	private final GoogleSheetsService sheetService;
	
	public GoogleSheetScheduler(GoogleSheetsService sheetService){
		this.sheetService = sheetService;
	}
	
	@Scheduled(fixedRate = 60000)
	public void fetchSheetData(){
		try{
			sheetService.fetchAndSaveItem();
			sheetService.fetchAndSaveNpc();
			sheetService.fetchAndSaveRegion();
			sheetService.fetchAndSaveBarter();
			System.out.println("Data saved to DB.");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
