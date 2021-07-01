package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.Relation;
import io.ran.TestDbType;

@Mapper(dbType = TestDbType.class)
public class Door {
	private String id;
	private String carId;
	@Relation()
	private transient Car car;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
		this.carId = car.getId();
	}
}
