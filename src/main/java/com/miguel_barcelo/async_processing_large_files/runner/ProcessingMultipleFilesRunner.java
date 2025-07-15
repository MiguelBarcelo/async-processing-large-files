package com.miguel_barcelo.async_processing_large_files.runner;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		StringBuilder log = new StringBuilder();
		log.append("📄 Processing file: ").append(filePath).append("\n");
		
		try {	
			long start = System.currentTimeMillis();
			
			for (String domain: AppConstants.DOMAINS) {
				int total = service.countEmails(filePath, domain);
				log.append(String.format("📬 %s: %d\n", domain, total));
			}
			
			long duration = System.currentTimeMillis() - start;
			log.append(String.format("⏱️ Time: %d ms\n\n", duration));
		} catch (Exception e) {
			log.append("❌ Error: ").append(e.getMessage()).append("\n");
		}
		
		System.out.print(log.toString());
	}
}
