package com.miguel_barcelo.async_processing_large_files.runner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
		
		ExecutorService executor = Executors.newFixedThreadPool(AppConstants.DOMAINS.length);
		Map<String, Future<Integer>> results = new HashMap<>();
		String filePath = AppConstants.USERS_FILE_PATH;
		
		long start = System.currentTimeMillis();
		
		for (String domain : AppConstants.DOMAINS) {
			Future<Integer> future = executor.submit(() -> service.countEmails(filePath, domain));
			results.put(domain, future);
		}
		
		executor.shutdown();
		
		for (Map.Entry<String, Future<Integer>> entry : results.entrySet()) {
			try {
				String domain = entry.getKey();
				int count = entry.getValue().get(); // Wait for every result
				System.out.printf("📬 %s: %d\n", domain, count);
			} catch (ExecutionException e) {
				System.out.printf("❌ Error processing %s: %s\n", entry.getKey(), e.getCause());
			}
		}
		
		long duration = System.currentTimeMillis() - start;
		System.out.printf("⏱️ Total overall time: %d ms\n", duration);
	}
}
