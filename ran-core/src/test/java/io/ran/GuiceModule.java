/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import org.mockito.Mockito;

public class GuiceModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Resolver.class).to(ResolverImpl.class);
		bind(GenericFactory.class).to(GuiceHelper.GuiceGenericFactory.class);
		bind(new TypeLiteral<DbResolver<TestDbType>>(){}).to(TestResolver.class);
	}

	@Provides
	@Singleton
	public TestDoubleDb provideTestDoubleDb(MappingHelper mappingHelper) {
		return Mockito.spy(new TestDoubleDb(mappingHelper));
	}
}
