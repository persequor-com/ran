package io.ran.testclasses;

import io.ran.GsonSerializer;
import io.ran.Serialized;

public class ObjectWithSerializedField {
	private String id;
	@Serialized(serializer = GsonSerializer.class)
	private SerializableObject serialized;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SerializableObject getSerialized() {
		return serialized;
	}

	public void setSerialized(SerializableObject serialized) {
		this.serialized = serialized;
	}
}
