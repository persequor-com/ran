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
