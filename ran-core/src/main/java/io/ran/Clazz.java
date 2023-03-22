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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
		return of(type, Collections.emptyMap(), Collections.emptySet());
	}

	public static Clazz of(Type type, Map<String, Clazz<?>> genericMap, Set<String> loopStop) {
		if (type == null) {
			return raw(null);
		}
		if (type instanceof Class) {
			return of((Class<?>) type, loopStop);
		}
		if (type instanceof GenericArrayType) {
			return of((GenericArrayType) type, genericMap, loopStop);
		}
		if (type instanceof WildcardType) {
			return of((WildcardType) type, genericMap, loopStop);
		}
		if (type instanceof ParameterizedType) {
			return of((ParameterizedType) type, genericMap, loopStop);
		}
		if (type instanceof TypeVariable) {
			return of((TypeVariable<?>) type, genericMap, loopStop);
		}
		throw new IllegalArgumentException("unhandled Type type: " + type.getClass());
	}

	public static Clazz of(Class<?> clazz, Set<String> loopStop) {
		if (clazz != null) {
			String name = clazz.getName();
			if (loopStop.contains(name)) {
				return raw(clazz);
			}
			Set<String> newLoopStop = new HashSet<>(loopStop);
			newLoopStop.add(name);
			return new Clazz(clazz, Stream.of(clazz.getTypeParameters()).map(type -> Clazz.of(type, Collections.emptyMap(), newLoopStop)).collect(Collectors.toList()));
		}
		return raw(null);
	}

	public static Clazz of(GenericArrayType genericArray, Map<String, Clazz<?>> genericMap, Set<String> loopStop) {
		Clazz<?> arrType = of(genericArray.getGenericComponentType(), genericMap, loopStop);
		return arrType.getArrayType();
	}

	public static Clazz of(WildcardType wildcardType, Map<String, Clazz<?>> genericMap, Set<String> loopStop) {
		Type[] bounds = wildcardType.getUpperBounds();
		if (bounds.length > 1) {
			throw new IllegalArgumentException("multiple bounds are not supported " + wildcardType);
		}
		return Clazz.of(bounds[0], genericMap, loopStop);
	}

	public static Clazz of(ParameterizedType parameterizedType, Map<String, Clazz<?>> genericMap, Set<String> loopStop) {
		List<Clazz> generics = Arrays.stream(parameterizedType.getActualTypeArguments())
				.map(t -> of(t, genericMap, loopStop))
				.collect(Collectors.toList());
		// wildcards can have weaker / different bounds than the original type,
		// so we choose the more specific out of default bounds and wildcard bounds
		// currently we correctly resolve only some basic cases
		List<Clazz> defaultGenerics = of((Class<?>) parameterizedType.getRawType(), loopStop).generics;
		List<Clazz> specificGenerics = getMostSpecific(parameterizedType, generics, defaultGenerics);
		return new Clazz((Class<?>) parameterizedType.getRawType(), specificGenerics);
	}

	public static Clazz of(TypeVariable<?> typeVariable, Map<String, Clazz<?>> genericMap, Set<String> loopStop) {
		if (genericMap.containsKey(typeVariable.getName())) {
			return genericMap.get(typeVariable.getName());
		}

		Type[] bounds = typeVariable.getBounds();
		if (bounds.length > 1) {
			throw new IllegalArgumentException("multiple bounds are not supported " + typeVariable);
		}

		String fullName = typeVariable.getGenericDeclaration().toString() + " " + typeVariable.getName();
		if (loopStop.contains(fullName)) {
			if (bounds[0] instanceof Class) {
				return raw((Class<?>) bounds[0]);
			}
			if (bounds[0] instanceof ParameterizedType) {
				return raw((Class<?>) ((ParameterizedType) bounds[0]).getRawType());
			}
			throw new RuntimeException("unhandled bound type " + bounds[0].getClass());
		}
		Set<String> newLoopStop = new HashSet<>(loopStop);
		newLoopStop.add(fullName);

		return of(bounds[0], genericMap, newLoopStop);
	}

	// todo check type count and bounds?
	public static <T> Clazz<T> ofClasses(Class<T> clazz, Class<?>... generics) {
		return new Clazz<>(clazz, Arrays.stream(generics).map(Clazz::of).toArray(Clazz[]::new));
	}

	public static Clazz ofClazzes(Class clazz, Clazz<?>... generics) {
		return new Clazz<>(clazz, generics);
	}

	public static Clazz ofClazzes(Class clazz, List<Clazz> generics) {
		return new Clazz(clazz, generics);
	}

	public static <T> Clazz<T> ofType(Class<T> clazz, Type type) { // todo
		Clazz<T> newClazz = new Clazz<>(clazz);
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
						genericMap.put(typeVariables[i].getName(), generics.get(i));
					}
				} else {
					// todo warning?
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
		if (isArray()) {
			return new Clazz<>(clazz.getComponentType(), generics);
		}
		return null;
	}

	public Clazz<?> getArrayType() {
		return new Clazz(Array.newInstance(clazz, 0).getClass(), generics);
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
		Type genericSuper = clazz.getGenericSuperclass();
		if (genericSuper != null) {
			if (genericSuper instanceof Class) {
				return raw((Class<?>) genericSuper);
			}
			return Clazz.of(clazz.getGenericSuperclass(), this.genericMap, Collections.emptySet());
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
			if (isPublicStatic(field) || (!includeNonProperties && !isPropertyField(field)) || field.getName().matches(COVERAGE_FIELD_PATTERN)) {
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
				Arrays.stream(working.clazz.getInterfaces())
						.flatMap(inter -> Stream.of(inter.getMethods()))
						.filter(m -> !m.isBridge())
						.filter(m -> result.keySet().stream().noneMatch(otherM -> getSignatureOfMethod(m).equals(getSignatureOfMethod(otherM))))
						.forEach(m -> {
					result.put(m, new ClazzMethod(this, finalWorking, m));
				});
			}
			working = working.getSuper();
		} while (working.clazz != null && !Object.class.equals(working.clazz));
		return new ClazzMethodList(result.values());
	}

	private static String getSignatureOfMethod(Method method) {
		// TODO: What about static/final/etc.? method.getModifiers()
		return method.getReturnType().getName()+" "+method.getName()+"("+Stream.of(method.getParameters()).map(p->p.getType().getName()).collect(Collectors.joining(","));
	}

	public static boolean isPropertyField(Field field) {
		return CamelHumpToken.is(field.getName()) && !Modifier.isTransient(field.getModifiers()) && field.getAnnotation(Relation.class) == null && !Modifier.isStatic(field.getModifiers());
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
		if (isVoid()) {
			return null;
		}
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

	/**
	 * @param ofClazz superclass or generic interface of `this`
	 * @return Clazz of `ofClazz` with generics set as specified by `this`
	 */
	Clazz<?> findGenericSuper(Class<?> ofClazz) {
		if (ofClazz.equals(Object.class)) { // todo I added this, is it an issue?
			return new Clazz<>(Object.class);
		}
		if (clazz.equals(Object.class)) {
			return null;
		}
		if (clazz.equals(ofClazz)) {
			return this;
		}
		Clazz<?> superClass = getSuper();
		if (superClass != null && superClass.clazz != null) {
			Clazz<?> duperClass = superClass.findGenericSuper(ofClazz);
			if (duperClass != null) {
				return duperClass;
			}
		}
		if (!ofClazz.isInterface()) {
			return null;
		}
		for (Type superInterface : clazz.getGenericInterfaces()) {
			Clazz<?> duperInterface = Clazz.of(superInterface, genericMap, Collections.emptySet()).findGenericSuper(ofClazz);
			if (duperInterface != null) {
				return duperInterface;
			}
		}
		return null;
	}

	public boolean equals(Clazz<?> clazz) { // todo what about other equals
		return this.clazz.equals(clazz.clazz);
	}

	public boolean declaresMethod(ClazzMethod cm) {
		return methods().find(cm).filter(m -> m.getDeclaringClazz().equals(this)).isPresent();
	}

	public boolean isVoid() {
		return clazz.equals(Void.class) || clazz.equals(void.class);
	}

	private static List<Clazz> getMostSpecific(ParameterizedType parentType, List<Clazz> actualTypes, List<Clazz> defaultTypes) {
		if (actualTypes.size() != defaultTypes.size()) {
			if (actualTypes.isEmpty()) {
				return defaultTypes;
			}
			if (defaultTypes.isEmpty()) {
				return actualTypes;
			}
			throw new IllegalArgumentException("mismatch in generics count " + actualTypes.size() + " and " + defaultTypes.size() + " for " + parentType);
		}

		List<Clazz> specificTypes = new ArrayList<>(actualTypes.size());
		for (int i = 0; i < actualTypes.size(); i++) {
			Clazz<?> mostSpecific = getMostSpecific(actualTypes.get(i), defaultTypes.get(i));
			if (mostSpecific == null) {
				throw new IllegalArgumentException("Conflicting generics " + actualTypes.get(i) + " and " + defaultTypes.get(i) + " at index " + i + " of " + parentType);
			}
			specificTypes.add(mostSpecific);
		}
		return specificTypes;
	}

	private static Clazz<?> getMostSpecific(Clazz<?> actualType, Clazz<?> defaultType) {
		// there can be <? extends Collection<String>> and <? extends List<?>> which should
		// resolve into List<String>. I managed to make it work, but it broke many other things
		if (actualType.clazz == defaultType.clazz) {
			return actualType;
		}
		if (defaultType.clazz.isAssignableFrom(actualType.clazz)) {
			return actualType;
		}
		if (actualType.clazz.isAssignableFrom(defaultType.clazz)) {
			return defaultType;
		}
		return null;
	}
}
