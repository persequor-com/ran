/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
