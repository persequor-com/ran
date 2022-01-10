package io.ran.schema;

import io.ran.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SchemaBuilder<SB extends SchemaBuilder<SB, TB, CB, IB, ITB>, TB extends TableBuilder<TB, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>, ITB extends ITableBuilder<TB, CB, IB>> {
	private SchemaExecutor executor;

	public SchemaBuilder(SchemaExecutor executor) {
		this.executor = executor;
	}

	private List<TableAction> tableActions = new ArrayList<>();

	abstract protected TB getTableBuilder();
	protected abstract TableActionDelegate create();
	protected abstract TableActionDelegate modify();
	protected abstract TableActionDelegate remove();

	public SB addTable(Token name, Consumer<ITB> consumer) {
		TableAction table = new TableAction(name, TableActionType.CREATE, ta -> create().execute(ta));
		TB tableBuilder = getTableBuilder();
		consumer.accept((ITB) tableBuilder);
		tableBuilder.actions.forEach(table::addAction);
		tableActions.add(table);
		return (SB) this;
	}

	public SB modifyTable(Token name, Consumer<TB> consumer) {
		TableAction table = new TableAction(name, TableActionType.MODIFY, ta -> modify().execute(ta));
		TB tableBuilder = getTableBuilder();
		consumer.accept(tableBuilder);
		tableBuilder.actions.forEach(table::addAction);
		tableActions.add(table);

		return (SB) this;
	}

	public SB removeTable(Token name) {
		TableAction table = new TableAction(name, TableActionType.REMOVE, ta -> remove().execute(ta));
		TB tableBuilder = getTableBuilder();
		tableBuilder.actions.forEach(table::addAction);
		tableActions.add(table);
		return (SB) this;
	}

	public void build() {
		executor.execute(tableActions);
	}
}
