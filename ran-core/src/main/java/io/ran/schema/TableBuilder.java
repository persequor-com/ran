/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.schema;

import io.ran.KeySet;
import io.ran.Property;
import io.ran.token.ColumnToken;
import io.ran.token.IndexToken;
import io.ran.token.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class TableBuilder<TB extends TableBuilder<TB, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>> implements ITableBuilder<TB, CB, IB> {
	List<OnTableAction> actions = new ArrayList<>();

	protected abstract CB getColumnBuilder(ColumnAction column);

	protected abstract IB getIndexBuilder(IndexAction indexAction);

	protected abstract ColumnToken getColumnToken(Token token);

	protected abstract IndexToken getIndexToken(Token token);

	protected abstract ColumnToken getColumnToken(Property property);

	protected abstract IndexToken getIndexToken(Property property);

	protected abstract ColumnActionDelegate create();

	protected abstract ColumnActionDelegate modify();

	protected abstract ColumnActionDelegate remove();

	protected abstract IndexActionDelegate createIndex();

	protected abstract IndexActionDelegate removeIndex();


	public TB addColumn(Property property) {
		ColumnAction column = new ColumnAction(getColumnToken(property.getToken()), property, property.getType().clazz, (t, ca) -> create().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		actions.add(column);
		return (TB) this;
	}

	public TB addColumn(Property property, Consumer<CB> consumer) {
		ColumnAction column = new ColumnAction(getColumnToken(property.getToken()), property, property.getType().clazz, (t, ca) -> create().execute(t, ca));
		CB columnBuilder = getColumnBuilder(column);
		consumer.accept(columnBuilder);
		actions.add(column);
		return (TB) this;
	}

	public TB addPrimaryKey(KeySet key) {
		IndexAction indexAction = new IndexAction(getIndexToken(Token.of("PRIMARY")), FormattingTokenList.ofProperties(this::getColumnToken, key.stream().map(KeySet.Field::getProperty).collect(Collectors.toList())), true, (t, ia) -> createIndex().execute(t, ia));
		actions.add(indexAction);
		return (TB) this;
	}

	public TB addPrimaryKey(List<Property> id) {
		IndexAction indexAction = new IndexAction(getIndexToken(Token.of("PRIMARY")), FormattingTokenList.ofProperties(this::getColumnToken, id), true, (t, ia) -> createIndex().execute(t, ia));
		actions.add(indexAction);
		return (TB) this;
	}

	public TB addPrimaryKey(Property... id) {
		return addPrimaryKey(Arrays.asList(id));
	}

	public TB addIndex(KeySet key) {
		IndexAction indexAction = new IndexAction(getIndexToken(Token.get(key.getName())), FormattingTokenList.ofProperties(this::getColumnToken, key.stream().map(KeySet.Field::getProperty).collect(Collectors.toList())), false, (t, ia) -> createIndex().execute(t, ia));
		actions.add(indexAction);
		return (TB) this;
	}

	public TB addIndex(Property name, List<Property> id) {
		IndexAction indexAction = new IndexAction(getIndexToken(name), FormattingTokenList.ofProperties(this::getColumnToken, id), false, (t, ia) -> createIndex().execute(t, ia));
		actions.add(indexAction);
		return (TB) this;
	}

	public TB addIndex(Property name, Property... id) {
		return addIndex(name, Arrays.asList(id));
	}

	public TB addIndex(Property name, Consumer<IB> consumer) {
		IndexAction indexAction = new IndexAction(getIndexToken(name), new FormattingTokenList<>(), false, (t, ia) -> createIndex().execute(t, ia));
		IB indexBuilder = getIndexBuilder(indexAction);
		consumer.accept(indexBuilder);
		actions.add(indexAction);
		return (TB) this;
	}


}
