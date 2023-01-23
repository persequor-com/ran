/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
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
