package io.ran;

import io.ran.token.Token;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PropertyListTest {

    @Test
    public void deduplicatePropertiesBySnakeCase_onAddProperty() {
        Property.PropertyList list = new Property.PropertyList();
        list.add(Property.get(Token.snake_case("the_column")));
        list.add(Property.get(Token.snake_case("the_column")));

        assertEquals(1, list.size());
    }

    @Test
    public void deduplicatePropertiesBySnakeCase_onAddToken() {
        Property.PropertyList list = new Property.PropertyList();
        list.add(Token.snake_case("the_column"), Clazz.of(String.class));
        list.add(Token.snake_case("the_column"), Clazz.of(String.class));

        assertEquals(1, list.size());
    }

    @Test
    public void deduplicatePropertiesBySnakeCase_onAddString() {
        Property.PropertyList list = new Property.PropertyList();
        list.add("the_column", Clazz.of(String.class));
        list.add("the_column", Clazz.of(String.class));

        assertEquals(1, list.size());
    }

    @Test(expected = RuntimeException.class)
    public void addByPositionIsUnsupported() {
        Property.PropertyList list = new Property.PropertyList();
        list.add(1,Property.get(Token.snake_case("the_column")));
    }

    @Test(expected = RuntimeException.class)
    public void addAllByPositionIsUnsupported() {
        Property.PropertyList list = new Property.PropertyList();
        list.addAll(1, Arrays.asList(Property.get(Token.snake_case("the_column"))));
    }

}