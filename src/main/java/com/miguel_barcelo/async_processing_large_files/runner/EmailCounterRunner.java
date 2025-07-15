package com.miguel_barcelo.async_processing_large_files.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.miguel_barcelo.async_processing_large_files.service.EmailCounterService;
import com.miguel_barcelo.async_processing_large_files.util.AppConstants;

@Component
@Order(10)
public class EmailCounterRunner implements CommandLineRunner {

	private final EmailCounterService service;
	
	public EmailCounterRunner(EmailCounterService service) {
		this.service = service;
	}

	@Override
	public void run(String... args) throws Exception {
		String filePath = AppConstants.USERS_FILE_PATH;
		long start = System.currentTimeMillis();
		int total = service.countGmailEmails(filePath);
		long duration = System.currentTimeMillis() - start;
		
		System.out.printf("📬 Total number of @gmail.com emails: %d\n", total);
		System.out.printf("⏱️ Total time: %d ms\n", duration);
	}
}
