package io.ran.schema;

import io.ran.token.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class TableBuilder<TB extends TableBuilder<TB, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>> implements ITableBuilder<TB, CB, IB> {
	List<OnTableAction> actions = new ArrayList<>();

	protected abstract CB getColumnBuilder(ColumnAction column);
	protected abstract IB getIndexBuilder(IndexAction indexAction);
	protected abstract ColumnActionDelegate create();
	protected abstract ColumnActionDelegate modify();
	protected abstract ColumnActionDelegate remove();
	protected abstract IndexActionDelegate createIndex();
	protected abstract IndexActionDelegate removeIndex();

	public TB addColumn(Token token, Class type) {
		ColumnAction column = new ColumnAction(token, type, (t,ca) -> create().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		actions.add(column);
		return (TB) this;
	}

	public TB addColumn(Token token, Class type, Consumer<CB> consumer) {
		ColumnAction column = new ColumnAction(token, type, (t,ca) -> create().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		consumer.accept(columnBuilder);
		actions.add(column);
		return (TB) this;
	}


	public TB addPrimaryKey(Token... id) {
		IndexAction indexAction = new IndexAction("PRIMARY", Arrays.asList(id), true, (t,ia) -> createIndex().execute(t, ia));
		actions.add(indexAction);
		return (TB) this;
	}

	public TB addIndex(String name, Token... id) {
		IndexAction indexAction = new IndexAction(name, Arrays.asList(id), false, (t,ia) -> createIndex().execute(t, ia));
		actions.add(indexAction);
		return (TB) this;
	}

	public TB addIndex(String name, Consumer<IB> consumer) {
		IndexAction indexAction = new IndexAction(name, new ArrayList<>(), false, (t, ia) -> createIndex().execute(t, ia));
		IB indexBuilder = getIndexBuilder(indexAction);
		consumer.accept(indexBuilder);
		actions.add(indexAction);
		return (TB) this;
	}


}
