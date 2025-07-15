package com.miguel_barcelo.async_processing_large_files.util;

import java.nio.file.Paths;
import java.util.Random;

public class AppConstants {
	
	public static final String USERS_FILE_PATH = Paths.get(System.getProperty("user.dir"), "users.csv").toString();
	
	public static final String[] DOMAINS = { "gmail.com", "yahoo.com", "hotmail.com" };
	
	public static final String[] FILES = { "users.csv", "users2.csv", "users3.csv" };
	
	private static Random random = new Random();
	
	// Avoid instantiation
	private AppConstants() {}
	
	public static String getRandomDomain() {
		return DOMAINS[random.nextInt(DOMAINS.length)];
	}
}
