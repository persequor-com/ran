package io.ran.schema;

import io.ran.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableAction {
	private List<ColumnAction> columns = new ArrayList<>();
	private List<IndexAction> indexActions = new ArrayList<>();
	private Token name;
	private Function<TableAction, String> action;
	private TableActionType type;

	public TableAction(Token name, TableActionType type, Function<TableAction, String> action) {
		this.type = type;
		this.name = name;
		this.action = action;
	}

	public List<ColumnAction> getColumns() {
		return columns;
	}

	public List<IndexAction> getIndexActions() {
		return indexActions;
	}

	public Token getName() {
		return name;
	}

	public void addColumn(ColumnAction column) {
		columns.add(column);
	}

	public void addIndex(IndexAction indexAction) {
		indexActions.add(indexAction);
	}

	public Function<TableAction, String> getAction() {
		return action;
	}

	public List<String> getActions() {
		List<String> tableActions = new ArrayList<>();
		getColumns().stream().map(ca -> ca.getColumnAction().apply(this,ca)).forEach(tableActions::add);
		getIndexActions().stream().map(ia -> ia.getAction().apply(this, ia)).forEach(tableActions::add);
		return tableActions;
	}

	public TableActionType getType() {
		return type;
	}
}
