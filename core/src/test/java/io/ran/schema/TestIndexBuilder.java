package io.ran.schema;

public class TestIndexBuilder extends IndexBuilder<TestIndexBuilder> {
	public TestIndexBuilder(IndexAction action) {
		super(action);
	}

	public void isUnique() {
		action.addProperty("isUnique", true);
	}
}
