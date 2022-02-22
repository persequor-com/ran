package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.TestDb;

@Mapper(dbType = TestDb.class)
public class BikeGear {
	@PrimaryKey
	int gearNum;

	public int getGearNum() {
		return gearNum;
	}

	public void setGearNum(int gearNum) {
		this.gearNum = gearNum;
	}
}
