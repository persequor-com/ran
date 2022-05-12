package io.ran;

import io.ran.token.Token;
import org.junit.Test;

import static org.junit.Assert.*;

public class KeySetTest {
    @Test
    public void matchesKeys_happy() {
        KeySet keySet1 = KeySet.get(new KeySet.Field(Property.get(Token.snake_case("id"), Clazz.of(String.class)),0));
        KeySet keySet2 = KeySet.get(new KeySet.Field(Property.get(Token.snake_case("id"), Clazz.of(String.class)),0));
        assertTrue(keySet1.matchesKeys(keySet2));
    }

    @Test
    public void matchesKeys_notWhenNotSameOrder() {
        KeySet keySet1 = KeySet.get(new KeySet.Field(Property.get(Token.snake_case("id"), Clazz.of(String.class)),0));
        KeySet keySet2 = KeySet.get(new KeySet.Field(Property.get(Token.snake_case("id"), Clazz.of(String.class)),1));
        assertFalse(keySet1.matchesKeys(keySet2));
    }

    @Test
    public void matchesKeys_notWhenDifferentKeys() {
        KeySet keySet1 = KeySet.get(new KeySet.Field(Property.get(Token.snake_case("id"), Clazz.of(String.class)),0));
        KeySet keySet2 = KeySet.get(new KeySet.Field(Property.get(Token.snake_case("not_id"), Clazz.of(String.class)),0));
        assertFalse(keySet1.matchesKeys(keySet2));
    }
}