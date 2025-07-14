package com.miguel_barcelo.async_processing_large_files.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.miguel_barcelo.async_processing_large_files.util.LargeCsvGenerator;

@Component
public class CsvInitRunner implements CommandLineRunner {

	@Override
	public void run(String... args) {
		String filePath = System.getProperty("user.dir") + "/users.csv";
		int totalLines = 100_000;
		LargeCsvGenerator.generateCsv(filePath, totalLines);
	}
}
