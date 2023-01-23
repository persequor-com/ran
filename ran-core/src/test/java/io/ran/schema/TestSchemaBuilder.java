/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
