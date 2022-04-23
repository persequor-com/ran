package io.ran.schema;

import io.ran.Property;
import io.ran.token.Token;

public class IndexBuilder<IB extends IndexBuilder<IB>> {
	private TableBuilder<?, ?, IB> tableBuilder;
	protected IndexAction action;

	public IndexBuilder(TableBuilder<?,?, IB> tableBuilder, IndexAction action) {
		this.tableBuilder = tableBuilder;
		this.action = action;
	}

	public void addField(Property field) {
		action.getFields().add(tableBuilder.getColumnToken(field));
	}
}
