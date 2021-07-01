package io.ran;

import io.ran.testclasses.IsItsOwnKey;
import io.ran.testclasses.IsItsOwnKeyRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class IsItsOwnKeyTest {
	private GuiceHelper helper;
	private IsItsOwnKeyRepository repo;
	private TestDoubleDb store;

	@Before
	public void setup() {
		store = new TestDoubleDb();
		helper = new GuiceHelper();
		repo = new IsItsOwnKeyRepository(helper.factory, new MappingHelper(helper.factory), store);
	}

	@Test
	public void happy() {
		IsItsOwnKey o = helper.factory.get(IsItsOwnKey.class);
		o.setKey1("k1");
		o.setKey2("k2");
		repo.save(o);

		IsItsOwnKey res = repo.get(o).orElseThrow(() -> new RuntimeException());
		assertEquals(o, res);
	}
}
