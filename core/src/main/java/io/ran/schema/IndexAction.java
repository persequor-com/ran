package io.ran.schema;

import io.ran.token.ColumnToken;
import io.ran.token.FormattingToken;
import io.ran.token.IndexToken;
import io.ran.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IndexAction implements OnTableAction {
	private IndexToken name;
	private boolean isPrimary = false;
	private FormattingTokenList<ColumnToken> fields = new FormattingTokenList<ColumnToken>();
	private BiFunction<TableAction, IndexAction, String> action;
	private HashMap<String, Object> properties = new HashMap<>();

	public IndexAction(IndexToken name, FormattingTokenList<ColumnToken> fields, boolean isPrimary, BiFunction<TableAction, IndexAction, String> action) {
		this.name = name;
		this.fields = fields;
		this.isPrimary = isPrimary;
		this.action = action;
	}

	public IndexToken getName() {
		return name;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public FormattingTokenList<ColumnToken> getFields() {
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

	@Override
	public String apply(TableAction tableAction, OnTableAction ca) {
		return action.apply(tableAction, (IndexAction) ca);
	}
}