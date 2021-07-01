package io.ran;

import java.util.HashMap;

public class KeySets extends HashMap<String, KeySet> {
	public KeySet getPrimary() {
		return values().stream().filter(KeySet::isPrimary).findFirst().orElseThrow(() -> new RuntimeException("Missing primary key in keysets"));
	}
}
