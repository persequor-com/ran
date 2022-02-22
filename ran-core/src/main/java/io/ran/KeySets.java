package io.ran;

import java.util.HashMap;
import java.util.Optional;

public class KeySets extends HashMap<String, KeySet> {
	public KeySet getPrimary() {
		return getPrimaryOptionally().orElseThrow(() -> new RuntimeException("Missing primary key in keysets"));
	}

	public Optional<KeySet> getPrimaryOptionally() {
		return values().stream().filter(KeySet::isPrimary).findFirst();
	}
}
