package io.ran.schema;

class TestColumnBuilder extends ColumnBuilder<TestColumnBuilder> {
	public TestColumnBuilder(ColumnAction column) {
		super(column);
	}

	public void isTest() {
		this.column.setProperty("isTest", true);
	}
}
