/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.testclasses.IsItsOwnKey;
import io.ran.testclasses.IsItsOwnKeyRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IsItsOwnKeyTest {
	private GuiceHelper helper;
	private IsItsOwnKeyRepository repo;

	@Before
	public void setup() {
		helper = new GuiceHelper();
		TestDoubleDb store = new TestDoubleDb(new MappingHelper(helper.factory));
		repo = new IsItsOwnKeyRepository(helper.factory, new MappingHelper(helper.factory), store);
	}

	@Test
	public void happy() {
		IsItsOwnKey o = helper.factory.get(IsItsOwnKey.class);
		o.setKey1("k1");
		o.setKey2("k2");
		repo.save(o);

		IsItsOwnKey res = repo.get(o).orElseThrow(RuntimeException::new);
		assertEquals("k1", res.getKey1());
		assertEquals("k2", res.getKey2());
	}
}
