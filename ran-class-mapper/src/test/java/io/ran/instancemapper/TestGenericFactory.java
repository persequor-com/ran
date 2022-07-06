package io.ran.instancemapper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.ran.*;

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

	@Override
	public <T> T wrapped(Class<T> aClass) {
		return null;
	}


}
