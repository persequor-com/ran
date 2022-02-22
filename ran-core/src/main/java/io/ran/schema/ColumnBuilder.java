package io.ran.schema;

public abstract class ColumnBuilder<CB extends ColumnBuilder<CB>> {
	protected ColumnAction column;

	protected ColumnBuilder(ColumnAction column) {
		this.column = column;
	}

	public void notNull() {
		column.setProperty("nullable", false);
	}

	public void allowsNull() {
		column.setProperty("nullable", true);
	}
}
