package io.ran.testclasses;

import io.ran.Mapper;
import io.ran.PrimaryKey;
import io.ran.Relation;
import io.ran.TestDb;

@Mapper(dbType = TestDb.class)
public class BikeToBike {

	@PrimaryKey
	private String bike1Id;
	@PrimaryKey
	private String bike2Id;

	@Relation(relationFields = "id", fields = "bike1Id")
	private Bike bike1;
	@Relation(relationFields = "id", fields = "bike2Id")
	private Bike bike2;

	public String getBike1Id() {
		return bike1Id;
	}

	public void setBike1Id(String bike1Id) {
		this.bike1Id = bike1Id;
	}

	public String getBike2Id() {
		return bike2Id;
	}

	public void setBike2Id(String bike2Id) {
		this.bike2Id = bike2Id;
	}

	public Bike getBike1() {
		return bike1;
	}

	public void setBike1(Bike bike1) {
		this.bike1 = bike1;
	}

	public Bike getBike2() {
		return bike2;
	}

	public void setBike2(Bike bike2) {
		this.bike2 = bike2;
	}
}
