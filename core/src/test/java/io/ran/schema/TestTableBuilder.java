package io.ran.schema;

import io.ran.token.Token;

import java.util.stream.Collectors;

class TestTableBuilder extends TableModifier<TestTableBuilder, TestColumnBuilder, TestIndexBuilder> implements ITestTableBuilder {

	@Override
	protected TestColumnBuilder getColumnBuilder(ColumnAction column) {
		return new TestColumnBuilder(column);
	}

	@Override
	protected TestIndexBuilder getIndexBuilder(IndexAction indexAction) {
		return new TestIndexBuilder(indexAction);
	}

	@Override
	protected ColumnActionDelegate create() {
		return (t,ca) -> {
			return (t == TableActionType.MODIFY ? "ADD COLUMN " : "")+ca.getName().snake_case()+" "+ca.getType().getSimpleName().toLowerCase();
		};
	}

	@Override
	protected ColumnActionDelegate modify() {
		return (t,ca) -> {
			return "COLUMN "+ca.getName().snake_case()+" "+ca.getType().getSimpleName().toLowerCase();
		};
	}

	@Override
	protected ColumnActionDelegate remove() {
		return (t,ca) -> {
			return "DROP COLUMN "+ca.getName().snake_case();
		};
	}

	@Override
	protected IndexActionDelegate createIndex() {
		return (t,ia) -> {
			String indexType = "INDEX ";
			if (ia.isPrimary()) {
				indexType = "PRIMARY KEY";
			} else if (ia.getProperty("isUnique").equals(true)) {
				indexType = "UNIQUE ";
			}
			return (t == TableActionType.MODIFY ? "ADD " : "")+(ia.isPrimary() ? "PRIMARY KEY ":indexType+ia.getName()+" ")+"("+ia.getFields().stream().map(Token::snake_case).collect(Collectors.joining(", "))+")";
		};
	}

	@Override
	protected IndexActionDelegate removeIndex() {
		return (t,ia) -> {
			return (ia.isPrimary() ? "DROP PRIMARY KEY":"DROP INDEX "+ia.getName());
		};
	}
}
