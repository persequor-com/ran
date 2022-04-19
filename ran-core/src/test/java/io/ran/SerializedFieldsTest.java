package io.ran;

import io.ran.testclasses.ObjectWithSerializedField;
import io.ran.testclasses.SerializableObject;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializedFieldsTest {
    private GuiceHelper helper;
    private MappingHelper mappingHelper;
    private ObjectWithSerializedField objectWithSerializedField;

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

    private Property property(String name) {
        return Property.get(Token.of(name));
    }

    @Test
    public void serializedField_happyPath() {
        ObjectMap objectMap = new ObjectMap();
        mappingHelper.columnize(objectWithSerializedField, objectMap);

        assertEquals("The id of this thing", objectMap.get(Token.get("id")));
        assertEquals("{\"up\":33,\"down\":\"down\"}", objectMap.getString(property("serialized")));
    }

    @Test
    public void deserializedField_forNonMappedObject() {
        ObjectMap objectMap = new ObjectMap();
        objectMap.set(property("id"), "the id yo");
        objectMap.set(property("serialized"), "{\"up\":33,\"down\":\"down\"}");
        ObjectWithSerializedField anotherObjectWithSerializedField = new ObjectWithSerializedField();
        mappingHelper.hydrate(anotherObjectWithSerializedField, objectMap);

        assertEquals("the id yo", anotherObjectWithSerializedField.getId());
        assertEquals(33, anotherObjectWithSerializedField.getSerialized().getUp());
        assertEquals("down", anotherObjectWithSerializedField.getSerialized().getDown());
    }

    @Test
    public void deserializedField_forMappedObject() {
        ObjectMap objectMap = new ObjectMap();
        objectMap.set(property("id"), "the id yo");
        objectMap.set(property("serialized"), "{\"up\":33,\"down\":\"down\"}");
        ObjectWithSerializedField anotherObjectWithSerializedField = helper.factory.get(ObjectWithSerializedField.class);
        mappingHelper.hydrate(anotherObjectWithSerializedField, objectMap);

        assertEquals("the id yo", anotherObjectWithSerializedField.getId());
        assertEquals(33, anotherObjectWithSerializedField.getSerialized().getUp());
        assertEquals("down", anotherObjectWithSerializedField.getSerialized().getDown());
    }
}
