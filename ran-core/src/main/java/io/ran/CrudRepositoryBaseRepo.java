/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

public interface CrudRepositoryBaseRepo<T, K, Q extends CrudRepository.InlineQuery<T, Q>> extends CrudRepository<T, K> {
	Q query();
}
