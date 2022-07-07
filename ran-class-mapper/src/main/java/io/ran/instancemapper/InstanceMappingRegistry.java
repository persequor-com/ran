package io.ran.instancemapper;

import io.ran.*;
import io.ran.token.Token;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class InstanceMappingRegistry {
    Map<Class<?>, Map<Class<?>, Map<Class<?>, Map<Token, InstanceFieldMapper>>>> classSpecificMappings = new ConcurrentHashMap<>();
    Map<CacheTriple, List> mappingsCache = new ConcurrentHashMap<>();
    private GenericFactory genericFactory;

    @Inject
    public InstanceMappingRegistry(GenericFactory genericFactory) {
        this.genericFactory = genericFactory;
    }

    public <FROM, TO, V> void putIfAbsent(Class<FROM> fromClass, Class<TO> toClass, Function<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(Object.class, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
    }

    public <FROM, TO, V> void putIfAbsent(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, Function<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(context, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
    }

    public <FROM, TO, V> void putIfAbsent(Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(Object.class, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
    }

    public <FROM, TO, V> void putIfAbsent(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(context, fromClass, toClass).putIfAbsent(getToken(toClass, field), mapper);
    }


    public <FROM, TO, V> void put(Class<FROM> fromClass, Class<TO> toClass, Function<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(Object.class, fromClass, toClass).put(getToken(toClass, field), mapper);
    }

    public <FROM, TO, V> void put(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, Function<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(context, fromClass, toClass).put(getToken(toClass, field), mapper);
    }

    public <FROM, TO, V> void put(Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(Object.class, fromClass, toClass).put(getToken(toClass, field), mapper);
    }

    public <FROM, TO, V> void put(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, BiConsumer<TO, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        getMapForWriting(context, fromClass, toClass).put(getToken(toClass, field), mapper);
    }

    public <FROM, TO> List<InstanceFieldMapper<FROM, TO, ?>> getMappers(Class<FROM> fromClass, Class<TO> toClass) {
        return getMappers(Object.class, fromClass, toClass);
    }

    public <FROM, TO> List<InstanceFieldMapper<FROM, TO, ?>> getMappers(Class<?> context, Class<FROM> fromClass, Class<TO> toClass) {
        return (List)mappingsCache.computeIfAbsent(new CacheTriple(context, fromClass, toClass), t -> {
            LinkedHashMap<Token, InstanceFieldMapper<?, ?, ?>> result = new LinkedHashMap<>();
            List<Class<?>> fromHierarchy = getSortedClassHierarchy(fromClass);
            List<Class<?>> toHierarchy = getSortedClassHierarchy(toClass);
            List<Class<?>> contextHierarchy = getSortedClassHierarchy(context);
            for(Class<?> ctx : contextHierarchy) {
                if(classSpecificMappings.containsKey(ctx)) {
                    for(Class<?> tc : toHierarchy) {
                        if (classSpecificMappings.get(ctx).containsKey(tc)) {
                            Map<Class<?>, Map<Token, InstanceFieldMapper>> mappings = classSpecificMappings.get(ctx).get(tc);
                            for (Class<?> c : fromHierarchy) {
                                if (mappings.containsKey(c)) {
                                    mappings.get(c).forEach((token, instanceFieldMapper) -> {
                                        result.putIfAbsent(token, instanceFieldMapper);
                                    });
                                }
                            }
                        }
                    }
                }
            }
            return new ArrayList<InstanceFieldMapper<FROM, TO, ?>>((Collection)result.values());
        });
    }

    private List<Class<?>> getSortedClassHierarchy(Class<?> c) {
        List<Class<?>> sortedHierarchy = new ArrayList<>();
        Class<?> work = c;
        do {
            sortedHierarchy.add(work);
        } while((work = work.getSuperclass()) != null);
        return sortedHierarchy;
    }

    private <TO, V> Token getToken(Class<TO> toClass, BiConsumer<TO, V> field) {
        TO qi = genericFactory.getQueryInstance(toClass);
        field.accept(qi, null);
        Token token = ((QueryWrapper)qi).getCurrentProperty().getToken();
        return token;
    }


    private <TO, V> Token getToken(Class<TO> toClass, Function<TO, V> field) {
        TO qi = genericFactory.getQueryInstance(toClass);
        field.apply(qi);
        Token token = ((QueryWrapper)qi).getCurrentProperty().getToken();
        return token;
    }

    private <FROM, TO> Map<Token, InstanceFieldMapper> getMapForWriting(Class<?> context, Class<FROM> fromClass, Class<TO> toClass) {
        mappingsCache.clear();
        return classSpecificMappings
                .computeIfAbsent(context, cc -> new ConcurrentHashMap<>())
                .computeIfAbsent(toClass, cc -> new ConcurrentHashMap<>())
                .computeIfAbsent(fromClass, cc -> new ConcurrentHashMap<>());
    }

    private static class CacheTriple {
        private Class<?> context;
        private Class<?> from;
        private Class<?> to;

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
