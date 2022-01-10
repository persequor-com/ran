package io.ran.schema;

import io.ran.token.Token;

import java.util.Collections;
import java.util.function.Consumer;

public abstract class TableModifier<TM extends TableModifier<TM, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>> extends TableBuilder<TM, CB, IB> implements ITableBuilder<TM, CB, IB> {

	public TM modifyColumn(Token token, Class type) {
		ColumnAction column = new ColumnAction(token, type, (t,ca) -> modify().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		columns.add(column);
		return (TM) this;
	}

	public TM modifyColumn(Token token, Class type, Consumer<CB> consumer) {
		ColumnAction column = new ColumnAction(token, type, (t,ca) -> modify().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		consumer.accept(columnBuilder);
		columns.add(column);
		return (TM) this;
	}

	public TM removeColumn(Token token) {
		ColumnAction column = new ColumnAction(token, null, (t,ca) -> remove().execute(t, ca));
		columns.add(column);
		return (TM) this;
	}


	public TM dropIndex(String name) {
		IndexAction indexAction = new IndexAction(name, Collections.emptyList(), false, (t, ia) -> removeIndex().execute(t, ia));
		indices.add(indexAction);
		return (TM) this;
	}


	public TM dropPrimaryKey() {
		IndexAction indexAction = new IndexAction("PRIMARY", Collections.emptyList(), true, (t,ia) -> removeIndex().execute(t, ia));
		indices.add(indexAction);
		return (TM) this;
	}

}
