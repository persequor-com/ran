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
