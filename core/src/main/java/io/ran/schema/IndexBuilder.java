package io.ran.schema;

import io.ran.token.Token;

public class IndexBuilder<IB extends IndexBuilder<IB>> {
	protected IndexAction action;

	public IndexBuilder(IndexAction action) {
		this.action = action;
	}

	public void addField(Token field) {
		action.getFields().add(field);
	}
}
