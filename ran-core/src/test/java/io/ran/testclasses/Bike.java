/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.testclasses;


import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.Relation;
import io.ran.TestDb;

import java.util.List;

@Mapper(dbType = TestDb.class)
public class Bike {
	@PrimaryKey
	private String id;
	private BikeType bikeType;
	private int wheelSize;
	@Relation(fields = {"bikeType", "wheelSize"}, relationFields = {"bikeType", "size"}, autoSave = true)
	private BikeWheel frontWheel;
	@Relation(fields = {"bikeType", "wheelSize"}, relationFields = {"bikeType", "size"}, autoSave = true)
	private BikeWheel backWheel;
	@Relation(collectionElementType = BikeGear.class, via = BikeGearBike.class, autoSave = true)
	private List<BikeGear> gears;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BikeType getBikeType() {
		return bikeType;
	}

	public void setBikeType(BikeType bikeType) {
		this.bikeType = bikeType;
	}

	public int getWheelSize() {
		return wheelSize;
	}

	public void setWheelSize(int wheelSize) {
		this.wheelSize = wheelSize;
	}

	public BikeWheel getFrontWheel() {
		return frontWheel;
	}

	public void setFrontWheel(BikeWheel frontWheel) {
		this.frontWheel = frontWheel;
	}

	public BikeWheel getBackWheel() {
		return backWheel;
	}

	public void setBackWheel(BikeWheel backWheel) {
		this.backWheel = backWheel;
	}

	public List<BikeGear> getGears() {
		return gears;
	}

	public void setGears(List<BikeGear> gears) {
		this.gears = gears;
	}
}
