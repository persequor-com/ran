package io.ran.instancemapper;

public interface InstanceFieldMapper<FROM, TO> {
     void map(FROM from, TO to);
}
