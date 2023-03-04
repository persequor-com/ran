/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.token.CamelHumpToken;
import io.ran.token.Token;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Clazz<T> {
	private static final String COVERAGE_FIELD_PATTERN = "__\\$.*\\$__";
	public String className;
	public Class<T> clazz;
	public List<Clazz<?>> generics = new ArrayList<>();
	public Map<String, Clazz<?>> genericMap = new HashMap<>();
	private Annotations annotations = null;

	public static Clazz raw(Class<?> clazz) {
		return new Clazz<>(clazz);
	}

	public static Clazz of(Type type) {
		return of(type, Collections.emptyMap());
	}

	public static Clazz of(Type type, Map<String, Clazz<?>> genericMap) {
		if (type == null) {
			return raw(null);
		}
		if (type instanceof Class) {
			return of((Class<?>) type);
		}
		if (type instanceof GenericArrayType) {
			return of((GenericArrayType) type, genericMap);
		}
		if (type instanceof WildcardType) {
			return of((WildcardType) type, genericMap);
		}
		if (type instanceof ParameterizedType) {
			return of((ParameterizedType) type, genericMap);
		}
		if (type instanceof TypeVariable) {
			return of((TypeVariable<?>) type, genericMap);
		}
		throw new IllegalArgumentException("unhandled Type type: " + type.getClass());
	}

	public static Clazz of(Class<?> clazz) {
		if(clazz != null) {
			// todo make shared map and add as you resolve?
			return Clazz.ofClazzes(clazz, Stream.of(clazz.getTypeParameters()).map(p -> Clazz.of(p)).collect(Collectors.toList()));
		}
		return raw(null);
	}

	public static Clazz of(GenericArrayType genericArray, Map<String, Clazz<?>> genericMap) {
		Clazz<?> arrType = of(genericArray.getGenericComponentType(), genericMap);
		return of(Array.newInstance(arrType.clazz, 0).getClass());
	}

	public static Clazz of(WildcardType wildcardType, Map<String, Clazz<?>> genericMap) {
		// todo warning that we ignore lower bounds?
		Type[] bounds = wildcardType.getUpperBounds();
		if (bounds.length > 1) {
			throw new IllegalArgumentException("multiple bounds are not supported " + wildcardType);
		}
		return Clazz.of(bounds[0], genericMap);
	}

	public static Clazz of(ParameterizedType parameterizedType, Map<String, Clazz<?>> genericMap) {
		List<Clazz> generics = Arrays.stream(parameterizedType.getActualTypeArguments())
				.map(t -> of(t, genericMap))
				.collect(Collectors.toList());
		// wildcards can have weaker bounds than the original type
		List<Clazz> defaultGenerics = of(parameterizedType.getRawType()).generics;

		List<Clazz> specificGenerics = getMostSpecific(parameterizedType, (List) generics, (List) defaultGenerics);
		return Clazz.ofClazzes((Class<?>) parameterizedType.getRawType(), specificGenerics);
	}

	public static Clazz of(TypeVariable<?> typeVariable, Map<String, Clazz<?>> genericMap) {
		if (genericMap.containsKey(typeVariable.getName())) {
			return genericMap.get(typeVariable.getName());
		}

		Type[] bounds = typeVariable.getBounds();
		if (bounds.length > 1) {
			throw new IllegalArgumentException("multiple bounds are not supported " + typeVariable);
		}

		Type bound = bounds[0];
		if (bound instanceof Class) {
			return Clazz.of((Class<?>) bound);
		}
		if (bound instanceof TypeVariable) {
			return of((TypeVariable<?>) bound, genericMap);
//			String boundName = ((TypeVariable<?>) bound).getName();
//			if (genericMap.containsKey(boundName)) {
//				return genericMap.get(boundName);
//			}
//			return new Clazz<>(Object.class);
		}
		if (bound instanceof ParameterizedType) {
			ParameterizedType bpt = (ParameterizedType) bounds[0];
			if(bpt.getActualTypeArguments().length == 1) { // todo you can have multiple args bound to itself
				Type tv2 = bpt.getActualTypeArguments()[0];
				if(typeVariable == tv2) {
					Clazz self = Clazz.ofClazzes((Class) bpt.getRawType());
					self.generics.add(self);
					self.genericMap.put(tv2.getTypeName(), self);
					return self;
					//return Clazz.ofClazzes((Class)bpt.getRawType(), new Clazz((Class)bpt.getRawType()));
					////return Clazz.of(bpt.getRawType(), genericMap);
				} else if((tv2 instanceof WildcardType)
						&& ((WildcardType)tv2).getLowerBounds().length == 1
						&& ((WildcardType)tv2).getLowerBounds()[0] == typeVariable) {
					return Clazz.ofClazzes((Class)bpt.getRawType(), new Clazz((Class)bpt.getRawType()));
				} else {
					System.out.println("Not same type: "+typeVariable+" and "+tv2);
				}
			} else if(bpt.getActualTypeArguments().length > 1) {
				List<Clazz> genricParams = Stream.of(bpt.getActualTypeArguments())
						.map(tv2 -> {
							if (tv2 instanceof TypeVariable && tv2 == typeVariable) {
								return new Clazz<>((Class) bpt.getRawType()); // todo this makes raw class and then generic pointing to it, find a way to loop it
							} else if (tv2 instanceof TypeVariable && genericMap.containsKey(((TypeVariable<?>)tv2).getName())) {
								return genericMap.get(((TypeVariable<?>)tv2).getName());
							} else if(tv2 instanceof ParameterizedType) {
								return of(tv2, genericMap);
							} else if (tv2 instanceof Class) {
								return new Clazz((Class) tv2);
							} else {
								return new Clazz<>(Object.class);
							}
						})
						.collect(Collectors.toList());
				return Clazz.ofClazzes((Class)bpt.getRawType(), genricParams);

				//System.out.println("More than one actual: "+bpt.getActualTypeArguments().length);
			}
			return Clazz.of(bound, genericMap);
		}

		throw new IllegalArgumentException("Unsupported type of bound " + bound.getClass());
	}

	// todo check type count?
	public static <T> Clazz<T> ofClasses(Class<T> clazz, Class<?>... generics) {
		return new Clazz<T>(clazz, Arrays.stream(generics).map(Clazz::of).toArray(Clazz[]::new));
	}

	public static Clazz ofClazzes(Class clazz, Clazz<?>... generics) {
		return new Clazz<>(clazz, generics);
	}

	public static Clazz ofClazzes(Class clazz, List<Clazz> generics) {
		return new Clazz(clazz, generics);
	}

	public static <T> Clazz<T> ofType(Class<T> clazz, Type type) { // todo
		Clazz<T> newClazz = new Clazz<T>(clazz);
		if (type instanceof ParameterizedType) {
			ParameterizedType paraType = (ParameterizedType) type;
			Arrays.stream(paraType.getActualTypeArguments()).map(t -> (Class<?>) t).map(Clazz::of).forEach(c -> newClazz.generics.add(c));
		}
		return newClazz;
	}

	public static Clazz<?> of(Field field) {
		return Clazz.ofType(field.getType(), field.getGenericType());
	}

	public static Clazz getVoid() {
		return Clazz.of(void.class);
	}

	public static Clazz getInt() {
		return Clazz.of(int.class);
	}

	public static Clazz getShort() {
		return Clazz.of(short.class);
	}

	public static Clazz of(String s) {
		return new Clazz(s);
	}

	public Clazz(String className) {
		this.className = className;
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
			if (!generics.isEmpty()) {
				TypeVariable<Class<T>>[] typeVariables = clazz.getTypeParameters();
				if (typeVariables.length == generics.size()) {
					for (int i = 0; i < generics.size(); i++) {
						TypeVariable<Class<T>> typeVariable = typeVariables[i];
						Type bound0 = typeVariable.getBounds()[0];
						if (typeVariable.getGenericDeclaration() == clazz &&
								bound0 instanceof ParameterizedType &&
								Arrays.equals(typeVariables, ((ParameterizedType) bound0).getActualTypeArguments()) &&
								((ParameterizedType) bound0).getRawType() == clazz &&
								generics.get(i).clazz == clazz) {
							generics.set(i, this);
						}
						genericMap.put(typeVariable.getName(), generics.get(i));
					}
				}
				this.generics.addAll(generics);
			}
			getAnnotations();
		}
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
		return clazz.getName().replace('.', '/');
	}

	public boolean isPrimitive() {
		return clazz.isPrimitive();
	}

	public boolean isArray() {
		return clazz.isArray();
	}

	public Clazz<?> getComponentType() {
		if(isArray()) {
			return Clazz.of(clazz.getComponentType());
		}
		return null;
	}

	public Clazz<?> getArrayType() {
		if(isArray()) {
			return null;
		}
		return Clazz.of(java.lang.reflect.Array.newInstance(clazz, 0).getClass());
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
			return Clazz.of(clazz.getGenericSuperclass(), this.genericMap);
		}
		return Clazz.of(clazz.getSuperclass());
	}

	public int getPrimitiveOffset() {
		return Primitives.get(clazz).getPrimitiveOffset();
	}


	public String getDescriptor() {
		if (isPrimitive()) {
			return Primitives.get(clazz).getDescriptor();
		}
		if (clazz == byte[].class) {
			return "[B";
		}
		return "L" + getInternalName() + ";";
	}

	public String getSignature() {
		if (isPrimitive()) {
			return Primitives.get(clazz).getDescriptor();
		}
		if (clazz == byte[].class) {
			return "[B";
		}
		return "L" + getInternalName() + (generics.isEmpty() ? "" : "<" + (generics.stream().map(Clazz::getSignature).collect(Collectors.joining())) + ">") + ";";
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

	@Override
	public String toString() {
		return name();
	}

	public String classRepresentation() {
		if (clazz.isPrimitive()) {
			return getSimpleName();
		}
		if (clazz != null) {
			String simpleName = getSimpleName();
			if (clazz.getEnclosingClass() != null) {
				simpleName = clazz.getEnclosingClass().getSimpleName() + "." + simpleName;
			}
			return simpleName + (generics.isEmpty() ? "" : "<" + generics.stream().map(Clazz::classRepresentation).collect(Collectors.joining(", ")) + ">");
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
			return "(Clazz)Clazz.ofClasses(" + clazz.getSimpleName() + ".class, " + generics.stream().map(c -> c.name() + ".class").collect(Collectors.joining(", ")) + ")";
		}
	}

	public Token getToken() {
		return Token.get(getSimpleName());
	}

	public String getSimpleName() {
		if (clazz.isPrimitive()) {
			if (clazz.equals(int.class)) {
				return "Integer";
			}
			if (clazz.equals(char.class)) {
				return "Character";
			}
			return clazz.getSimpleName().substring(0, 1).toUpperCase() + clazz.getSimpleName().substring(1);
		}
		return clazz.getSimpleName();
	}

	public KeySets getKeys() {
		return getProperties().keys();
	}

	public Annotations getAnnotations() {
		if (annotations == null) {
			annotations = new Annotations();
			annotations.addFrom(this);
		}
		return annotations;
	}

	public Property.PropertyList getAllFields() {
		return getFields(true);
	}

	public Property.PropertyList getProperties() {
		return getFields(false);
	}

	private Property.PropertyList getFields(boolean includeNonProperties) {
		Property.PropertyList fields = Property.list();

		for (Field field : getFields()) {
			if (isPublicStatic(field) || !includeNonProperties && !isPropertyField(field)) {
				continue;
			}
			Token token = Token.camelHump(field.getName());
			Clazz<?> fieldType = Clazz.of(field);
			Property<?> property = Property.get(token, fieldType);
			Key[] keys = field.getAnnotationsByType(Key.class);
			Arrays.asList(keys).forEach(key -> {
				property.addKey(new KeyInfo(false, property, key.name(), key.order(), key.unique()));
			});
			PrimaryKey[] primaryKeys = field.getAnnotationsByType(PrimaryKey.class);
			Arrays.asList(primaryKeys).forEach(key -> {
				property.addKey(new KeyInfo(true, property, "", key.order(), true));
			});

			property.setOn(this);
			property.getAnnotations().addFrom(field);
			fields.add(property);
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
				Clazz finalWorking = working;
				Arrays.stream(working.clazz.getDeclaredMethods()).filter(m -> !m.isBridge()).forEach(m -> {
					result.put(m, new ClazzMethod(this, finalWorking, m));
				});
			}
			working = working.getSuper();
		} while (working.clazz != null && !Object.class.equals(working.clazz));
		return new ClazzMethodList(result.values());
	}

	public static boolean isPropertyField(Field field) {
		return CamelHumpToken.is(field.getName()) && !Modifier.isTransient(field.getModifiers()) && field.getAnnotation(Relation.class) == null;
	}

	public static boolean isPublicStatic(Field field) {
		return Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers());
	}


	public static boolean isRelationField(Field field) {
		return CamelHumpToken.is(field.getName()) && field.getAnnotation(Relation.class) != null;
	}


	public List<Field> getFields() {
		List<Field> fields = new ArrayList<>();
		Class working = clazz;
		while (working != Object.class) {
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

	public Map<String, String> initialGenericSuperMap() {
		return genericMap.keySet().stream().collect(Collectors.toMap(k -> k, k-> k));
	}

	Clazz<?> findGenericSuper(Class<?> ofClazz) {
		return findGenericSuper(ofClazz, initialGenericSuperMap());
	}

	/**
	 * @param ofClazz superclass or generic interface of `this`
	 * @param thisTypeToSubType should be result of `this.initialGenericSuperMap()` which will be modified to contain mapping from the returned Clazz'es type parameter names to parameter names of `this`
	 * @return Clazz of `ofClazz` with generics set as specified by `this`
	 */
	Clazz<?> findGenericSuper(Class<?> ofClazz, Map<String, String> thisTypeToSubType) {
		if (clazz.equals(Object.class)) {
			return null;
		}
		if (clazz.equals(ofClazz)) {
			return this;
		}
		Clazz<?> superClass = getSuper();
		if (superClass != null && superClass.clazz != null) {
			Type genericSuperClass = clazz.getGenericSuperclass();
			Map<String, String> superTypeToSubType = genericSuperClass == null ? initialGenericSuperMap() : linkGenericTypes(thisTypeToSubType, genericSuperClass);
			Clazz<?> duperClass = superClass.findGenericSuper(ofClazz, superTypeToSubType);
			if (duperClass != null) {
				thisTypeToSubType.clear();
				thisTypeToSubType.putAll(superTypeToSubType);
				return duperClass;
			}
		}
		if (!ofClazz.isInterface()) {
			return null;
		}
		for (Type superInterface : clazz.getGenericInterfaces()) { // todo non generic interfaces?
			Map<String, String> superTypeToSubType = linkGenericTypes(thisTypeToSubType, superInterface);
			Clazz<?> duperInterface = Clazz.of(superInterface, genericMap).findGenericSuper(ofClazz, superTypeToSubType);
			if (duperInterface != null) {
				thisTypeToSubType.clear();
				thisTypeToSubType.putAll(superTypeToSubType);
				return duperInterface;
			}
		}
		return null;
	}

	// link type parameter names between a class/interface, and its super class/interface
	private static Map<String, String> linkGenericTypes(Map<String, String> thisTypeToSubType, Type parent) {
		if (!(parent instanceof ParameterizedType)) {
			return Collections.emptyMap();
		}

		ParameterizedType parentType = (ParameterizedType) parent;
		Class<?> parentClass = (Class<?>) parentType.getRawType();
		Type[] superTypes = parentClass.getTypeParameters();
		Type[] thisTypes = parentType.getActualTypeArguments();
		if (superTypes.length != thisTypes.length) {
			throw new IllegalArgumentException("type params count does not match actual type args count " + parentType);
		}

		Map<String, String> superTypeToSubType = new HashMap<>();
		for (int i = 0; i < thisTypes.length; i++) {
			if (thisTypes[i] instanceof TypeVariable) {
				if (!(superTypes[i] instanceof TypeVariable)) {
					throw new IllegalArgumentException("actual type arg is TypeVariable, but super type param is not " + superTypes[i].getClass());
				}
				String thisTypeName = ((TypeVariable<?>) thisTypes[i]).getName();
				if (thisTypeToSubType.containsKey(thisTypeName)) {
					String subTypeName = thisTypeToSubType.get(thisTypeName);
					superTypeToSubType.put(superTypes[i].getTypeName(), subTypeName);
				}
			}
		}
		return superTypeToSubType;
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

	private static List<Clazz> getMostSpecific(Type parentType, List<Clazz<?>> actualTypes, List<Clazz<?>> defaultTypes) {
		if (actualTypes.size() != defaultTypes.size()) {
			throw new IllegalArgumentException("mismatch in generics count " + actualTypes.size() + " and " + defaultTypes.size() + " for " + parentType);
		}

		List<Clazz> specificTypes = new ArrayList<>(actualTypes.size());
		for (int i = 0; i < actualTypes.size(); i++) {
			Clazz<?> moreSpecific = getMostSpecific(actualTypes.get(i), defaultTypes.get(i));
			if (moreSpecific == null) {
				throw new IllegalArgumentException("Conflicting generics " + actualTypes.get(i) + " and " + defaultTypes.get(i) + " at index " + i + " of " + parentType);
			}
			specificTypes.add(moreSpecific);
		}
		return specificTypes;
	}

	private static Clazz<?> getMostSpecific(Clazz<?> actualType, Clazz<?> defaultType) {
		if (actualType.clazz == defaultType.clazz) {
			return Clazz.ofClazzes(actualType.clazz, getMostSpecific(actualType.clazz, actualType.generics, defaultType.generics)); // todo loops?
		}

		Clazz<?> superClass, subClass;
		if (defaultType.clazz.isAssignableFrom(actualType.clazz)) {
			superClass = defaultType;
			subClass = actualType;
		} else if (actualType.clazz.isAssignableFrom(defaultType.clazz)) {
			superClass = actualType;
			subClass = defaultType;
		} else {
			return null;
		}

		if (subClass.generics.isEmpty()) {
			return subClass;
		}
		// get most specific generics from common parent, and replace matching generics in subClass
		// for example List<Object> and Collection<String> gives List<String>
		// see StringCollectionHolderFactory in tests
		Clazz<?> genericSuper = subClass.findGenericSuper(superClass.clazz);
		return Clazz.ofClazzes(subClass.clazz, getMostSpecific(actualType.clazz, actualType.generics, genericSuper.generics)); // todo
	}
}
