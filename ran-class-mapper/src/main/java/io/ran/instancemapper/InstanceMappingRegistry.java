package io.ran.instancemapper;

import io.ran.*;
import io.ran.token.Token;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;

public class InstanceMappingRegistry {
    Map<Class<?>, Map<Class<?>, Map<Class<?>, Map<Token, InstanceFieldMapper>>>> classSpecificMappings = new HashMap<>();
    private GenericFactory genericFactory;

    @Inject
    public InstanceMappingRegistry(GenericFactory genericFactory) {
        this.genericFactory = genericFactory;
    }

    public <FROM, TO, V> void add(Class<FROM> fromClass, Class<TO> toClass, Function<FROM, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        FROM qi = genericFactory.getQueryInstance(fromClass);
        field.apply(qi);
        Token token = ((QueryWrapper)qi).getCurrentProperty().getToken();

        classSpecificMappings
                .computeIfAbsent(Object.class, cc -> new HashMap<>())
                .computeIfAbsent(toClass, cc -> new HashMap<>())
                .computeIfAbsent(fromClass, cc -> new HashMap<>())
                .put(token, mapper);
    }

    public <FROM, TO, V> void add(Class<?> context, Class<FROM> fromClass, Class<TO> toClass, Function<FROM, V> field, InstanceFieldMapper<FROM, TO, V> mapper) {
        FROM qi = genericFactory.getQueryInstance(fromClass);
        field.apply(qi);
        Token token = ((QueryWrapper)qi).getCurrentProperty().getToken();

        classSpecificMappings
                .computeIfAbsent(context, cc -> new HashMap<>())
                .computeIfAbsent(toClass, cc -> new HashMap<>())
                .computeIfAbsent(fromClass, cc -> new HashMap<>())
                .put(token, mapper);
    }

    public <FROM, TO> List<InstanceFieldMapper<FROM, TO, ?>> getMappers(Class<FROM> fromClass, Class<TO> toClass) {
        return getMappers(Object.class, fromClass, toClass);
    }

    public <FROM, TO> List<InstanceFieldMapper<FROM, TO, ?>> getMappers(Class<?> context, Class<FROM> fromClass, Class<TO> toClass) {
        LinkedHashMap<Token, InstanceFieldMapper<?, ?, ?>> result = new LinkedHashMap<>();
        List<Class<?>> fromHierarchy = getSortedClassHierarchy(fromClass);
        List<Class<?>> contextHierarchy = getSortedClassHierarchy(context);
        for(Class<?> ctx : contextHierarchy) {
            if(classSpecificMappings.containsKey(ctx)) {
                Map<Class<?>, Map<Token, InstanceFieldMapper>> mappings = classSpecificMappings.get(ctx).get(toClass);

                for (Class<?> c : fromHierarchy) {
                    if (mappings.containsKey(c)) {
                        mappings.get(c).forEach((token, instanceFieldMapper) -> {
                            result.putIfAbsent(token, instanceFieldMapper);
                        });
                    }
                }
            }
        }
        return new ArrayList<InstanceFieldMapper<FROM, TO, ?>>((Collection)result.values());
    }

    private List<Class<?>> getSortedClassHierarchy(Class<?> c) {
        List<Class<?>> sortedHierarchy = new ArrayList<>();
        Class<?> work = c;
        do {
            sortedHierarchy.add(work);
        } while((work = work.getSuperclass()) != null);
        return sortedHierarchy;
    }
}
