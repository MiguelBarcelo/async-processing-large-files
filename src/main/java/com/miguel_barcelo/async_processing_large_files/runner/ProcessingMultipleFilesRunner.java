package com.miguel_barcelo.async_processing_large_files.runner;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.miguel_barcelo.async_processing_large_files.service.EmailCounterService;
import com.miguel_barcelo.async_processing_large_files.util.AppConstants;

@Component
@Order(100)
public class ProcessingMultipleFilesRunner implements CommandLineRunner {
	
	private final EmailCounterService service;
	
	public ProcessingMultipleFilesRunner(EmailCounterService service) {
		this.service = service;
	}

	@Override
	public void run(String... args) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(AppConstants.FILES.length);
		String baseDir = System.getProperty("user.dir");
		
		List<Callable<Void>> tasks = new ArrayList<>();
		
		for (String fileName: AppConstants.FILES) {
			String path = Paths.get(baseDir, fileName).toString();
			tasks.add(() -> {
				processFile(path);
				return null;
			});
		}
		
		executor.invokeAll(tasks);
		executor.shutdown();
	}
	
	private void processFile(String filePath) {
		ExecutorService executor = Executors.newFixedThreadPool(AppConstants.DOMAINS.length);
		Map<String, Future<Integer>> domainTasks = new HashMap<>();
		
		StringBuilder log = new StringBuilder();
		log.append("📄 Processing file: ").append(filePath).append("\n");
		
		try {	
			long start = System.currentTimeMillis();
			
			for (String domain: AppConstants.DOMAINS) {
				domainTasks.put(domain, executor.submit(() -> service.countEmails(filePath, domain)));
			}
			
			for (String domain: AppConstants.DOMAINS) {
				int total = getEmailCount(domainTasks.get(domain), domain, log);
				if (total >= 0) {
					log.append(String.format("📬 %s: %d\n", domain, total));
				}
			}
			
			long duration = System.currentTimeMillis() - start;
			log.append(String.format("⏱️ Time: %d ms\n\n", duration));
		} finally {
			executor.shutdown();
			try {
				executor.awaitTermination(10, TimeUnit.SECONDS);				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.append(String.format("⚠️ Termination interrupted: %s", e.getMessage()));
			}
		}
		
		System.out.print(log.toString());
	}
	
	private int getEmailCount(Future<Integer> future, String domain, StringBuilder log) {
		try {
			return future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.append(String.format("❌ Interrupted while counting %s: %s\n", domain, e.getMessage()));
		} catch (ExecutionException e) {
			log.append(String.format("❌ Error while counting %s: %s\n", domain, e.getCause().getMessage()));
		}
		
		return -1;
	}
}
