package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.TestDbType;

@Mapper(dbType = TestDbType.class)
public class IsItsOwnKey {
	@PrimaryKey
	private String key1;
	@PrimaryKey
	private String key2;

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}
}
