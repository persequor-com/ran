package io.ran;

import io.ran.token.CamelHumpToken;
import io.ran.token.Token;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Clazz<T> {
	private static final String COVERAGE_FIELD_PATTERN = "__\\$.*\\$__";
	public String className;
	public Class<T> clazz;
	public List<Clazz<?>> generics = new ArrayList<>();
	public Map<String,Clazz<?>> genericMap = new HashMap<>();

	public static Clazz of(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = ((ParameterizedType) type);
			List<Clazz> genericClasses = Arrays.asList(parameterizedType.getActualTypeArguments()).stream().map(Clazz::of).collect(Collectors.toList());

			return Clazz.ofClazzes((Class)((ParameterizedType) type).getRawType(), genericClasses);
		} else if (type instanceof Class) {
			return Clazz.of((Class)type);
		}
		throw new RuntimeException("Don't know what to do with type: "+type.getClass().getName());
	}

	public static Clazz getShort() {
		return Clazz.of(short.class);
	}

	public static Clazz of(String s) {
		return new Clazz(s);
	}

	public Method getUnBoxSignature() {
		return Primitives.get(clazz).getConstructorSignature();
	}

	public Clazz getUnBoxed() {
		if (isPrimitive() || !isBoxedPrimitive()) {
			return this;
		}
		return Clazz.of(Primitives.get(clazz).getPrimitive());
	}

	public Clazz getBoxed() {
		if (!isPrimitive()) {
			return this;
		}
		return Clazz.of(Primitives.get(clazz).getBoxed());
	}

	public String getInternalName() {
		if (clazz == null) {
			return className;
		}
		return clazz.getName().replace('.','/');
	}

	public boolean isPrimitive() {
		return clazz.isPrimitive();
	}

	public int size() {
		if (isPrimitive()) {
			if (clazz == void.class) {
				return 0;
			}
			if (clazz == long.class || clazz == double.class) {
				return 2;
			}
		}
		return 1;
	}

	public boolean isBoxedPrimitive() {
		return Primitives.isBoxedPrimitive(clazz);
	}

	public Clazz getPrimitive() {
		return Clazz.of(Primitives.get(clazz).getPrimitive());
	}

	public Clazz<?> getSuper() {
		if (clazz.getGenericSuperclass() != null) {
			return Clazz.of(clazz.getGenericSuperclass());
		}
		return Clazz.of(clazz.getSuperclass());
	}

	public int getPrimitiveOffset() {
		return Primitives.get(clazz).getPrimitiveOffset();
	}





	public static Clazz getVoid() {
		return Clazz.of(void.class);
	}

	public static Clazz getInt() {
		return Clazz.of(int.class);
	}

	public String getDescriptor() {
		if (isPrimitive()) {
			return Primitives.get(clazz).getDescriptor();
		}
		if (clazz == byte[].class) {
			return "[B";
		}
		return "L"+getInternalName()+";";
	}

	public String getSignature() {
		if (isPrimitive()) {
			return Primitives.get(clazz).getDescriptor();
		}
		if (clazz == byte[].class) {
			return "[B";
		}
		return "L"+getInternalName()+(generics.isEmpty()?"":"<"+(generics.stream().map(Clazz::getSignature).collect(Collectors.joining()))+">")+";";
	}

	public Clazz(String className) {
		this.className = className;
	}

	public static <T> Clazz<T> ofClasses(Class<T> clazz, Class<?>... generics) {
		return new Clazz<T>(clazz, Arrays.stream(generics).map(Clazz::of).toArray(Clazz[]::new));
	}

	public static Clazz of(Class clazz) {
		return new Clazz(clazz);
	}

	public static Clazz ofClazzes(Class clazz, Clazz<?>... generics) {
		return new Clazz<>(clazz, generics);
	}

	public static Clazz ofClazzes(Class clazz, List<Clazz> generics) {
		return new Clazz(clazz, generics);
	}

	public static <T> Clazz<T> ofType(Class<T> clazz, Type type) {
		Clazz<T> newClazz = new Clazz<T>(clazz);
		if (type instanceof ParameterizedType) {
			ParameterizedType paraType = (ParameterizedType) type;
			Arrays.stream(paraType.getActualTypeArguments()).map(t -> (Class<?>)t).map(Clazz::of).forEach(c -> newClazz.generics.add(c));
		}
		return newClazz;
	}

	public Clazz(Class<T> clazz, Clazz<?>... generics) {
		this(clazz, Arrays.asList(generics));
	}

	public Clazz(Class<T> clazz, List<Clazz<?>> generics) {
		this.clazz = clazz;
		if (clazz != null) {
			this.className = this.clazz.getName();
			if (this.clazz.getEnclosingClass() != null) {
				this.className = this.clazz.getEnclosingClass().getName() + "." + clazz.getSimpleName();
			}
			if (this.clazz == Void.class) {
				this.className = "void";
			}
			this.generics.addAll(generics);
			if (!generics.isEmpty()) {
				TypeVariable<? extends Class<?>>[] typeVariables = clazz.getTypeParameters();
				if (typeVariables.length == generics.size()) {
					for (int i = 0; i < generics.size(); i++) {
						genericMap.put(typeVariables[i].getName(), generics.get(i));
					}
				}
			}
		}
	}

	public static Clazz<?> of(Field field) {
		return Clazz.ofType(field.getType(), field.getGenericType());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Clazz<?> clazz1 = (Clazz<?>) o;

		if (!Objects.equals(clazz, clazz1.clazz)) return false;
		return Objects.equals(generics, clazz1.generics);
	}

	@Override
	public int hashCode() {
		int result = clazz != null ? clazz.hashCode() : 0;
		result = 31 * result + (generics != null ? generics.hashCode() : 0);
		return result;
	}

	public String name() {
		return className;
	}

	public String classRepresentation() {
		if (clazz.isPrimitive()) {
			return getSimpleName();
		}
		if (clazz != null) {
			String simpleName = getSimpleName();
			if (clazz.getEnclosingClass() != null) {
				simpleName = clazz.getEnclosingClass().getSimpleName()+"."+simpleName;
			}
			return simpleName+(generics.isEmpty() ? "" : "<"+generics.stream().map(Clazz::classRepresentation).collect(Collectors.joining(", "))+">");
		} else {
			return className;
		}
	}

	public String representation() {

		if (clazz == Void.class) {
			return "void";
		}
		if (clazz == int.class) {
			return "int";
		}
		return classRepresentation();
	}

	public boolean isInterface() {
		return clazz != null && clazz.isInterface();
	}

	public String clazzRepresentation() {
		if (generics.isEmpty()) {
			return "Clazz.of(" + getSimpleName() + ".class)";
		} else {
			return "(Clazz)Clazz.ofClasses(" + clazz.getSimpleName() + ".class, " + generics.stream().map(c -> c.name()+".class").collect(Collectors.joining(", ")) + ")";
		}
	}

	public String getSimpleName() {
		if (clazz.isPrimitive()) {
			if (clazz.equals(int.class)) {
				return "Integer";
			}
			if (clazz.equals(char.class)) {
				return "Character";
			}
			return clazz.getSimpleName().substring(0,1).toUpperCase()+clazz.getSimpleName().substring(1);
		}
		return clazz.getSimpleName();
	}

	public KeySets getKeys() {
		return getProperties().keys();
	}

	public Property.PropertyList getProperties() {
		Property.PropertyList fields = Property.list();

		for (Field field : getFields()) {
			if(!isPropertyField(field)) {
				continue;
			}
			Token token = Token.camelHump(field.getName());
			Clazz<?> fieldType = Clazz.of(field);
			Property<?> property = Property.get(token,fieldType);
			Key[] keys = field.getAnnotationsByType(Key.class);
			Arrays.asList(keys).forEach(key -> {
				property.addKey(new KeyInfo(false, property, key.name(), key.order()));
			});
			PrimaryKey[] primaryKeys = field.getAnnotationsByType(PrimaryKey.class);
			Arrays.asList(primaryKeys).forEach(key -> {
				property.addKey(new KeyInfo(true, property, "", key.order()));
			});

			property.setOn(this);
			property.getAnnotations().addFrom(field);
			if (isPropertyField(field)) {
				fields.add(property);
			}
		}
		return fields;
	}

	public ClazzMethodList methods() {
		Map<Method, ClazzMethod> result = new LinkedHashMap();
		Clazz working = this;
		do {
			if (clazz.isInterface()) {
				Arrays.stream(working.clazz.getMethods()).filter(m -> !m.isBridge()).forEach(m -> {
					result.put(m, new ClazzMethod(this, m));
				});
			} else {
				Arrays.stream(working.clazz.getDeclaredMethods()).filter(m -> !m.isBridge()).forEach(m -> {
					result.put(m, new ClazzMethod(this, m));
				});
			}
			working = working.getSuper();
		} while(working.clazz != null && !Object.class.equals(working.clazz));
		return new ClazzMethodList(result.values());
	}

	public static boolean isPropertyField(Field field) {
		return CamelHumpToken.is(field.getName()) && !Modifier.isTransient(field.getModifiers()) && field.getAnnotation(Relation.class) == null;
	}

	public static boolean isRelationField(Field field) {
		return CamelHumpToken.is(field.getName()) && field.getAnnotation(Relation.class) != null;
	}



	public List<Field> getFields() {
		List<Field> fields = new ArrayList<>();
		Class working = clazz;
		while(working != Object.class) {
			fields.addAll(Arrays.asList(working.getDeclaredFields()));
			working = working.getSuperclass();
		}
		return fields;
	}

	public List<Field> getRelationFields() {
		return getFields().stream().filter(Clazz::isRelationField).collect(Collectors.toList());
	}

	public List<Field> getPropertyFields() {
		return getFields().stream().filter(Clazz::isPropertyField).collect(Collectors.toList());
	}


	public List<Field> getDeclaredPropertyFields() {
		return Stream.of(clazz.getDeclaredFields()).filter(Clazz::isPropertyField).collect(Collectors.toList());
	}

	private static RelationDescriber describeRelation(Clazz<?> from, Relation relationAnnotation, Token token, List<String> fields, List<String> relationFields, Clazz<?> relation, Clazz<?> collectionType, Clazz<?> via) {
		RelationDescriber relationDescriber;
		Property.PropertyList properties = from.getProperties();


		KeySet selfKeys = KeySet.get();
		KeySet relationKeys = KeySet.get();
		if (relationFields.size() > 0) {
			relationFields.stream().map(Token::get).map(t ->  relation.getProperties().get(t)).forEach(relationKeys::add);
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
			List<RelationDescriber> viaRelations = via.getRelations();
			Optional<RelationDescriber> fromRelation = viaRelations.stream().filter(r -> r.getToClass().clazz.equals(from.clazz)).findFirst();
			Optional<RelationDescriber> toRelation = viaRelations.stream().filter(r -> r.getToClass().clazz.equals(relation.clazz)).findFirst();

			relationDescriber.getVia().add(RelationDescriber
					.describer(from, relationAnnotation, token, via, fromRelation.map(RelationDescriber::getToKeys)
					.orElse(from.getKeys().getPrimary())
				, fromRelation
					.map(RelationDescriber::getFromKeys)
					.orElse(via.getKeys().getPrimary().toProperties().mapProperties(from.getKeys().getPrimary().toProperties()))
				, RelationType.OneToMany
				, fromRelation
					.map(RelationDescriber::getCollectionType)
					.orElse(null)));

			relationDescriber.getVia().add(RelationDescriber
					.describer(via, relationAnnotation, token, relation, toRelation.map(RelationDescriber::getFromKeys)
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

	public List<RelationDescriber> getRelations() {
		Property.PropertyList fields = getProperties();
		List<RelationDescriber> describers = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			Relation relation = field.getAnnotation(Relation.class);

			if (relation != null) {
				Token token = Token.camelHump(field.getName());
				Token idToken = Token.camelHump(field.getName()+"Id");
				boolean isCollection = field.getType().isAssignableFrom(Collection.class) || field.getType().isAssignableFrom(List.class);


				describers.add(describeRelation(this, relation, token, Arrays.asList(relation.fields()), Arrays.asList(relation.relationFields()), isCollection ? Clazz.of(field).generics.get(0) : Clazz.of(field), isCollection ? Clazz.of(field) : null, Clazz.of(relation.via())));
			}
		}
		return describers;
	}

	public Object getDefaultValue() {
		if (isPrimitive()) {
			return Primitives.get(clazz).getDefaultValue();
		}
		return null;
	}

	public Type getType() {
		if (generics.isEmpty()) {
			return clazz;
		}

		return new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return generics.stream().map(c -> c.clazz).toArray(Type[]::new);
			}

			@Override
			public Type getRawType() {
				return clazz;
			}

			@Override
			public Type getOwnerType() {
				return clazz.getEnclosingClass();
			}
		};
	}

	Clazz<?> findGenericSuper(Class<?> ofClazz) {
		if (clazz.equals(Object.class)) {
			return null;
		}
		if (clazz.equals(ofClazz)) {
			return this;
		}
		Clazz<?> s;
		Clazz<?> sup = getSuper();
		if (sup != null && sup.clazz != null) {
			s = sup.findGenericSuper(ofClazz);
			if (s != null) {
				return s;
			}
		}
		for (Type i : Arrays.asList(clazz.getGenericInterfaces())) {
			s = Clazz.of(i).findGenericSuper(ofClazz);
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	public boolean equals(Clazz<?> clazz) {
		return this.clazz.equals(clazz.clazz);
	}

	public boolean declaresMethod(ClazzMethod cm) {
		return methods().find(cm).filter(m -> m.getDeclaringClazz().equals(this)).isPresent();
	}

	public boolean isVoid() {
		return clazz.equals(Void.class) || clazz.equals(void.class);
	}
}
