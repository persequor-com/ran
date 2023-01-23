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

import java.util.List;

@Mapper(dbType = TestDb.class)
public class BikeGear {
	@PrimaryKey
	int gearNum;
	@Relation(collectionElementType = Bike.class, via = BikeGearBike.class/*, fields = {"gear_num"}, relationFields = "gear_num"*/, autoSave = true)
	private List<Bike> bikes;

	public int getGearNum() {
		return gearNum;
	}

	public void setGearNum(int gearNum) {
		this.gearNum = gearNum;
	}

	public List<Bike> getBikes() {
		return bikes;
	}

	public void setBikes(List<Bike> bikes) {
		this.bikes = bikes;
	}
}
