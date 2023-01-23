/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

public class KeyInfo {
	private boolean primary;
	private Property<?> property;
	private String name;
	private int order;
	private boolean unique;

	public KeyInfo(boolean primary, Property<?> property, String name, int order, boolean unique) {
		this.primary = primary;
		this.property = property;
		this.name = name;
		this.order = order;
		this.unique = unique;
	}

	public boolean isPrimary() {
		return primary;
	}

	public Property<?> getProperty() {
		return property;
	}

	public int order() {
		return order;
	}

	public String getMapKey() {
		return Boolean.toString(primary) + name;
	}

	public String getName() {
		return name;
	}

	public boolean isUnique() {
		return unique || primary;
	}
}
