/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyTest {
	@Test
	public void mapProperties_typeNameKeyFrom() {
		KeySet actual = Clazz.of(TypeNameKey.class).getProperties().mapProperties(Clazz.of(SingleKey.class).getProperties());

		assertEquals(1, actual.size());
		assertEquals("single_key_id", actual.get(0).getToken().snake_case());
	}

	@Test
	public void mapProperties_typeNameKeyTo() {
		KeySet actual = Clazz.of(SingleKey.class).getProperties().mapProperties(Clazz.of(TypeNameKey.class).getProperties());

		assertEquals(1, actual.size());
		assertEquals("id", actual.get(0).getToken().snake_case());
	}

	public static class SingleKey {
		@PrimaryKey
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	public static class MultiKey {
		@PrimaryKey
		private int id;
		@PrimaryKey
		private String other;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getOther() {
			return other;
		}

		public void setOther(String other) {
			this.other = other;
		}
	}

	public static class TypeNameKey {
		@PrimaryKey
		private int id;
		private int singleKeyId;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getSingleKeyId() {
			return singleKeyId;
		}

		public void setSingleKeyId(int singleKeyId) {
			this.singleKeyId = singleKeyId;
		}
	}

}