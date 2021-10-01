package io.ran;

import io.ran.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RelationDescriber {
	private Clazz collectionType;
	private Token field;
	private Clazz<?> fromClass;
	private Clazz<?> toClass;
	private KeySet fromKeys;
	private KeySet toKeys;
	private RelationType type;
	private RelationDescriberList via;
	private Relation relationAnnotation;

	private RelationDescriber() {}

	public static RelationDescriberList list(Clazz fromClass) {
		return new RelationDescriberList(fromClass);
	}

	public static RelationDescriber describer(Clazz fromClass, Relation relationAnnotation, Token field, Clazz<?> toClass, KeySet fromKeys, KeySet toKeys, RelationType type, Clazz collectionType) {

		RelationDescriber desc = new RelationDescriber();
		desc.relationAnnotation = relationAnnotation;
		desc.collectionType = collectionType;
		desc.fromClass = fromClass;
		desc.field = field;
		desc.toClass = toClass;
		desc.fromKeys = fromKeys;
		desc.type = type;
		desc.toKeys = toKeys;
		desc.via = new RelationDescriberList(desc.fromClass);
		return desc;
	}

	public Clazz<?> getFromClass() {
		return fromClass;
	}

	public Clazz<?> getToClass() {
		return toClass;
	}

	public KeySet getFromKeys() {
		return fromKeys;
	}

	public RelationType getType() {
		return type;
	}

	public Token getField() {
		return field;
	}

	public KeySet getToKeys() {
		return toKeys;
	}

	public Clazz getCollectionType() {
		return collectionType;
	}

	public boolean isCollectionRelation() {
		return collectionType != null;
	}

	public List<RelationDescriber> getVia() {
		return via;
	}

	public RelationDescriber inverse() {
		return RelationDescriber.describer(toClass, null, null, fromClass, toKeys, fromKeys, null, null);
	}

	public Relation getRelationAnnotation() {
		return relationAnnotation;
	}

	public static class RelationDescriberList extends ArrayList<RelationDescriber> {
		private Clazz<?> fromClass;

		public RelationDescriberList(Clazz<?> fromClass) {
			this.fromClass = fromClass;
		}

		public RelationDescriber get(String snakeCaseField) {
			return stream().filter(rd -> rd.getField().snake_case().equals(snakeCaseField)).findFirst().orElseThrow(() -> new RuntimeException("Could not find field "+snakeCaseField));
		}

		public Optional<RelationDescriber> get(Class<?> toClass) {
			return stream().filter(rd -> rd.getToClass().clazz.equals(toClass)).findFirst();
		}
	}
}
