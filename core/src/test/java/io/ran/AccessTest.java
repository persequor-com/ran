package io.ran;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccessTest {
    private Access access;
    @Test
    public void happy_path() {
        access = Access.of(1);
        assertEquals(access, Access.Public);
        access = Access.of(2);
        assertEquals(access, Access.Private);
        access = Access.of(4);
        assertEquals(access, Access.Protected);
    }

    @Test
    public void synthetic_private() {
        // Test when it's a generated synthetic private access modifier
        access = Access.of(4098);
        assertEquals(access, Access.Private);
    }
}