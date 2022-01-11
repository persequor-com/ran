package io.ran.schema;

import io.ran.token.ColumnToken;
import io.ran.token.Token;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColumnAction implements OnTableAction {
	private ColumnToken name;
	private Class type;
	private BiFunction<TableAction, ColumnAction, String> columnAction;
	private HashMap<String, Object> properties = new HashMap<>();

	public ColumnAction(ColumnToken name, Class type, BiFunction<TableAction, ColumnAction, String> columnAction) {
		this.name = name;
		this.type = type;
		this.columnAction = columnAction;
	}

	public ColumnToken getName() {
		return name;
	}

	public Class getType() {
		return type;
	}

	public BiFunction<TableAction,ColumnAction, String> getColumnAction() {
		return columnAction;
	}

	public void setProperty(String property, Object value) {
		properties.put(property, value);
	}

	@Override
	public String apply(TableAction tableAction, OnTableAction ca) {
		return columnAction.apply(tableAction, (ColumnAction) ca);
	}
}