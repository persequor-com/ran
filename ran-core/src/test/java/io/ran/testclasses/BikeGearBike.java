/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.Relation;
import io.ran.TestDb;

@Mapper(dbType = TestDb.class)
public class BikeGearBike {
	@PrimaryKey
	private String bikeId;
	@PrimaryKey
	private int gearNum;
	@Relation(relationFields = "id", fields = "bikeId")
	private Bike bike;
	@Relation(relationFields = "gear_num", fields = "gear_num")
	private BikeGear bikeGear;


	public String getBikeId() {
		return bikeId;
	}

	public void setBikeId(String bikeId) {
		this.bikeId = bikeId;
	}

	public int getGearNum() {
		return gearNum;
	}

	public void setGearNum(int gearNum) {
		this.gearNum = gearNum;
	}

	public Bike getBike() {
		return bike;
	}

	public void setBike(Bike bike) {
		this.bike = bike;
	}

	public BikeGear getBikeGear() {
		return bikeGear;
	}

	public void setBikeGear(BikeGear bikeGear) {
		this.bikeGear = bikeGear;
	}
}
