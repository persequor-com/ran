package io.ran.schema;

import io.ran.token.Token;

import java.util.Arrays;
import java.util.function.Consumer;

public interface ITableBuilder<TB extends ITableBuilder<TB, CB, IB>, CB extends ColumnBuilder<CB>, IB extends IndexBuilder<IB>> {
	TB addColumn(Token token, Class type);
	TB addColumn(Token token, Class type, Consumer<CB> consumer);
	TB addPrimaryKey(Token... id);
	TB addIndex(Token name, Token... id);
	TB addIndex(Token name, Consumer<IB> consumer);
}
