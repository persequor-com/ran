/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

import io.ran.Property;
import io.ran.token.Token;

import java.util.function.Consumer;

public abstract class TableModifier<TM extends TableModifier<TM, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>> extends TableBuilder<TM, CB, IB> implements ITableBuilder<TM, CB, IB> {

	public TM modifyColumn(Property property) {
		ColumnAction column = new ColumnAction(getColumnToken(property.getToken()), property, property.getType().clazz, (t, ca) -> modify().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		actions.add(column);
		return (TM) this;
	}

	public TM modifyColumn(Property property, Consumer<CB> consumer) {
		ColumnAction column = new ColumnAction(getColumnToken(property.getToken()), property, property.getType().clazz, (t, ca) -> modify().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		consumer.accept(columnBuilder);
		actions.add(column);
		return (TM) this;
	}

	public TM removeColumn(Token token) {
		ColumnAction column = new ColumnAction(getColumnToken(token), null, null, (t, ca) -> remove().execute(t, ca));
		actions.add(column);
		return (TM) this;
	}


	public TM dropIndex(Token name) {
		IndexAction indexAction = new IndexAction(getIndexToken(name), FormattingTokenList.empty(), false, (t, ia) -> removeIndex().execute(t, ia));
		actions.add(indexAction);
		return (TM) this;
	}


	public TM dropPrimaryKey() {
		IndexAction indexAction = new IndexAction(getIndexToken(Token.of("PRIMARY")), FormattingTokenList.empty(), true, (t, ia) -> removeIndex().execute(t, ia));
		actions.add(indexAction);
		return (TM) this;
	}

}
