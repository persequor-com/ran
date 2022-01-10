package io.ran.schema;

import io.ran.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IndexAction {
	private String name;
	private boolean isPrimary = false;
	private List<Token> fields = new ArrayList<>();
	private BiFunction<TableAction, IndexAction, String> action;
	private HashMap<String, Object> properties = new HashMap<>();

	public IndexAction(String name, List<Token> fields, boolean isPrimary, BiFunction<TableAction, IndexAction, String> action) {
		this.name = name;
		this.fields = fields;
		this.isPrimary = isPrimary;
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public List<Token> getFields() {
		return fields;
	}

	public BiFunction<TableAction, IndexAction, String> getAction() {
		return action;
	}

	public void addProperty(String name, Object value) {
		properties.put(name, value);
	}
	public Object getProperty(String name) {
		return properties.getOrDefault(name, new Object());
	}
}