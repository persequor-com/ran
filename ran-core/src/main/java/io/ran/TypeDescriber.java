package io.ran;

import io.ran.token.Token;

import java.util.List;

public interface TypeDescriber<T> {
	KeySet primaryKeys();

	List<KeySet> indexes();

	Property.PropertyList fields();

	Property.PropertyList allFields();

	List<ClazzMethod> methods();

	RelationDescriber.RelationDescriberList relations();

	Class<T> clazz();

	Annotations annotations();

	Property getPropertyFromSnakeCase(String snakeCase);

	Token getTokenFromSnakeCase(String snakeCase);

}
