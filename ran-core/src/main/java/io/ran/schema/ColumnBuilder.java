/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

public abstract class ColumnBuilder<CB extends ColumnBuilder<CB>> {
	protected ColumnAction column;

	protected ColumnBuilder(ColumnAction column) {
		this.column = column;
	}

	public void notNull() {
		column.setProperty("nullable", false);
	}

	public void allowsNull() {
		column.setProperty("nullable", true);
	}
}
