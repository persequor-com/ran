package io.ran;

public class KeyInfo {
	private boolean primary;
	private Property<?> property;
	private String name;
	private int order;

	public KeyInfo(boolean primary, Property<?> property, String name, int order) {
		this.primary = primary;
		this.property = property;
		this.name = name;
		this.order = order;
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
		return Boolean.toString(primary)+name;
	}
}
