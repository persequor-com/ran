/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

import io.ran.Property;
import io.ran.token.ColumnToken;

import java.util.HashMap;
import java.util.function.BiFunction;

public class ColumnAction implements OnTableAction {
	private ColumnToken name;
	private Class type;
	private BiFunction<TableAction, ColumnAction, String> columnAction;
	private HashMap<String, Object> properties = new HashMap<>();
	private Property property;

	public ColumnAction(ColumnToken name, Property property, Class type, BiFunction<TableAction, ColumnAction, String> columnAction) {
		this.name = name;
		this.type = type;
		this.columnAction = columnAction;
		this.property = property;
	}

	public ColumnToken getName() {
		return name;
	}

	public Class getType() {
		return type;
	}

	public BiFunction<TableAction, ColumnAction, String> getColumnAction() {
		return columnAction;
	}

	public void setProperty(String property, Object value) {
		properties.put(property, value);
	}

	@Override
	public String apply(TableAction tableAction, OnTableAction ca) {
		return columnAction.apply(tableAction, (ColumnAction) ca);
	}

	public Property getProperty() {
		return property;
	}
}
