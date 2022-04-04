package io.ran.schema;

import io.ran.KeySet;
import io.ran.Property;
import io.ran.token.Token;

import java.util.Arrays;
import java.util.function.Consumer;

public interface ITableBuilder<TB extends ITableBuilder<TB, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>> {
	TB addColumn(Property property);
	TB addColumn(Property property, Consumer<CB> consumer);
	TB addPrimaryKey(KeySet key);
	TB addPrimaryKey(Token... id);
	TB addIndex(Token name, Token... id);
	TB addIndex(Token name, Consumer<IB> consumer);
	TB addIndex(KeySet key);
}
