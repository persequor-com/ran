/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
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

	TB addPrimaryKey(Property... id);

	TB addIndex(Property name, Property... id);

	TB addIndex(Property name, Consumer<IB> consumer);

	TB addIndex(KeySet key);
}
