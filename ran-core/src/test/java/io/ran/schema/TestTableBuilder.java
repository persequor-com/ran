/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.schema;

import io.ran.Property;
import io.ran.token.ColumnToken;
import io.ran.token.IndexToken;
import io.ran.token.Token;

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
		return (t, ca) -> {
			return (t.getType() == TableActionType.MODIFY ? "ADD COLUMN " : "") + ca.getName() + " " + ca.getType().getSimpleName().toLowerCase();
		};
	}

	@Override
	protected ColumnActionDelegate modify() {
		return (t, ca) -> {
			return "COLUMN " + ca.getName() + " " + ca.getType().getSimpleName().toLowerCase();
		};
	}

	@Override
	protected ColumnActionDelegate remove() {
		return (t, ca) -> {
			return "DROP COLUMN " + ca.getName();
		};
	}

	@Override
	protected IndexActionDelegate createIndex() {
		return (t, ia) -> {
			String indexType = "INDEX ";
			if (ia.isPrimary()) {
				indexType = "PRIMARY KEY";
			} else if (ia.getProperty("isUnique").equals(true)) {
				indexType = "UNIQUE ";
			}
			return (t.getType() == TableActionType.MODIFY ? "ADD " : "") + (ia.isPrimary() ? "PRIMARY KEY " : indexType + ia.getName() + " ") + "(" + ia.getFields().join(", ") + ")";
		};
	}

	@Override
	protected IndexActionDelegate removeIndex() {
		return (t, ia) -> {
			return (ia.isPrimary() ? "DROP PRIMARY KEY" : "DROP INDEX " + ia.getName());
		};
	}
}
