/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import java.util.UUID;

@Mapper(dbType = TestDb.class)
public class MyWithCompoundKey {
	@PrimaryKey
	private String first;
	@PrimaryKey
	private UUID second;
	private String value;

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public UUID getSecond() {
		return second;
	}

	public void setSecond(UUID second) {
		this.second = second;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
