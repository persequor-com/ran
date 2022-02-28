package io.ran;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TypeDescriberFactory {

	private final Map<Class, TypeDescriber> describers = Collections.synchronizedMap(new HashMap<>());
	private final AutoMapper autoMapper;

	@Inject
	public TypeDescriberFactory(AutoMapper autoMapper) {
		this.autoMapper = autoMapper;
	}

	public <X> TypeDescriber<X> getTypeDescriber(Class<X> tClass) {
		return describers.computeIfAbsent(tClass, c -> new TypeDescriberImpl<>(Clazz.of(tClass), autoMapper));
	}
}
