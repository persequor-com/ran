/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
