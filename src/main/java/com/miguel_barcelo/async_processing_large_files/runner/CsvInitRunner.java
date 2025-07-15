package com.miguel_barcelo.async_processing_large_files.runner;

import java.nio.file.Paths;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.miguel_barcelo.async_processing_large_files.util.LargeCsvGenerator;

@Component
@Order(1)
public class CsvInitRunner implements CommandLineRunner {

	@Override
	public void run(String... args) {
		String filePath = Paths.get(System.getProperty("user.dir"), "users.csv").toString();
		int totalLines = 100_000;
		LargeCsvGenerator.generateCsv(filePath, totalLines);
	}
}
