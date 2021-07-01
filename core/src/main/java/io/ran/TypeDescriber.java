package io.ran;

import java.util.List;

public interface TypeDescriber<T> {
	KeySet primaryKeys();
	List<KeySet> indexes();
	Property.PropertyList fields();
	RelationDescriber.RelationDescriberList relations();
	Class<T> clazz();
	Annotations annotations();

}
