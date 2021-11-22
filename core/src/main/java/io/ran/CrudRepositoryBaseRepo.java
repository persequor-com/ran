package io.ran;

public interface CrudRepositoryBaseRepo<T, K, Q extends CrudRepository.InlineQuery<T,Q>> extends CrudRepository<T, K> {
	Q query();
}
