package io.ran.instancemapper;

import javax.inject.Inject;
import java.util.function.BiConsumer;

public class InstanceMapper {
    private final InstanceMappingRegistry instanceMappingRegistry;

    @Inject
    public InstanceMapper(InstanceMappingRegistry instanceMappingRegistry) {
        this.instanceMappingRegistry = instanceMappingRegistry;
    }

    public <FROM, TO> void map(FROM from, TO to) {
        for (BiConsumer<? super FROM, ? super TO> mapper : instanceMappingRegistry.getMappers(from.getClass(), to.getClass())) {
	        mapper.accept(from, to);
        }
    }

    public <FROM, TO> void map(Class<?> context, FROM from, TO to) {
        for (BiConsumer<? super FROM, ? super TO> mapper : instanceMappingRegistry.getMappers(context, from.getClass(), to.getClass())) {
	        mapper.accept(from, to);
        }
    }
}
