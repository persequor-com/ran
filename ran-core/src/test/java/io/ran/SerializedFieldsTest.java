package io.ran;

import io.ran.testclasses.ObjectWithSerializedField;
import io.ran.testclasses.SerializableObject;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class SerializedFieldsTest {
    private AutoMapper mapper;
    private GuiceHelper helper;
    private MappingHelper mappingHelper;
    private ObjectWithSerializedField objectWithSerializedField;

    @BeforeClass
    public static void beforeClass() throws IOException {

    }

    @Before
    public void setup() {
        helper = new GuiceHelper();
        mappingHelper = helper.injector.getInstance(MappingHelper.class);
        objectWithSerializedField = new ObjectWithSerializedField();
        objectWithSerializedField.setId("The id of this thing");
        objectWithSerializedField.setSerialized(new SerializableObject());
        objectWithSerializedField.getSerialized().setDown("down");
        objectWithSerializedField.getSerialized().setUp(33);
    }

    @Test
    public void serializedField_happyPath() {
        ObjectMap objectMap = new ObjectMap();
        mappingHelper.columnize(objectWithSerializedField, objectMap);

        assertEquals("The id of this thing", objectMap.get(Token.of("id")));
        assertEquals("{\"up\":33,\"down\":\"down\"}", objectMap.getString(Token.of("serialized")));
    }

    @Test
    public void deserializedField_forNonMappedObject() {
        ObjectMap objectMap = new ObjectMap();
        objectMap.set(Token.of("id"), "the id yo");
        objectMap.set(Token.of("serialized"), "{\"up\":33,\"down\":\"down\"}");
        ObjectWithSerializedField anotherObjectWithSerializedField = new ObjectWithSerializedField();
        mappingHelper.hydrate(anotherObjectWithSerializedField, objectMap);

        assertEquals("the id yo", anotherObjectWithSerializedField.getId());
        assertEquals(33, anotherObjectWithSerializedField.getSerialized().getUp());
        assertEquals("down", anotherObjectWithSerializedField.getSerialized().getDown());
    }

    @Test
    public void deserializedField_forMappedObject() {
        ObjectMap objectMap = new ObjectMap();
        objectMap.set(Token.of("id"), "the id yo");
        objectMap.set(Token.of("serialized"), "{\"up\":33,\"down\":\"down\"}");
        ObjectWithSerializedField anotherObjectWithSerializedField = helper.factory.get(ObjectWithSerializedField.class);
        mappingHelper.hydrate(anotherObjectWithSerializedField, objectMap);

        assertEquals("the id yo", anotherObjectWithSerializedField.getId());
        assertEquals(33, anotherObjectWithSerializedField.getSerialized().getUp());
        assertEquals("down", anotherObjectWithSerializedField.getSerialized().getDown());
    }
}
