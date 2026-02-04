package com.ipaccessanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class IpAccessAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpAccessAnalyzerApplication.class, args);
	}

}
