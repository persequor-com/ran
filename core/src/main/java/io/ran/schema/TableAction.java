package io.ran.schema;

import io.ran.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableAction {
	private List<OnTableAction> actions = new ArrayList<>();
	private Token name;
	private Function<TableAction, String> action;
	private TableActionType type;

	public TableAction(Token name, TableActionType type, Function<TableAction, String> action) {
		this.type = type;
		this.name = name;
		this.action = action;
	}

	public List<OnTableAction> getColumns() {
		return actions;
	}

	public Token getName() {
		return name;
	}

	public void addColumn(ColumnAction column) {
		actions.add(column);
	}

	public void addIndex(IndexAction indexAction) {
		actions.add(indexAction);
	}

	public Function<TableAction, String> getAction() {
		return action;
	}

	public List<String> getActions() {
		List<String> tableActions = new ArrayList<>();
		actions.stream().map(ca -> ca.apply(this,ca)).forEach(tableActions::add);
		return tableActions;
	}

	public TableActionType getType() {
		return type;
	}

	public void addAction(OnTableAction onTableAction) {
		actions.add(onTableAction);
	}
}
