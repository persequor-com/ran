package io.ran.testclasses;

import io.ran.CrudRepositoryTestDoubleBase;
import io.ran.GenericFactory;
import io.ran.MappingHelper;
import io.ran.TestDoubleDb;

import javax.inject.Inject;

public class IsItsOwnKeyRepository extends CrudRepositoryTestDoubleBase<IsItsOwnKey, IsItsOwnKey> {
	@Inject
	public IsItsOwnKeyRepository(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
		super(genericFactory, IsItsOwnKey.class, IsItsOwnKey.class, mappingHelper, store);
	}
}
