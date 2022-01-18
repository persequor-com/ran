package io.ran.schema;

import io.ran.token.Token;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SchemaBuilderTest {
	TestExecutor executor = new TestExecutor();
	TestSchemaBuilder builder = new TestSchemaBuilder(executor);

	@Test
	public void buildSimpleSchema() {
		builder.addTable(Token.of("the","table"), tb -> {
			tb.addColumn(Token.get("id"), UUID.class);
			tb.addColumn(Token.get("title"), String.class);
			tb.addPrimaryKey(Token.get("id"));
		});
		builder.build();

		assertEquals("CREATE TABLE 'TheTable' ('id' uuid, 'title' string, PRIMARY KEY ('id'));", executor.result.toString());
	}

	@Test
	public void buildCompoundKeySchema() {
		builder.addTable(Token.get("TheTable"), tb -> {
			tb.addColumn(Token.get("id"), UUID.class);
			tb.addColumn(Token.get("title"), String.class);
			tb.addPrimaryKey(Token.get("id"), Token.get("title"));
		});
		builder.build();

		assertEquals("CREATE TABLE 'TheTable' ('id' uuid, 'title' string, PRIMARY KEY ('id', 'title'));", executor.result.toString());
	}

	@Test
	public void modifyTable_dropColumn() {
		builder.modifyTable(Token.get("TheTable"), tb -> {
			tb.modifyColumn(Token.get("id"), String.class);
			tb.removeColumn(Token.get("title"));
			tb.dropPrimaryKey();
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' COLUMN 'id' string, DROP COLUMN 'title', DROP PRIMARY KEY;", executor.result.toString());
	}

	@Test
	public void modifyTable_addColumn() {
		builder.modifyTable(Token.get("TheTable"), tb -> {
			tb.addColumn(Token.get("CreatedAt"), ZonedDateTime.class);
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' ADD COLUMN 'created_at' zoneddatetime;", executor.result.toString());
	}

	@Test
	public void modifyTable_addIndex() {
		builder.modifyTable(Token.get("the_table"), tb -> {
			tb.addIndex(Token.get("my_index"), Token.get("title"), Token.get("TypeName"));
		});
		builder.build();

		assertEquals("ALTER TABLE 'TheTable' ADD INDEX 'myIndex' ('title', 'type_name');", executor.result.toString());
	}

	@Test
	public void modifyTable_addIndexWithCustomProperties() {
		builder.modifyTable(Token.get("theTable"), tb -> {
			tb.addIndex(Token.get("myUnique"), ib -> {
				ib.addField(Token.get("id1"));
				ib.addField(Token.get("id2"));
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
	}
}