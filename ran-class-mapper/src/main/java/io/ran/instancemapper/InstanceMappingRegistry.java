package io.ran.instancemapper;

import io.ran.GenericFactory;
import io.ran.QueryWrapper;
import io.ran.token.Token;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class InstanceMappingRegistry {
	Map<CacheTriple, Map<Token, BiConsumer<?, ?>>> classSpecificMappings = new ConcurrentHashMap<>();
	Map<CacheTriple, List<BiConsumer<?, ?>>> mappingsCache = new ConcurrentHashMap<>();
	private final GenericFactory genericFactory;

	@Inject
	public InstanceMappingRegistry(GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	public <FROM, TO> void putIfAbsent(Class<FROM> fromClass, Class<TO> toClass, Function<TO, ?> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(Object.class, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
	}

	public <FROM, TO> void putIfAbsent(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, Function<TO, ?> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(context, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
	}

	public <FROM, TO, FIELD> void putIfAbsent(Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, FIELD> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(Object.class, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
	}

	public <FROM, TO, FIELD> void putIfAbsent(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, FIELD> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(context, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
	}

	public <FROM, TO> void put(Class<FROM> fromClass, Class<TO> toClass, Function<TO, ?> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(Object.class, fromClass, toClass).put(getToken(toClass, field), mapper);
	}

	public <FROM, TO> void put(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, Function<TO, ?> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(context, fromClass, toClass).put(getToken(toClass, field), mapper);
	}

	public <FROM, TO, FIELD> void put(Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, FIELD> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(Object.class, fromClass, toClass).put(getToken(toClass, field), mapper);
	}

	public <FROM, TO, FIELD> void put(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, FIELD> field, BiConsumer<FROM, TO> mapper) {
		getMapForWriting(context, fromClass, toClass).put(getToken(toClass, field), mapper);
	}

	public <FROM, TO> List<BiConsumer<? super FROM, ? super TO>> getMappers(Class<?> fromClass, Class<?> toClass) {
		return getMappers(Object.class, fromClass, toClass);
	}

	public <FROM, TO> List<BiConsumer<? super FROM, ? super TO>> getMappers(Class<?> context, Class<?> fromClass, Class<?> toClass) {
		//noinspection unchecked,rawtypes
		return (List) mappingsCache.computeIfAbsent(new CacheTriple(context, fromClass, toClass), t -> {
			LinkedHashMap<Token, BiConsumer<?, ?>> result = new LinkedHashMap<>();
			List<Class<?>> contextHierarchy = getSortedClassHierarchy(context);
			List<Class<?>> toHierarchy = getSortedClassHierarchy(toClass);
			List<Class<?>> fromHierarchy = getSortedClassHierarchy(fromClass);

			for (Class<?> ctx : contextHierarchy) {
				for (Class<?> to : toHierarchy) {
					for (Class<?> from : fromHierarchy) {
						Map<Token, BiConsumer<?, ?>> mappings = classSpecificMappings.get(new CacheTriple(ctx, to, from));
						if (mappings != null) {
							mappings.forEach(result::putIfAbsent);
						}
					}
				}
			}
			return new ArrayList<>(result.values());
		});
	}

	private List<Class<?>> getSortedClassHierarchy(Class<?> c) {
		List<Class<?>> sortedHierarchy = new ArrayList<>();
		Class<?> work = c;
		do {
			sortedHierarchy.add(work);
		} while ((work = work.getSuperclass()) != null);
		return sortedHierarchy;
	}

	private <TO> Token getToken(Class<TO> toClass, BiConsumer<TO, ?> field) {
		TO qi = genericFactory.getQueryInstance(toClass);
		field.accept(qi, null);
		return ((QueryWrapper) qi).getCurrentProperty().getToken();
	}

	private <TO> Token getToken(Class<TO> toClass, Function<TO, ?> field) {
		TO qi = genericFactory.getQueryInstance(toClass);
		field.apply(qi);
		return ((QueryWrapper) qi).getCurrentProperty().getToken();
	}

	private <FROM, TO> Map<Token, BiConsumer<?,?>> getMapForWriting(Class<?> context, Class<FROM> fromClass, Class<TO> toClass) {
		mappingsCache.clear();
		return classSpecificMappings.computeIfAbsent(new CacheTriple(context, toClass, fromClass), cc -> new ConcurrentHashMap<>());
	}

	private static class CacheTriple {
		private final Class<?> context;
		private final Class<?> from;
		private final Class<?> to;

		private CacheTriple(Class<?> context, Class<?> from, Class<?> to) {
			this.context = context;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CacheTriple that = (CacheTriple) o;
			return Objects.equals(context, that.context) && Objects.equals(from, that.from) && Objects.equals(to, that.to);
		}

		@Override
		public int hashCode() {
			return Objects.hash(context, from, to);
		}
	}
}
