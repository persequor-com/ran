/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import io.ran.token.Token;

import javax.management.remote.JMXServerErrorException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TypeDescriberImpl<T> implements TypeDescriber<T> {
	private static Map<Class, TypeDescriber> descibers = new ConcurrentHashMap<>();
	private Clazz<T> clazz;
	private KeySet primaryKeys = null;
	private Property.PropertyList fields = null;
	private Property.PropertyList allFields = null;

	private RelationDescriber.RelationDescriberList relations = null;
	private Annotations annotations = new Annotations();
	private List<KeySet> indexes;
	private List<ClazzMethod> methods;

	private TypeDescriberImpl(Clazz<T> clazz) {
		this.clazz = clazz;
		annotations.addFrom(clazz);
		AutoMapper.map(clazz.clazz);
	}

	public static <X> TypeDescriber<X> getTypeDescriber(Class<X> tClass) {
		if (!descibers.containsKey(tClass)) {
			synchronized (TypeDescriberImpl.class) {
				if (!descibers.containsKey(tClass)) {
					return descibers.computeIfAbsent(tClass, c -> new TypeDescriberImpl<>(Clazz.of(tClass)));
				}
			}
		}
		return descibers.get(tClass);
	}

	@Override
	public KeySet primaryKeys() {
		if (primaryKeys == null) {
			synchronized (this) {
				if (primaryKeys == null) {
					primaryKeys = clazz.getKeys().getPrimary();
				}
			}
		}
		return primaryKeys;
	}

	@Override
	public List<KeySet> indexes() {
		if (indexes == null) {
			synchronized (this) {
				if (indexes == null) {
					indexes = clazz.getKeys().values().stream().filter(ks -> !ks.isPrimary()).collect(Collectors.toList());
				}
			}
		}
		return indexes;
	}

	@Override
	public Property.PropertyList fields() {
		if (fields == null) {
			synchronized (this) {
				if (fields == null) {
					fields = clazz.getProperties();
				}
			}
		}
		return fields;
	}

	@Override
	public Property.PropertyList allFields() {
		if (allFields == null) {
			synchronized (this) {
				if (allFields == null) {
					allFields = clazz.getAllFields();
				}
			}
		}
		return allFields;
	}

	@Override
	public List<ClazzMethod> methods() {
		if (methods == null) {
			synchronized (this) {
				if (methods == null) {
					methods = clazz.methods();
				}
			}
		}
		return methods;
	}

	public ClazzMethod method(String methodToken) {
		return methods().stream().filter(cm -> cm.matches(methodToken)).findFirst().orElseThrow(() -> new RuntimeException("Could not find method bytoken " + methodToken + " on " + clazz.clazz.getName()));
	}

	@Override
	public RelationDescriber.RelationDescriberList relations() {
		if (relations == null) {
			synchronized (this) {
				if (relations == null) {
					relations = new RelationDescriber.RelationDescriberList(clazz).addRelations(getRelations(clazz));
				}
			}
		}
		return relations;
	}

	public List<RelationDescriber> getRelations(Clazz c) {
		Property.PropertyList fields = c.getProperties();
		List<RelationDescriber> describers = new ArrayList<>();
		for (Field field : c.clazz.getDeclaredFields()) {
			Relation relation = field.getAnnotation(Relation.class);

			if (relation != null) {
				Token token = Token.camelHump(field.getName());
				Token idToken = Token.camelHump(field.getName() + "Id");
				boolean isCollection = field.getType().isAssignableFrom(Collection.class) || field.getType().isAssignableFrom(List.class);


				describers.add(describeRelation(c, relation, token, Arrays.asList(relation.fields()), Arrays.asList(relation.relationFields()), isCollection ? Clazz.of(field).generics.get(0) : Clazz.of(field), isCollection ? Clazz.of(field) : null, Clazz.of(relation.via())));
			}
		}
		return describers;
	}

	private static RelationDescriber describeRelation(Clazz<?> from, Relation relationAnnotation, Token token, List<String> fields, List<String> relationFields, Clazz<?> relation, Clazz<?> collectionType, Clazz<?> via) {
		RelationDescriber relationDescriber;
		Property.PropertyList properties = from.getProperties();


		KeySet selfKeys = KeySet.get();
		KeySet relationKeys = KeySet.get();
		Clazz<?> relationForFields = relationAnnotation.via() != None.class ? Clazz.of(relationAnnotation.via()) : relation;

		if (relationFields.size() > 0) {
			relationFields.stream().map(Token::get).map(t -> relationForFields.getProperties().get(t)).forEach(relationKeys::add);
		}
		if (fields.size() > 0) {
			fields.stream().map(Token::get).map(properties::get).forEach(selfKeys::add);
		}

		if (relationKeys.isEmpty()) {
			relationKeys.add(relation.getProperties().mapProperties(properties));
		}
		if (selfKeys.isEmpty()) {
			selfKeys.add(properties.mapProperties(relation.getProperties()));
		}

		if (collectionType != null) {
			relationDescriber = RelationDescriber.describer(from, relationAnnotation, token, relation, selfKeys, relationKeys, RelationType.OneToMany, collectionType);

		} else {
			relationDescriber = RelationDescriber.describer(from, relationAnnotation, token, relation, selfKeys, relationKeys, RelationType.OneToOne, null);
		}

		if (via.clazz != None.class) {
			List<RelationDescriber> viaRelations = TypeDescriberImpl.getTypeDescriber(via.clazz).relations();
			Optional<RelationDescriber> fromRelation;
			Optional<RelationDescriber> toRelation;
			if (!viaRelations.isEmpty() && from.clazz.equals(relation.clazz)) {
				boolean keysMatchesStraight = viaRelations.get(0).getFromKeys().matchesKeys(relationDescriber.getToKeys())
						&&
						viaRelations.get(0).getToKeys().matchesKeys(relationDescriber.getFromKeys());

				fromRelation = Optional.ofNullable(
						keysMatchesStraight
								? viaRelations.get(0)
								: viaRelations.get(1));
				toRelation = Optional.ofNullable(
						keysMatchesStraight
								? viaRelations.get(1)
								: viaRelations.get(0));

				boolean toMatchesFrom = toRelation.get().getFromKeys().matchesKeys(relationDescriber.getFromKeys())
						&&
						toRelation.get().getToKeys().matchesKeys(relationDescriber.getToKeys());

				if (toMatchesFrom) {
					throw new RuntimeException("Invalid via relation configuration. 'fields' and 'relationsFields' must match. See configuration on " + relationDescriber.getFromClass().getSimpleName() + "." + relationDescriber.getField().camelHump());
				}


			} else {
				fromRelation = viaRelations.stream().filter(r -> r.getToClass().clazz.equals(from.clazz)).findFirst();
				toRelation = viaRelations.stream().filter(r -> r.getToClass().clazz.equals(relation.clazz)).findFirst();
			}


			relationDescriber.getVia().add(RelationDescriber
					.describer(from
							, relationAnnotation
							, token
							, via
							, fromRelation.map(RelationDescriber::getToKeys)
									.orElse(from.getKeys().getPrimary())
							, fromRelation
									.map(RelationDescriber::getFromKeys)
									.orElse(via.getKeys().getPrimary().toProperties().mapProperties(from.getKeys().getPrimary().toProperties()))
							, RelationType.OneToMany
							, fromRelation
									.map(RelationDescriber::getCollectionType)
									.orElse(null)));

			relationDescriber.getVia().add(RelationDescriber
					.describer(via
							, from.getRelationFields().stream().filter(f -> f.getName().equals(token.camelHump())).findFirst().orElseThrow(RuntimeException::new).getAnnotation(Relation.class)
							, token
							, relation
							, toRelation.map(RelationDescriber::getFromKeys)
									.orElse(via
											.getKeys()
											.getPrimary()
											.toProperties()
											.mapProperties(relation.getKeys().getPrimary().toProperties())
									)
							, toRelation
									.map(RelationDescriber::getToKeys)
									.orElse(relation.getKeys().getPrimary())
							, RelationType.OneToMany
							, toRelation.map(RelationDescriber::getCollectionType)
									.orElse(null)
					)
			);
		}
		return relationDescriber;
	}

	@Override
	public Class<T> clazz() {
		return clazz.clazz;
	}

	@Override
	public Annotations annotations() {
		return annotations;
	}

	@Override
	public Property getPropertyFromSnakeCase(String snakeCase) {
		return allFields().get(snakeCase);
	}

	@Override
	public Token getTokenFromSnakeCase(String snakeCase) {
		return allFields().get(snakeCase).getToken();
	}
}
