/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

class TestColumnBuilder extends ColumnBuilder<TestColumnBuilder> {
	public TestColumnBuilder(ColumnAction column) {
		super(column);
	}

	public void isTest() {
		this.column.setProperty("isTest", true);
	}
}
