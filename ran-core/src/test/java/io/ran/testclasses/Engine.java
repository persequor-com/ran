/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.Relation;
import io.ran.TestDbType;

import java.util.Collection;
import java.util.UUID;

@Mapper(dbType = TestDbType.class)
public class Engine {
	private UUID id;
	private Brand brand;
	@Relation(collectionElementType = Car.class)
	private Collection<Car> cars;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Collection<Car> getCars() {
		return cars;
	}

	public void setCars(Collection<Car> cars) {
		this.cars = cars;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}
}
