/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

@Mapper(dbType = TestDbType.class)
public class HeadLights {
	@PrimaryKey
	private String on;

	public String getOn() {
		return on;
	}

	public void setOn(String on) {
		this.on = on;
	}

	@Override
	public String toString() {
		return "HeadLights{" +
				"on='" + on + '\'' +
				'}';
	}
}
