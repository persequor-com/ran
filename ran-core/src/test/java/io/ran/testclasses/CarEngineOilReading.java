/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.Relation;
import io.ran.TestDbType;

import java.util.UUID;

@Mapper(dbType = TestDbType.class)
public class CarEngineOilReading {
	@PrimaryKey
	private String carId;
	@PrimaryKey
	private UUID engineId;
	private double reading;
	@Relation
	private Engine engine;
	@Relation
	private Car car;

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public UUID getEngineId() {
		return engineId;
	}

	public void setEngineId(UUID engineId) {
		this.engineId = engineId;
	}

	public double getReading() {
		return reading;
	}

	public void setReading(double reading) {
		this.reading = reading;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
		this.engineId = engine.getId();
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
		this.carId = car.getId();
	}
}
