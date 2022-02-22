package io.ran.schema;

public class TestIndexBuilder extends IndexBuilder<TestIndexBuilder> {
	public TestIndexBuilder(TestTableBuilder tableBuilder, IndexAction action) {
		super(tableBuilder, action);
	}

	public void isUnique() {
		action.addProperty("isUnique", true);
	}
}
