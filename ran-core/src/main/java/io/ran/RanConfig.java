package io.ran;

import java.io.File;
import java.io.IOException;

public class RanConfig {
	public static boolean enableRanClassesDebugging = false;
	public static String projectBasePath;

	static {
		try {
			projectBasePath = new File("./").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
