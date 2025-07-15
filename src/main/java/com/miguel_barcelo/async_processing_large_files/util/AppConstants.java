package com.miguel_barcelo.async_processing_large_files.util;

import java.nio.file.Paths;

public class AppConstants {
	
	public static final String USERS_FILE_PATH = Paths.get(System.getProperty("user.dir"), "users.csv").toString();
	
	// Avoid instantiation
	private AppConstants() {}
}
