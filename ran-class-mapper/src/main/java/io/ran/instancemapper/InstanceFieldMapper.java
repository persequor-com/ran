package io.ran.instancemapper;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface InstanceFieldMapper<FROM, TO, VALUETYPE> {
     void map(FROM from, TO to);
}
