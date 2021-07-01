package io.ran;

import io.ran.testclasses.Car;

import java.util.UUID;

@Mapper(dbType = TestDbType.class)
public class Transmission {
	private UUID id;
	private String carId;
	private transient Car car;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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
	}
}
