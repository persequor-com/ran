package io.ran.instancemapper;

import javax.inject.Inject;

public class InstanceMapper<FROM, TO> {
    private InstanceMappingRegistry instanceMappingRegistry;

    @Inject
    public InstanceMapper(InstanceMappingRegistry instanceMappingRegistry) {
        this.instanceMappingRegistry = instanceMappingRegistry;
    }

    public void map(FROM from, TO to) {
        for (InstanceFieldMapper<FROM, TO, ?> instanceFieldMapper : instanceMappingRegistry.getMappers((Class<FROM>)from.getClass(), (Class<TO>)to.getClass())) {
            instanceFieldMapper.map(from, to);
        }
    }

    public void map(Class<?> context, FROM from, TO to) {
        for (InstanceFieldMapper<FROM, TO, ?> instanceFieldMapper : instanceMappingRegistry.getMappers(context, (Class<FROM>)from.getClass(), (Class<TO>)to.getClass())) {
            instanceFieldMapper.map(from, to);
        }
    }
}
