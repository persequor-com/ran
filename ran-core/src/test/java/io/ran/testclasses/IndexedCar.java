/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran.testclasses;

import io.ran.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mapper(dbType = TestDbType.class)
public class IndexedCar {

	@Key(name = "title_idx")
	private String title;
	@PrimaryKey
	private String id;
	private ZonedDateTime constructionDate;
	private UUID engineId;
	private Brand brand;
	private Double crashRating;
	private Boolean canBeSoldInEu;
	private boolean theBoolean;
	@Relation
	private IndexedEngine engine;
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

	public IndexedEngine getEngine() {
		return engine;
	}

	public void setEngine(IndexedEngine engine) {
		if(engine != null) {
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
