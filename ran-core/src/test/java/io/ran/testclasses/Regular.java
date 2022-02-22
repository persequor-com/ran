package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.TestDbType;

@Mapper(dbType = TestDbType.class)
public class Regular extends Super {
	@PrimaryKey
	private String reg;

	public String getReg() {
		return reg;
	}

	public void setReg(String reg) {
		this.reg = reg;
	}
}
