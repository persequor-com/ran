package io.ran.schema;

class TestSchemaBuilder extends SchemaBuilder<TestSchemaBuilder, TestTableBuilder, TestColumnBuilder, TestIndexBuilder, ITestTableBuilder> {
	public TestSchemaBuilder(SchemaExecutor executor) {
		super(executor);
	}

	@Override
	protected TestTableBuilder getTableBuilder() {
		return new TestTableBuilder();
	}

	@Override
	protected TableActionDelegate create() {
		return ta -> {
			return "CREATE TABLE "+ta.getName().snake_case()+" ("+ String.join(", ", ta.getActions()) +");";
		};
	}

	@Override
	protected TableActionDelegate modify() {
		return ta -> {
			return "ALTER TABLE "+ta.getName().snake_case()+" "+ String.join(", ", ta.getActions()) +";";
		};
	}

	@Override
	protected TableActionDelegate remove() {
		return ta -> {
			return "DROP TABLE "+ta.getName().snake_case()+";";
		};
	}
}
