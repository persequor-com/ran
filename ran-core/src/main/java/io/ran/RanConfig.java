package io.ran;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class RanConfig {
	public static boolean enableRanClassesDebugging = true;
	public static String projectBasePath = null;

	static {
		try {
			StringBuilder baseProjectPath = new StringBuilder("./");
			File baseProjectDir = new File(baseProjectPath.toString());
			while (Arrays.stream(Objects.requireNonNull(baseProjectDir.listFiles())).noneMatch(file -> file.getName().equals(".idea"))) {
				baseProjectPath.append("../");
				baseProjectDir = new File(baseProjectPath.toString());
				if (baseProjectDir.getCanonicalPath().equals("/")) {
					break;
				}
			}

			projectBasePath = baseProjectDir.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
