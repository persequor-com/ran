/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

import io.ran.TypeDescriber;
import io.ran.token.TableToken;
import io.ran.token.Token;

class TestSchemaBuilder extends SchemaBuilder<TestSchemaBuilder, TestTableBuilder, TestColumnBuilder, TestIndexBuilder, ITestTableBuilder> {
	public TestSchemaBuilder(SchemaExecutor executor) {
		super(executor);
	}

	@Override
	protected TestTableBuilder getTableBuilder() {
		return new TestTableBuilder();
	}

	@Override
	protected TableToken getTableToken(Token token) {
		return new TestTableToken(token);
	}

	@Override
	protected TableToken getTableToken(TypeDescriber<?> typeDescriber) {
		return getTableToken(Token.CamelCase(typeDescriber.clazz().getSimpleName()));
	}

	@Override
	protected TableActionDelegate create() {
		return ta -> {
			return "CREATE TABLE " + ta.getName() + " (" + String.join(", ", ta.getActions()) + ");";
		};
	}

	@Override
	protected TableActionDelegate modify() {
		return ta -> {
			return "ALTER TABLE " + ta.getName() + " " + String.join(", ", ta.getActions()) + ";";
		};
	}

	@Override
	protected TableActionDelegate remove() {
		return ta -> {
			return "DROP TABLE " + ta.getName() + ";";
		};
	}
}
