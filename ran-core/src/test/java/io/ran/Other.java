/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import java.util.List;
import java.util.UUID;

@Mapper(dbType = TestDb.class)
public class Other {
	private UUID id;
	@Relation(collectionElementType = TestClass.class)
	private List<TestClass> testClasses;
	private MyEnum myEnum;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<TestClass> getTestClasses() {
		return testClasses;
	}

	public void setTestClasses(List<TestClass> testClasses) {
		this.testClasses = testClasses;
	}

	public MyEnum getMyEnum() {
		return myEnum;
	}

	public void setMyEnum(MyEnum myEnum) {
		this.myEnum = myEnum;
	}
}
