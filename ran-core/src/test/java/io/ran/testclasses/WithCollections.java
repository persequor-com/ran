/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.TestDbType;

import java.util.List;
import java.util.Set;

@Mapper(dbType = TestDbType.class)
public class WithCollections {
	private List<String> id;
	private Set<String> field;

	public List<String> getId() {
		return id;
	}

	public void setId(List<String> id) {
		this.id = id;
	}

	public Set<String> getField() {
		return field;
	}

	public void setField(Set<String> field) {
		this.field = field;
	}
}
