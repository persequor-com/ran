package io.ran;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeDescriberImpl<T> implements TypeDescriber<T> {
	private Clazz<T> clazz;
	private KeySet primaryKeys = null;
	private Property.PropertyList fields = null;
	private RelationDescriber.RelationDescriberList relations = null;
	private Annotations annotations = new Annotations();
	private List<KeySet> indexes;
	private List<ClazzMethod> methods;

	TypeDescriberImpl(Clazz<T> clazz, AutoMapper autoMapper) {
		this.clazz = clazz;
		annotations.addFrom(clazz);
		autoMapper.map(clazz.clazz);
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
		return methods().stream().filter(cm -> cm.matches(methodToken)).findFirst().orElseThrow(() -> new RuntimeException("Could not find method bytoken "+methodToken+" on "+clazz.clazz.getName()));
	}

	@Override
	public RelationDescriber.RelationDescriberList relations() {
		if (relations == null) {
			synchronized (this) {
				if (relations == null) {
					relations = new RelationDescriber.RelationDescriberList(clazz);
					relations.addAll(clazz.getRelations());
				}
			}
		}
		return relations;
	}

	@Override
	public Class<T> clazz() {
		return clazz.clazz;
	}

	@Override
	public Annotations annotations() {
		return annotations;
	}
}
