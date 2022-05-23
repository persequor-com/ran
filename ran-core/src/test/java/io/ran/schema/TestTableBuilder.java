package io.ran.schema;

import io.ran.Property;
import io.ran.token.ColumnToken;
import io.ran.token.IndexToken;
import io.ran.token.Token;

import java.util.stream.Collectors;

class TestTableBuilder extends TableModifier<TestTableBuilder, TestColumnBuilder, TestIndexBuilder> implements ITestTableBuilder {

	@Override
	protected TestColumnBuilder getColumnBuilder(ColumnAction column) {
		return new TestColumnBuilder(column);
	}

	@Override
	protected TestIndexBuilder getIndexBuilder(IndexAction indexAction) {
		return new TestIndexBuilder(this, indexAction);
	}

	@Override
	protected ColumnToken getColumnToken(Token token) {
		return new TestColumnToken(token);
	}

	@Override
	protected IndexToken getIndexToken(Token token) {
		return new TestIndexToken(token);
	}

	@Override
	protected ColumnToken getColumnToken(Property property) {
		return new TestColumnToken(property);
	}

	@Override
	protected IndexToken getIndexToken(Property property) {
		return new TestIndexToken(property);
	}

	@Override
	protected ColumnActionDelegate create() {
		return (t,ca) -> {
			return (t.getType() == TableActionType.MODIFY ? "ADD COLUMN " : "")+ca.getName()+" "+ca.getType().getSimpleName().toLowerCase();
		};
	}

	@Override
	protected ColumnActionDelegate modify() {
		return (t,ca) -> {
			return "COLUMN "+ca.getName()+" "+ca.getType().getSimpleName().toLowerCase();
		};
	}

	@Override
	protected ColumnActionDelegate remove() {
		return (t,ca) -> {
			return "DROP COLUMN "+ca.getName();
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
			return (t.getType() == TableActionType.MODIFY ? "ADD " : "")+(ia.isPrimary() ? "PRIMARY KEY ":indexType+ia.getName()+" ")+"("+ia.getFields().join(", ") +")";
		};
	}

	@Override
	protected IndexActionDelegate removeIndex() {
		return (t,ia) -> {
			return (ia.isPrimary() ? "DROP PRIMARY KEY":"DROP INDEX "+ia.getName());
		};
	}
}
