package io.ran;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class RanConfig {
	private boolean enableRanClassesDebugging = true;
	private String projectBasePath = null;

	public RanConfig() {
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

	public boolean isEnableRanClassesDebugging() {
		return enableRanClassesDebugging;
	}

	public void setEnableRanClassesDebugging(boolean enableRanClassesDebugging) {
		this.enableRanClassesDebugging = enableRanClassesDebugging;
	}

	public String getProjectBasePath() {
		return projectBasePath;
	}

	public void setProjectBasePath(String projectBasePath) {
		this.projectBasePath = projectBasePath;
	}
}
