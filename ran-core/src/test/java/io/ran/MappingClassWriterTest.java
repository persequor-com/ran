package io.ran;

import io.ran.testclasses.ClassWithStaticMembers;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class MappingClassWriterTest {
    @Test
    public void mapClassWithStaticMethods() {
        Class<ClassWithStaticMembers> classWithStaticMembers = AutoMapper.get(ClassWithStaticMembers.class);
        List<Method> methods = Arrays.asList(classWithStaticMembers.getDeclaredMethods());
        assertFalse(methods.stream().anyMatch(m -> m.getName().equals("setSomethingStatic")));
    }
}
