/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.testclasses;

import io.ran.HeadLights;
import io.ran.Mapper;
import io.ran.Relation;
import io.ran.TestDbType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mapper(dbType = TestDbType.class)
public class Car {

	private String title;
	private String id;
	private ZonedDateTime constructionDate;
	private UUID engineId;
	private Brand brand;
	private Double crashRating;
	private Boolean canBeSoldInEu;
	private boolean theBoolean;
	@Relation
	private Engine engine;
	@Relation(collectionElementType = Door.class)
	private List<Door> doors;
	@Relation(collectionElementType = CarEngineOilReading.class, fields = {"id", "engine_id"})
	private List<CarEngineOilReading> oilReadings;
	@Relation(fields = "id", relationFields = "on")
	private HeadLights headLights;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ZonedDateTime getConstructionDate() {
		return constructionDate;
	}

	public void setConstructionDate(ZonedDateTime constructionDate) {
		this.constructionDate = constructionDate;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		if (engine != null) {
			this.engineId = engine.getId();
		}
		this.engine = engine;
	}

	public UUID getEngineId() {
		return engineId;
	}

	public void setEngineId(UUID engineId) {
		this.engineId = engineId;
	}

	public List<Door> getDoors() {
		return doors;
	}

	public void setDoors(List<Door> doors) {
		this.doors = doors;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Double getCrashRating() {
		return crashRating;
	}

	public void setCrashRating(Double crashRating) {
		this.crashRating = crashRating;
	}

	public Boolean getCanBeSoldInEu() {
		return canBeSoldInEu;
	}

	public void setCanBeSoldInEu(Boolean canBeSoldInEu) {
		this.canBeSoldInEu = canBeSoldInEu;
	}

	public boolean isTheBoolean() {
		return theBoolean;
	}

	public void setTheBoolean(boolean theBoolean) {
		this.theBoolean = theBoolean;
	}

	public List<CarEngineOilReading> getOilReadings() {
		return oilReadings;
	}

	public void setOilReadings(List<CarEngineOilReading> oilReadings) {
		this.oilReadings = oilReadings;
	}

	public HeadLights getHeadLights() {
		return headLights;
	}

	public void setHeadLights(HeadLights headLights) {
		this.headLights = headLights;
	}
}
