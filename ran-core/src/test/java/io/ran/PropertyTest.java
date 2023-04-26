/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
