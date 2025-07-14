package com.miguel_barcelo.async_processing_large_files.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class LargeCsvGenerator {

	private static final String[] NAMES = { "Ana", "Bruno", "Carla", "Diego", "Elena", "Fabio", "Gina", "Hugo" };	
	private static final String[] DOMAINS = { "gmail.com", "yahoo.com", "hotmail.com" };
	
	public static void generateCsv(String filePath, int numLines) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			writer.write("id,name,email\n");
			Random random = new Random();
			
			for (int i = 1; i <= numLines; i++) {
				String name = NAMES[random.nextInt(NAMES.length)];
				String domain = DOMAINS[random.nextInt(DOMAINS.length)];
				String email = name.toLowerCase() + i + "@" + domain;
				writer.write(i + "," + name + "," + email + "\n");
			}
			
			System.out.println("✅ Generated CSV file: " + filePath);
		} catch (IOException e) {
			System.out.println("❌ Error generating CSV file: " + e.getMessage());
		}
	}
}
