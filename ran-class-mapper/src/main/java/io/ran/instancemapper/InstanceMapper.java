package io.ran.instancemapper;

import javax.inject.Inject;

public class InstanceMapper {
    private final InstanceMappingRegistry instanceMappingRegistry;

    @Inject
    public InstanceMapper(InstanceMappingRegistry instanceMappingRegistry) {
        this.instanceMappingRegistry = instanceMappingRegistry;
    }

    public <FROM, TO> void map(FROM from, TO to) {
        for (InstanceFieldMapper<FROM, TO> instanceFieldMapper : instanceMappingRegistry.getMappers((Class<FROM>)from.getClass(), (Class<TO>)to.getClass())) {
            instanceFieldMapper.map(from, to);
        }
    }

    public <FROM, TO> void map(Class<?> context, FROM from, TO to) {
        for (InstanceFieldMapper<FROM, TO> instanceFieldMapper : instanceMappingRegistry.getMappers(context, (Class<FROM>)from.getClass(), (Class<TO>)to.getClass())) {
            instanceFieldMapper.map(from, to);
        }
    }
}
