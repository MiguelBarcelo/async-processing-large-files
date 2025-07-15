package com.miguel_barcelo.async_processing_large_files.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

@Service
public class EmailCounterService {

	private static final int BLOCK_SIZE = 10_000;
	private static final int THREADS = Runtime.getRuntime().availableProcessors();
	
	public int countGmailEmails(String filePath) throws IOException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		List<Callable<Integer>> tasks = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			List<String> block = new ArrayList<>(BLOCK_SIZE);
			boolean skipHeader = true;
			
			while ((line = reader.readLine()) != null) {
				if (skipHeader) {
					skipHeader = false;
					continue;
				}
				
				block.add(line);
				
				if (block.size() == BLOCK_SIZE) {
					List<String> batch = new ArrayList<>(block);
					tasks.add(() -> countGmailInBlock(batch));
					block.clear();
				}
			}
			
			if (!block.isEmpty()) {
				List<String> batch = new ArrayList<>(block);
				tasks.add(() -> countGmailInBlock(batch) );
			}
		}
		
		System.out.println("🧩 Total number of tasks created: " + tasks.size());
		System.out.println("🧵 Maximum number of allowed threads: " + THREADS);
		
		// Executing in parallel
		List<Future<Integer>> futures = executor.invokeAll(tasks);
		executor.shutdown();
		
		// Aggregate results
		int total = 0;
		for (Future<Integer> future: futures) {
			try {
				total += future.get();
			} catch (ExecutionException e) {
				System.out.println("❌ Error in task: " + e.getCause());
			}
		}
		
		return total;
	}
	
	private int countGmailInBlock(List<String> block) {
		System.out.printf("🔧 thread: %s, block size: %d\n", Thread.currentThread().getName(), block.size());
		
		int count = 0;
		for (String line : block) {
			String[] parts = line.split(",");
			if (parts.length >= 3 && parts[2].endsWith("@gmail.com")) {
				count++;
			}
		}
		
		return count;
	}
}
