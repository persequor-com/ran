/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.schema;

import io.ran.Clazz;
import io.ran.Property;
import io.ran.token.Token;
import org.junit.Test;

import javax.sql.DataSource;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SchemaBuilderTest {
	TestExecutor executor = new TestExecutor();
	TestSchemaBuilder builder = new TestSchemaBuilder(executor);

	@Test
	public void buildSimpleSchema() {
		builder.addTable(Token.of("the", "table"), tb -> {
			tb.addColumn(Property.get(Token.get("id"), Clazz.of(UUID.class)));
			tb.addColumn(Property.get(Token.get("title"), Clazz.of(String.class)));
			tb.addPrimaryKey(Property.get(Token.get("id")));
		});
		builder.build();

		assertEquals("CREATE TABLE 'TheTable' ('id' uuid, 'title' string, PRIMARY KEY ('id'));", executor.result.toString());
	}

	@Test
	public void buildCompoundKeySchema() {
		builder.addTable(Token.get("TheTable"), tb -> {
			tb.addColumn(Property.get(Token.get("id"), Clazz.of(UUID.class)));
			tb.addColumn(Property.get(Token.get("title"), Clazz.of(String.class)));
			tb.addPrimaryKey(Property.get(Token.get("id")), Property.get(Token.get("title")));
		});
		builder.build();

		assertEquals("CREATE TABLE 'TheTable' ('id' uuid, 'title' string, PRIMARY KEY ('id', 'title'));", executor.result.toString());
	}

	@Test
	public void modifyTable_dropColumn() {
		builder.modifyTable(Token.get("TheTable"), tb -> {
			tb.modifyColumn(Property.get(Token.get("id"), Clazz.of(String.class)));
			tb.removeColumn(Token.get("title"));
			tb.dropPrimaryKey();
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' COLUMN 'id' string, DROP COLUMN 'title', DROP PRIMARY KEY;", executor.result.toString());
	}

	@Test
	public void modifyTable_addColumn() {
		builder.modifyTable(Token.get("TheTable"), tb -> {
			tb.addColumn(Property.get(Token.get("CreatedAt"), Clazz.of(ZonedDateTime.class)));
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' ADD COLUMN 'created_at' zoneddatetime;", executor.result.toString());
	}

	@Test
	public void modifyTable_addIndex() {
		builder.modifyTable(Token.get("the_table"), tb -> {
			tb.addIndex(Property.get(Token.get("my_index")), Property.get(Token.get("title")), Property.get(Token.get("TypeName")));
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' ADD INDEX 'myIndex' ('title', 'type_name');", executor.result.toString());
	}

	@Test
	public void modifyTable_addIndexWithCustomProperties() {
		builder.modifyTable(Token.get("theTable"), tb -> {
			tb.addIndex(Property.get(Token.get("myUnique")), ib -> {
				ib.addField(Property.get(Token.get("id1")));
				ib.addField(Property.get(Token.get("id2")));
				ib.isUnique();
			});
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' ADD UNIQUE 'myUnique' ('id1', 'id2');", executor.result.toString());
	}

	private static class TestExecutor implements SchemaExecutor {
		StringBuilder result = new StringBuilder();

		@Override
		public void execute(Collection<TableAction> values) {
			values.forEach(ta -> {
				result.append(ta.getAction().apply(ta));
			});
		}

		@Override
		public void execute(Collection<TableAction> tableActions, DataSource dataSourceToExecuteOn) {
			execute(tableActions);
		}
	}
}
