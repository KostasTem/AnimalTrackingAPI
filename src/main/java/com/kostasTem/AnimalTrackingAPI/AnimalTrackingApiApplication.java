package com.kostasTem.AnimalTrackingAPI;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kostasTem.AnimalTrackingAPI.Security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class AnimalTrackingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalTrackingApiApplication.class, args);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
		return FirebaseMessaging.getInstance(firebaseApp);
	}

	@Bean
	FirebaseAuth firebaseAuth(FirebaseApp firebaseApp){
		return FirebaseAuth.getInstance(firebaseApp);
	}
	@Bean
	FirebaseApp firebaseApp(GoogleCredentials credentials) {
		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(credentials)
				.build();

		return FirebaseApp.initializeApp(options);
	}

	@Bean
	GoogleCredentials googleCredentials() {
		try (InputStream is = new FileInputStream(ResourceUtils.getFile("classpath:credentials.json"))) {
			return GoogleCredentials.fromStream(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
