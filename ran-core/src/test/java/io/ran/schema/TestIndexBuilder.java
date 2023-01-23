/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.schema;

public class TestIndexBuilder extends IndexBuilder<TestIndexBuilder> {
	public TestIndexBuilder(TestTableBuilder tableBuilder, IndexAction action) {
		super(tableBuilder, action);
	}

	public void isUnique() {
		action.addProperty("isUnique", true);
	}
}
