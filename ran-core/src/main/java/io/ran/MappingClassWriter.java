package io.ran;


import io.ran.token.Token;
import org.objectweb.asm.Opcodes;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MappingClassWriter extends AutoMapperClassWriter {
	Clazz mapperClazz;

	public MappingClassWriter(Class clazz) {
		super(clazz);
		try {
			postFix = "$Ran$Mapper";
			mapperClazz = Clazz.of(this.wrapperClazz.getInternalName() + postFix);
			visit(Opcodes.V1_8, Access.Public.getOpCode(), this.wrapperClazz.getInternalName() + postFix, this.wrapperClazz.generics.isEmpty() ? null : this.wrapperClazz.getSignature(), this.wrapperClazz.getInternalName(), new String[]{Clazz.of(Mapping.class).getInternalName()});
			field(Access.Private, "_changed", Clazz.of(boolean.class), false);
			field(Access.Private, "typeDescriber", Clazz.of(TypeDescriberImpl.class), null);

			MethodWriter w = method(Access.Public, new MethodSignature(mapperClazz, "_isChanged", Clazz.of(boolean.class)));
			w.load(0);
			w.getField(mapperClazz, "_changed", Clazz.of(boolean.class));
			w.returnPrimitive(Clazz.of(boolean.class));
			w.end();

			for (Constructor c : Arrays.asList(clazz.getConstructors())) {
				MethodWriter mw = method(Access.of(c.getModifiers()), new MethodSignature(c));
				int i = 0;
				if (c.getAnnotation(Inject.class) != null) {
					mw.addAnnotation(Clazz.of(Inject.class), true);
				}
				mw.load(0);
				for (Parameter p : Arrays.asList(c.getParameters())) {
					mw.load(++i);
				}

				mw.invoke(new MethodSignature(c));

				mw.load(0);
				mw.push(this.wrapperClazz);
				mw.invoke(TypeDescriberImpl.class.getMethod("getTypeDescriber", Class.class));
				mw.cast(Clazz.of(TypeDescriberImpl.class));
				mw.putfield(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));

				mw.returnNothing();
				mw.end();
			}
			build();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e); // This should never happen, so a runtime exception is apt
		}
	}

	protected void build() {
		createGetValue();
		createSetValue();
		createOther();
		createHydrator();
		createKeyGetter();
		createSetRelation();
		createGetRelation();
		createSetRelationNotLoaded();
		createSetterWrappers();
		createCopy();
	}

	private void createCopy() {
		try {
			MethodWriter w = method(Access.Public, new MethodSignature(Mapping.class.getMethod("copy", Object.class, Object.class)));
			w.defineVar("from", 1);
			w.defineVar("to", 2);
			w.load("to");
			w.cast(wrapperClazz);
			w.objectVar("toTyped");
			w.load("from");
			w.cast(wrapperClazz);
			w.objectVar("fromTyped");
			for (Field field : wrapperClazz.getPropertyFields()) {
				MethodSignature getter = new MethodSignature(wrapperClass.getMethod((field.getType().isPrimitive() && field.getType().equals(boolean.class) ? "is" : "get") + Token.get(field.getName()).javaGetter()));
				MethodSignature setter = new MethodSignature(wrapperClass.getMethod("set" + Token.get(field.getName()).javaGetter(), field.getType()));
				w.load("toTyped");
				w.load("fromTyped");
				w.invoke(getter);
				w.invoke(setter);
			}
			w.returnNothing();
			;
			w.end();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createSetterWrappers() {
		try {
			for (Method method : wrapperClass.getMethods()) {
				if (!method.getName().startsWith("set")) {
					continue;
				}
				Token column = Token.javaMethod(method.getName().substring(3));
				Field field = ((Clazz<?>) Clazz.of(wrapperClass)).getFields().stream().filter(f -> f.getName().equals(column.camelHump())).findFirst().orElseThrow(() -> new RuntimeException("Could not find field with name: " + column.camelHump() + " on " + wrapperClass.getName()));
				if (Clazz.isPropertyField(field)) {
					MethodWriter w = method(Access.Public, new MethodSignature(method));
					w.load(0);
					w.push(Boolean.TRUE);
					w.putfield(mapperClazz, "_changed", Clazz.of(boolean.class));
					w.load(0);
					w.load(1, Clazz.of(method.getParameters()[0].getType()));
					w.invokeSuper(new MethodSignature(method));
					w.returnNothing();
					w.end();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void createGetRelation() {
		try {
			MethodWriter ce1 = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getRelation", RelationDescriber.class)));
			ce1.load(0);
			ce1.load(0);
			ce1.load(1);
			ce1.invoke(Mapping.class.getMethod("_getRelation", Object.class, RelationDescriber.class));
			ce1.returnObject();
			ce1.end();

			MethodWriter ce2 = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getRelation", Object.class, RelationDescriber.class)));
			ce2.load(0);
			ce2.load(1);
			ce2.load(2);
			ce2.invoke(new MethodSignature(RelationDescriber.class.getMethod("getField")));
			ce2.invoke(Mapping.class.getMethod("_getRelation", Object.class, Token.class));
			ce2.returnObject();
			ce2.end();

			MethodWriter ceToken = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getRelation", Token.class)));
			ceToken.load(0);
			ceToken.load(0);
			ceToken.load(1);
			ceToken.invoke(Mapping.class.getMethod("_getRelation", Object.class, Token.class));
			ceToken.returnObject();
			ceToken.end();


			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getRelation", Object.class, Token.class)));
			ce.load(2);


			ce.invoke(new MethodSignature(Token.class.getMethod("snake_case")));
			ce.objectStore(4);
			List<String> fields = new ArrayList<>();
			for (Field field : wrapperClazz.getRelationFields()) {
				Token column = Token.camelHump(field.getName());
				Method getter = getGetter(field, column);
				Relation resolver = field.getAnnotation(Relation.class);
				if (resolver != null) {
					MethodSignature superMethod = new MethodSignature(mapperClazz, "_" + getter.getName() + "Super", Clazz.of(getter.getReturnType()));
					MethodWriter superRelationReader = method(Access.Public, superMethod);
					superRelationReader.load(0);
					superRelationReader.invokeSuper(new MethodSignature(getter));
					superRelationReader.returnObject();
					superRelationReader.end();

					fields.add(column.snake_case());
					ce.load(4);
					ce.push(column.snake_case());
					ce.invoke(new MethodSignature(String.class.getMethod("equals", Object.class)));

					ce.ifThen(c -> {

//						ce.cast(mapperClazz);
						ce.load(1);
						ce.ifInstanceOf(mapperClazz, i -> {
							i.load(1);
							i.cast(mapperClazz);
							i.invoke(superMethod);
						}, e -> {
							e.load(1);
							e.cast(wrapperClazz);
							e.invoke(getter);
						});
						ce.objectVar("superResult");
						ce.load("superResult");
						ce.ifNonNull(c2 -> {
							ce.load("superResult");
							ce.cast(Clazz.of(field));
							ce.returnObject();
						});
						ce.nullConst();
						ce.returnObject();
					});
				}

			}
			//void _getRelation(RelationDescriber relationDescriber, Object value);
			ce.throwException(Clazz.of(RuntimeException.class), mw -> {
				mw.newInstance(Clazz.of(StringBuilder.class));
				mw.dup();
				mw.invoke(new MethodSignature(StringBuilder.class.getConstructor()));
				mw.push("Could not find field: ");
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.load(4);
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.push(". Must be one of: " + String.join(", ", fields));
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.invoke(StringBuilder.class.getMethod("toString"));
			});
			ce.end();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void createSetRelation() {
		try {
			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_setRelation", RelationDescriber.class, Object.class)));
			ce.load(1);
			ce.invoke(new MethodSignature(RelationDescriber.class.getMethod("getField")));
			ce.invoke(new MethodSignature(Token.class.getMethod("snake_case")));
			ce.objectStore(3);
			List<String> fields = new ArrayList<>();
			for (Field field : wrapperClazz.getRelationFields()) {
				Token column = Token.camelHump(field.getName());
				Method setter = getSetter(field);
				MethodSignature setterInfo = new MethodSignature(setter);
				Relation resolver = field.getAnnotation(Relation.class);
				if (resolver != null) {
					fields.add(column.snake_case());
					ce.load(3);
					ce.push(column.snake_case());
					ce.invoke(new MethodSignature(String.class.getMethod("equals", Object.class)));

					ce.ifThen(c -> {
						c.load(0);
						c.load(2);
						c.cast(Clazz.of(field));
						c.invoke(setterInfo);
						c.load(0);
						c.push(Boolean.TRUE);
						c.putfield(mapperClazz, "_relationLoaded" + column.CamelBack(), Clazz.of(boolean.class));
						c.returnNothing();
					});
				}

			}
			//void _setRelation(RelationDescriber relationDescriber, Object value);
			ce.throwException(Clazz.of(RuntimeException.class), mw -> {
				mw.newInstance(Clazz.of(StringBuilder.class));
				mw.dup();
				mw.invoke(new MethodSignature(StringBuilder.class.getConstructor()));
				mw.push("Could not find field: ");
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.load(3);
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.push(". Must be one of: " + String.join(", ", fields));
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.invoke(StringBuilder.class.getMethod("toString"));
			});
			ce.end();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void createSetRelationNotLoaded() {
		try {
			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_setRelationNotLoaded", RelationDescriber.class)));
			ce.load(1);
			ce.invoke(new MethodSignature(RelationDescriber.class.getMethod("getField")));
			ce.invoke(new MethodSignature(Token.class.getMethod("snake_case")));
			ce.objectStore(2);
			List<String> fields = new ArrayList<>();
			for (Field field : wrapperClazz.getRelationFields()) {
				Token column = Token.camelHump(field.getName());
				Relation resolver = field.getAnnotation(Relation.class);
				if (resolver != null) {
					fields.add(column.snake_case());
					ce.load(2);
					ce.push(column.snake_case());
					ce.invoke(new MethodSignature(String.class.getMethod("equals", Object.class)));

					ce.ifThen(c -> {
						c.load(0);
						c.load(0);
						c.push(Boolean.FALSE);
						c.putfield(mapperClazz, "_relationLoaded" + column.CamelBack(), Clazz.of(boolean.class));
						c.returnNothing();
					});

				}
			}

			ce.throwException(Clazz.of(RuntimeException.class), mw -> {
				mw.newInstance(Clazz.of(StringBuilder.class));
				mw.dup();
				mw.invoke(new MethodSignature(StringBuilder.class.getConstructor()));
				mw.push("Could not find field: ");
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.load(2);
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.push(". Must be one of: " + String.join(", ", fields));
				mw.invoke(StringBuilder.class.getMethod("append", String.class));
				mw.invoke(StringBuilder.class.getMethod("toString"));
			});
			ce.end();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}


	private void createKeyGetter() {
		try {
			MethodWriter w = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getKey")));
			w.load(0);
			w.load(0);
			w.invoke(new MethodSignature(Mapping.class.getMethod("_getKey", Object.class)));
			w.returnObject();
			w.end();

			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getKey", Object.class)));

			Optional<KeySet> optional = wrapperClazz.getKeys().getPrimaryOptionally();
			if (optional.isPresent()) {
				KeySet primaryKey = optional.get();
				ce.defineVar(Clazz.of(CompoundKey.class));
				ce.invoke(new MethodSignature(CompoundKey.class.getMethod("get")));
				ce.objectStore(2);
				primaryKey.forEach(field -> {
					try {
						ce.objectLoad(2);
						ce.push(field.getProperty().getToken().snake_case());
						ce.push(field.getProperty().getType().getBoxed());
						ce.load(1);
						ce.cast(wrapperClazz);
						ce.invoke(new MethodSignature(wrapperClass.getMethod("get" + field.getProperty().getToken().javaGetter())));
						if (field.getProperty().getType().isPrimitive() || field.getProperty().getType().clazz.isEnum()) {
							ce.box(field.getProperty().getType().getBoxed());
						}
						ce.invoke(new MethodSignature(CompoundKey.class.getMethod("add", String.class, Class.class, Object.class)));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				ce.objectLoad(2);
				ce.returnObject();
				ce.end();

			} else {
				ce.invoke(CompoundKey.class.getMethod("get"));
				ce.returnObject();
				ce.end();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createHydrator() {
		try {
			for (Field field : wrapperClazz.getPropertyFields()) {
				if (field.getAnnotation(Serialized.class) != null) {
					field(Access.Private, "_serializerFor" + field.getName(), Clazz.of(ISerializer.class), null);

					MethodWriter mv = method(Access.Public, new MethodSignature(wrapperClazz, "_serializerInject" + field.getName(), Clazz.getVoid(), Clazz.ofClazzes(field.getAnnotation(Serialized.class).serializer())));
					mv.addAnnotation(Clazz.of(Inject.class), true);

					{
						mv.load(0);
						mv.load(1);
						mv.cast(Clazz.of(ISerializer.class));
						mv.putfield(getSelf(), "_serializerFor" + field.getName(), Clazz.of(ISerializer.class));
						mv.returnNothing();
						mv.end();
					}
				}
			}

			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("hydrate", Object.class, ObjectMapHydrator.class)));
			ce.defineVar("this", 0);
			ce.defineVar("object", 1);
			ce.defineVar("hydratorUntyped", 2);
			MethodWriter ce1 = method(Access.Public, new MethodSignature(Mapping.class.getMethod("hydrate", ObjectMapHydrator.class)));
			ce1.defineVar("this", 0);
			ce1.defineVar("hydratorUntyped", 1);

			MethodWriter cec = method(Access.Public, new MethodSignature(Mapping.class.getMethod("columnize", ObjectMapColumnizer.class)));
			cec.defineVar("this", 0);
			cec.defineVar("columnizer", 1);
			cec.load("this");
			cec.load("this");
			cec.cast(Clazz.of(Object.class));
			cec.load("columnizer");
			cec.invoke(new MethodSignature(Mapping.class.getMethod("columnize", Object.class, ObjectMapColumnizer.class)));
			cec.returnNothing();
			cec.end();


			ce.load("object");
			ce.cast(wrapperClazz);
			ce.objectVar("clazz");
			ce.load("hydratorUntyped");
			ce.cast(Clazz.of(ObjectMapHydrator.class));
			ce.objectVar("hydrator");

			ce1.load("this");
			ce1.cast(mapperClazz);
			ce1.objectVar("clazz");
			ce1.load("hydratorUntyped");
			ce1.cast(Clazz.of(ObjectMapHydrator.class));
			ce1.objectVar("hydrator");


			cec = method(Access.Public, new MethodSignature(Mapping.class.getMethod("columnize", Object.class, ObjectMapColumnizer.class)));
			cec.defineVar("this", 0);
			cec.defineVar("object", 1);
			cec.defineVar("columnizer", 2);
			cec.load("object");
			cec.cast(wrapperClazz);
			cec.objectVar("clazz");

			for (Field field : wrapperClazz.getPropertyFields()) {
				Clazz<?> fieldClazz = Clazz.of(field);
				Token column = Token.camelHump(field.getName());
				Method fieldMethod = getGetter(field, column);
				MethodSignature getterInfo = new MethodSignature(fieldMethod);
				Method fieldMethodSetter = getSetter(field);
				MethodSignature setterInfo = new MethodSignature(fieldMethodSetter);


				if (field.getAnnotation(Serialized.class) != null) {
					ce.load("clazz");
					ce.load("this");
					ce.getField(getSelf(), "_serializerFor" + field.getName(), Clazz.of(ISerializer.class));
					{ // Pushes the field type to the stack
						ce.push(Clazz.of(field.getType()));
					}
					{ // Pushes the string value of the specified property to the stack
						ce.load("hydrator");
						ce.load("this");
						ce.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
						ce.push(Token.camelHump(field.getName()).snake_case());
						ce.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));
						ce.invoke(ObjectMapHydrator.class.getMethod("getString", Property.class));
					}
					ce.invoke(ISerializer.class.getMethod("deserialize", Class.class, String.class));
					ce.cast(Clazz.of(field));

					ce1.load("clazz");
					ce1.load("this");
					ce1.getField(getSelf(), "_serializerFor" + field.getName(), Clazz.of(ISerializer.class));
					{
						ce1.push(Clazz.of(field.getType()));
					}
					{
						ce1.load("hydrator");
						ce1.load("this");
						ce1.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
						ce1.push(Token.camelHump(field.getName()).snake_case());
						ce1.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));
						ce1.invoke(ObjectMapHydrator.class.getMethod("getString", Property.class));
					}
					ce1.invoke(ISerializer.class.getMethod("deserialize", Class.class, String.class));
					ce1.cast(Clazz.of(field));

					cec.load("columnizer");
					cec.load("this");
					cec.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
					cec.push(Token.camelHump(field.getName()).snake_case());
					cec.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));
					cec.load("this");
					cec.getField(getSelf(), "_serializerFor" + field.getName(), Clazz.of(ISerializer.class));
					cec.load("object");
					cec.cast(wrapperClazz);
					cec.invoke(getterInfo);
					cec.invoke(ISerializer.class.getMethod("serialize", Object.class));
					cec.invoke(ObjectMapColumnizer.class.getMethod("set", Property.class, String.class));
				} else {
					ce.load("clazz");
					ce.load("hydrator");
					ce.load("this");
					ce.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
					ce.push(Token.camelHump(field.getName()).snake_case());
					ce.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));


					ce1.load("this");
					ce1.load("hydrator");
					ce1.load("this");
					ce1.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
					ce1.push(Token.camelHump(field.getName()).snake_case());
					ce1.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));

					cec.load("columnizer");
					cec.load("this");
					cec.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
					cec.push(Token.camelHump(field.getName()).snake_case());
					cec.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));

					cec.load("object");
					cec.cast(wrapperClazz);
					cec.invoke(getterInfo);

					if (field.getType() == byte[].class) {
						ce.invoke(ObjectMapHydrator.class.getMethod("getBytes", Property.class));
						ce.cast(Clazz.of(field));

						ce1.invoke(ObjectMapHydrator.class.getMethod("getBytes", Property.class));
						ce1.cast(Clazz.of(field));

						cec.invoke(ObjectMapColumnizer.class.getMethod("set", Property.class, byte[].class));
					} else if (field.getType().isEnum()) {
						ce.push(Clazz.of(field));
						ce.invoke(ObjectMapHydrator.class.getMethod("getEnum", Property.class, Class.class));
						ce.cast(Clazz.of(field));

						ce1.push(Clazz.of(field));
						ce1.invoke(ObjectMapHydrator.class.getMethod("getEnum", Property.class, Class.class));
						ce1.cast(Clazz.of(field));

						cec.invoke(ObjectMapColumnizer.class.getMethod("set", Property.class, Enum.class));
					} else if (fieldClazz.isPrimitive() || fieldClazz.isBoxedPrimitive()) {
						ce.invoke(ObjectMapHydrator.class.getMethod("get" + fieldClazz.getBoxed().getSimpleName(), Property.class));

						ce1.invoke(ObjectMapHydrator.class.getMethod("get" + fieldClazz.getBoxed().getSimpleName(), Property.class));
						if (fieldMethodSetter.getParameterTypes()[0].isPrimitive()) {
							ce.unbox(fieldClazz);
							ce1.unbox(fieldClazz);
						}
						if (fieldMethod.getReturnType().isPrimitive()) {
							cec.box(fieldClazz);
						}
						cec.invoke(new MethodSignature(ObjectMapColumnizer.class.getMethod("set", Property.class, fieldClazz.getBoxed().clazz)));
					} else if (Collection.class.isAssignableFrom(field.getType())) {
						ce.push(fieldClazz.generics.get(0));
						ce.push(fieldClazz);
						ce.invoke(ObjectMapHydrator.class.getMethod("getCollection", Property.class, Class.class, Class.class));

						ce1.push(fieldClazz.generics.get(0));
						ce1.push(fieldClazz);
						ce1.invoke(ObjectMapHydrator.class.getMethod("getCollection", Property.class, Class.class, Class.class));

						cec.cast(Clazz.of(Collection.class));
						cec.invoke(ObjectMapColumnizer.class.getMethod("set", Property.class, Collection.class));
					} else {
						ce.invoke(ObjectMapHydrator.class.getMethod("get" + fieldClazz.getSimpleName(), Property.class));
						ce1.invoke(ObjectMapHydrator.class.getMethod("get" + fieldClazz.getSimpleName(), Property.class));
						cec.invoke(ObjectMapColumnizer.class.getMethod("set", Property.class, fieldClazz.clazz));
					}
				}

				ce1.invokeSuper(setterInfo);
				ce.invoke(setterInfo);

			}
			ce1.returnNothing();
			ce1.end();
			ce.returnNothing();
			ce.end();
			cec.returnNothing();
			cec.end();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Method getSetter(Field field) throws NoSuchMethodException {
		return wrapperClass.getMethod("set" + Token.camelHump(field.getName()).javaGetter(), field.getType());
	}

	private Method getGetter(Field field, Token column) throws NoSuchMethodException {
		return wrapperClass.getMethod((field.getType().isPrimitive() && field.getType().equals(boolean.class) ? "is" : "get") + column.javaGetter());
	}

	private void createSetValue() {
		try {
			MethodWriter w = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_setValue", Property.class, Object.class)));
			w.load(0);
			w.load(0);
			w.load(1);
			w.load(2);
			w.invoke(new MethodSignature(Mapping.class.getMethod("_setValue", Object.class, Property.class, Object.class)));
			w.returnNothing();
			w.end();

			MethodWriter gvce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_setValue", Object.class, Property.class, Object.class)));

			gvce.load(1); // instance
			gvce.cast(wrapperClazz); // cast instance
			gvce.objectStore(4); // store cast instance into slot 4

			List<String> fields = new ArrayList<>();

			for (Field field : wrapperClazz.getPropertyFields()) {
				Method fieldMethod = getSetter(field);
				gvce.load(2);
				fields.add(Token.camelHump(field.getName()).snake_case());
				gvce.push(Token.camelHump(field.getName()).snake_case());
				gvce.invoke(Property.class.getMethod("matchesSnakeCase", String.class));
				gvce.ifThen(c -> {
					c.load(4);
					c.load(3);

//					if (field.getType().isPrimitive()) {
//						c.box(Clazz.of(field));
//					}
					if (Clazz.of(field).isPrimitive()) {
						c.unbox(Clazz.of(field));
					} else {
						c.cast(Clazz.of(field));
					}
					c.invoke(fieldMethod);
					c.returnNothing();
				});
			}
			{

				gvce.throwException(Clazz.of(RuntimeException.class), mw -> {
					mw.newInstance(Clazz.of(StringBuilder.class));
					mw.dup();
					mw.invoke(new MethodSignature(StringBuilder.class.getConstructor()));
					mw.push("Could not find field: ");
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.load(2);
					mw.invoke(Property.class.getMethod("getSnakeCase"));
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.push(". Must be one of: " + String.join(", ", fields));
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.invoke(StringBuilder.class.getMethod("toString"));
				});
				gvce.end();

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void createGetValue() {
		try {
			MethodWriter w = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getValue", Property.class)));
			w.load(0);
			w.load(0);
			w.load(1);
			w.invoke(new MethodSignature(Mapping.class.getMethod("_getValue", Object.class, Property.class)));
			w.returnObject();
			w.end();

			MethodWriter gvce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getValue", Object.class, Property.class)));
//			gvce.load(2);
//			gvce.invoke(Property.class.getMethod("getToken"));
//			gvce.invoke(Token.class.getMethod("snake_case"));
//			gvce.objectStore(3);
			gvce.load(1);
			gvce.cast(wrapperClazz);
			gvce.objectStore(3);

			List<String> fields = new ArrayList<>();

			for (Field field : wrapperClazz.getPropertyFields()) {
				Token column = Token.camelHump(field.getName());
				Method fieldMethod = getGetter(field, column);
				gvce.load(2);
				fields.add(Token.camelHump(field.getName()).snake_case());
				gvce.push(Token.camelHump(field.getName()).snake_case());
				gvce.invoke(Property.class.getMethod("matchesSnakeCase", String.class));
				gvce.ifThen(c -> {
					gvce.load(3);
					gvce.invoke(fieldMethod);
					if (field.getType().isPrimitive()) {
						gvce.box(Clazz.of(field));
					}
					gvce.returnObject();
				});
			}
			{

				gvce.throwException(Clazz.of(RuntimeException.class), mw -> {
					mw.newInstance(Clazz.of(StringBuilder.class));
					mw.dup();
					mw.invoke(new MethodSignature(StringBuilder.class.getConstructor()));
					mw.push("Could not find field: ");
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.load(2);
					mw.invoke(Property.class.getMethod("getSnakeCase"));
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.push(". Must be one of: " + String.join(", ", fields));
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.invoke(StringBuilder.class.getMethod("toString"));
				});
				gvce.end();

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void createOther() {
		try {

			Clazz resolverClazz = Clazz.ofClazzes(Resolver.class);
			field(Access.Private, "_resolver", resolverClazz, null);

			MethodWriter mv = method(Access.Public, new MethodSignature(wrapperClazz, "_resolverInject", Clazz.getVoid(), Clazz.ofClazzes(Resolver.class)));
			mv.addAnnotation(Clazz.of(Inject.class), true);

			{
				mv.load(0);
				mv.load(1);
				mv.cast(Clazz.of(Resolver.class));
				mv.putfield(getSelf(), "_resolver", resolverClazz);
				mv.returnNothing();
				mv.end();
			}

			for (Field field : wrapperClazz.getRelationFields()) {
				Token column = Token.camelHump(field.getName());
				Method fieldMethod = getGetter(field, column);
				Method setterMethod = getSetter(field);
				Relation resolver = field.getAnnotation(Relation.class);
				if (resolver != null) {
					String relationLoaded = "_relationLoaded" + column.CamelBack();
					field(Access.Private, relationLoaded, Clazz.of(boolean.class), false);
					Method fieldMethodSetter = wrapperClass.getMethod("set" + column.CamelBack(), field.getType());
					boolean isCollection = Collection.class.isAssignableFrom(field.getType());
					String fieldName = column.camelHump();
					Class<?> elementType = isCollection ? resolver.collectionElementType() : field.getType();
					MethodSignature sig = new MethodSignature(fieldMethod);
					MethodWriter ce = method(Access.of(fieldMethod.getModifiers()), sig);
					MethodSignature setterSignature = new MethodSignature(setterMethod);
					setterSignature.setOwner(mapperClazz);
					MethodWriter setterWriter = method(Access.of(fieldMethod.getModifiers()), setterSignature);
					{ // Write the setter method
						{ // super.set${fieldName}(${firstParameter});
							setterWriter.load(0);
							setterWriter.load(1);
							MethodSignature superSetterSignature = new MethodSignature(setterMethod);
							setterWriter.invokeSuper(superSetterSignature);
						}
						{
							// this._relationsLoaded${fieldName} = true;
							setterWriter.load(0);
							setterWriter.push(Boolean.TRUE);
							setterWriter.putfield(mapperClazz, "_relationLoaded" + column.CamelBack(), Clazz.of(boolean.class));
						}
						{   // return;
							setterWriter.returnNothing();
							setterWriter.end();
						}
					}
					{ // Write the getter method
						// if (super.get{column.CamelBack()}() == null
//						ce.load(0);
//						ce.invokeSuper(sig);

						ce.load(0);
						ce.getField(getSelf(), relationLoaded, Clazz.of(boolean.class));
						ce.ifNegateBoolean(c -> {
							MethodSignature resolverMethod = null;
							if (isCollection) {
								resolverMethod = new MethodSignature(Clazz.ofClasses(Resolver.class, wrapperClass, elementType), "getCollection", Clazz.of(Collection.class), Clazz.of(Class.class), Clazz.of(String.class), Clazz.of(Object.class));
							} else {
								resolverMethod = new MethodSignature(Clazz.of(Resolver.class), "get", Clazz.of(Object.class), Clazz.of(Class.class), Clazz.of(String.class), Clazz.of(Object.class));
							}

							c.load(0); // this

							c.load(0);

							c.getField(getSelf(), "_resolver", Clazz.of(Resolver.class));

							// call method resolverMethod with clazz, Token.camelHump(fieldName).snake_case() and this
							c.push(wrapperClazz);
							c.push(Token.camelHump(fieldName).snake_case());
							c.load(0);
							c.invoke(resolverMethod);

							// cast the result of resolverMethod._resolver to Clazz.of(field)
							c.cast(Clazz.of(field));

							// invoke setter of the field with the cast object
							c.invoke(fieldMethodSetter);
						});


						// return super.get{column.CamelBack()}()
						ce.load(0);
						ce.invokeSuper(sig);
						ce.returnObject();

						// end function
						ce.end();
					}
				}

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
