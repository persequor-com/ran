package io.ran.schema;

import io.ran.token.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SchemaBuilder<SB extends SchemaBuilder<SB, TB, CB, IB, ITB>, TB extends TableBuilder<TB, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>, ITB extends ITableBuilder<TB, CB, IB>> {
	private SchemaExecutor executor;

	public SchemaBuilder(SchemaExecutor executor) {
		this.executor = executor;
	}

	private Map<Token, TableAction> tables = new HashMap<>();

	abstract protected TB getTableBuilder();
	protected abstract TableActionDelegate create();
	protected abstract TableActionDelegate modify();
	protected abstract TableActionDelegate remove();

	public SB addTable(Token name, Consumer<ITB> consumer) {
		TableAction table = new TableAction(name, TableActionType.CREATE, ta -> create().execute(ta));
		TB tableBuilder = getTableBuilder();
		consumer.accept((ITB) tableBuilder);
		tableBuilder.columns.values().forEach(table::addColumn);
		tableBuilder.indices.values().forEach(table::addIndex);
		tables.put(name, table);
		return (SB) this;
	}

	public SB modifyTable(Token name, Consumer<TB> consumer) {
		TableAction table = new TableAction(name, TableActionType.MODIFY, ta -> modify().execute(ta));
		TB tableBuilder = getTableBuilder();
		consumer.accept(tableBuilder);
		tableBuilder.columns.values().forEach(table::addColumn);
		tableBuilder.indices.values().forEach(table::addIndex);
		tables.put(name, table);
		return (SB) this;
	}

	public SB removeTable(Token name) {
		TableAction table = new TableAction(name, TableActionType.REMOVE, ta -> remove().execute(ta));
		TB tableBuilder = getTableBuilder();
		tableBuilder.columns.values().forEach(table::addColumn);
		tableBuilder.indices.values().forEach(table::addIndex);
		tables.put(name, table);
		return (SB) this;
	}

	public void build() {
		executor.execute(tables.values());
	}
}
