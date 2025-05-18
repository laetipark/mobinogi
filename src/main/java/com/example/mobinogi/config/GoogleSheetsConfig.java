package com.example.mobinogi.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GoogleSheetsConfig{
	
	@Value("${google.sheets.credentials.path}")
	private String credentialsPath;
	
	
	@Bean(name = "googleSheetsClient")
	public Sheets googleSheetsService() throws IOException, GeneralSecurityException{
		GoogleCredentials credentials = GoogleCredentials
			.fromStream(new ClassPathResource(credentialsPath).getInputStream())
			.createScoped("https://www.googleapis.com/auth/spreadsheets.readonly");
		
		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
		
		return new Sheets.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			GsonFactory.getDefaultInstance(),
			requestInitializer
		).setApplicationName("Google Sheets Reader").build();
	}
}
