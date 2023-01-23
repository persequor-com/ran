/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

import io.ran.token.TableToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableAction {
	private List<OnTableAction> actions = new ArrayList<>();
	private TableToken name;
	private Function<TableAction, String> action;
	private TableActionType type;

	public TableAction(TableToken name, TableActionType type, Function<TableAction, String> action) {
		this.type = type;
		this.name = name;
		this.action = action;
	}

	public List<OnTableAction> getColumns() {
		return actions;
	}

	public TableToken getName() {
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
		actions.stream().map(ca -> ca.apply(this, ca)).forEach(tableActions::add);
		return tableActions;
	}

	public TableActionType getType() {
		return type;
	}

	public void addAction(OnTableAction onTableAction) {
		actions.add(onTableAction);
	}
}
