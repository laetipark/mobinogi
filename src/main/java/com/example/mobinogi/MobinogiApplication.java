package com.example.mobinogi;

import com.example.mobinogi.service.GoogleSheetsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class MobinogiApplication implements CommandLineRunner{
	
	private final GoogleSheetsService sheetsService;
	
	public MobinogiApplication(GoogleSheetsService sheetsService){
		this.sheetsService = sheetsService;
	}
	
	public static void main(String[] args){
		SpringApplication.run(MobinogiApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception{
	}
}
