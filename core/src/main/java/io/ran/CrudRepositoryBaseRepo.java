package io.ran;

public interface CrudRepositoryBaseRepo<T, K, Q extends CrudRepository.InlineQuery<T,Q>> extends CrudRepository<T, K> {
	Q query();
	default <O, QO extends CrudRepository.InlineQuery<Q, QO>> QO query(Class<O> oClass) {
		throw new RuntimeException("Querying other models is not yet supported by this crud repository");
	}
}
