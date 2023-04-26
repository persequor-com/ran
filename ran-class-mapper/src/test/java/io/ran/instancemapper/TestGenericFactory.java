package io.ran.instancemapper;

import io.ran.AutoMapper;
import io.ran.DbResolver;
import io.ran.DbType;
import io.ran.GenericFactory;

public class TestGenericFactory implements GenericFactory {

	@Override
	public <T> T get(Class<T> clazz) {
		try {
			return AutoMapper.get(clazz).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T getQueryInstance(Class<T> clazz) {
		try {
			AutoMapper.get(clazz);
			return AutoMapper.getQueryMaps(clazz).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DbResolver<DbType> getResolver(Class<? extends DbType> dbTypeClass) {
		return null;
	}
}
