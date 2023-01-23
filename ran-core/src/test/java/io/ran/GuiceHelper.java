/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

public class GuiceHelper {
	final Injector injector;
	final GenericFactory factory;

	public GuiceHelper() {
		GuiceModule module = new GuiceModule();
		injector = Guice.createInjector(module);
		factory = injector.getInstance(GuiceGenericFactory.class);
	}

	public static class GuiceGenericFactory implements GenericFactory, AutoWrapperGenericFactory {
		private AutoMapper ormMapper;
		private Injector injector;

		@Inject
		public GuiceGenericFactory(AutoMapper ormMapper, Injector injector) {
			this.ormMapper = ormMapper;
			this.injector = injector;
		}

		@Override
		public <T> T get(Class<T> clazz) {
			return (T) injector.getInstance(ormMapper.get(clazz));
		}

		@Override
		public <T> T getQueryInstance(Class<T> clazz) {
			return injector.getInstance(ormMapper.getQueryMaps(clazz));
		}

		@Override
		public DbResolver<DbType> getResolver(Class<? extends DbType> dbTypeClass) {
			Clazz<DbResolver<DbType>> clazz = Clazz.ofClazzes(DbResolver.class, Clazz.of(dbTypeClass));
			return (DbResolver<DbType>) injector.getInstance(Key.get(clazz.getType()));
		}

		@Override
		public <T> T wrapped(Class<T> aClass) {
			return injector.getInstance(aClass);
		}

	}
}
