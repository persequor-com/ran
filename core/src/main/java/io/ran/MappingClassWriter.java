package io.ran;


import io.ran.token.Token;
import org.objectweb.asm.Opcodes;

import javax.inject.Inject;
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
		postFix = "Mapper";
		mapperClazz = Clazz.of(this.clazz.getInternalName()+postFix);
		visit(Opcodes.V1_8, Access.Public.getOpCode(), this.clazz.getInternalName()+postFix, this.clazz.generics.isEmpty() ? null : this.clazz.getSignature(), this.clazz.getInternalName(), new String[]{Clazz.of(Mapping.class).getInternalName()});
		field(Access.Private, "_changed", Clazz.of(boolean.class), false);

		MethodWriter w = method(Access.Public, new MethodSignature(mapperClazz, "_isChanged", Clazz.of(boolean.class)));
		w.load(0);
		w.getField(mapperClazz, "_changed", Clazz.of(boolean.class));
		w.returnPrimitive(Clazz.of(boolean.class));
		w.end();

		Arrays.asList(clazz.getConstructors()).forEach(c -> {
			MethodWriter mw = method(Access.of(c.getModifiers()), new MethodSignature(c));
			int i = 0;
			for (Parameter p : Arrays.asList(c.getParameters())) {
				mw.load(++i);
			}
			mw.load(0);
			mw.invoke(new MethodSignature(c));
			mw.returnNothing();
			mw.end();
		});
		build();
	}

	protected void build() {
		createGetValue();
		createOther();
		createHydrator();
		createKeyGetter();
		createSetRelation();
		createGetRelation();
		createSetRelationNotLoaded();
		createSetterWrappers();
	}

	private void createSetterWrappers() {
		try {
			for (Method method : aClass.getMethods()) {
				if (!method.getName().startsWith("set")) {
					continue;
				}
				Token column = Token.javaMethod(method.getName().substring(3));
				Field field = ((Clazz<?>)Clazz.of(aClass)).getFields().stream().filter(f -> f.getName().equals(column.camelHump())).findFirst().orElseThrow(() -> new RuntimeException("Could not find field with name: "+column.camelHump()+" on "+aClass.getName()));
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
			throw  new RuntimeException(e);
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



			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("_getRelation", Object.class, Token.class)));
			ce.load(2);

			ce.invoke(new MethodSignature(Token.class.getMethod("snake_case")));
			ce.objectStore(4);
			List<String> fields = new ArrayList<>();
			for (Field field : clazz.getRelationFields()) {
				Token column = Token.camelHump(field.getName());
				Method getter = getGetter(field, column);
				Relation resolver = field.getAnnotation(Relation.class);
				if (resolver != null) {
					MethodSignature superMethod = new MethodSignature(getter).setOwner(mapperClazz).setName("_" + getter.getName() + "Super");
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
						ce.load(1);
						ce.cast(mapperClazz);
						ce.invoke(superMethod);
						ce.ifNonNull(c2 -> {
							ce.load(1);
							ce.cast(mapperClazz);
							ce.invoke(superMethod);
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
				mw.push(". Must be one of: "+String.join(", ",fields));
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
			for (Field field : clazz.getRelationFields()) {
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
				mw.push(". Must be one of: "+String.join(", ",fields));
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
			for (Field field : clazz.getRelationFields()) {
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
				mw.push(". Must be one of: "+String.join(", ",fields));
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

			Optional<KeySet> optional = clazz.getKeys().getPrimaryOptionally();
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
						ce.cast(clazz);
						ce.invoke(new MethodSignature(aClass.getMethod("get" + field.getProperty().getToken().javaGetter())));
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
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createHydrator() {
		try {
			MethodWriter ce1 = method(Access.Public, new MethodSignature(Mapping.class.getMethod("hydrate", ObjectMapHydrator.class)));

			MethodWriter cec = method(Access.Public, new MethodSignature(Mapping.class.getMethod("columnize", ObjectMapColumnizer.class)));
			cec.load(0);
			cec.load(0);
			cec.cast(Clazz.of(Object.class));
			cec.load(1);
			cec.invoke(new MethodSignature(Mapping.class.getMethod("columnize", Object.class, ObjectMapColumnizer.class)));
			cec.returnNothing();
			cec.end();


			MethodWriter ce = method(Access.Public, new MethodSignature(Mapping.class.getMethod("hydrate", Object.class, ObjectMapHydrator.class)));
			ce.load(1);
			ce.cast(clazz);
			ce.objectStore(3);

			ce1.load(0);
			ce1.cast(mapperClazz);
			ce1.objectStore(3);


			cec = method(Access.Public, new MethodSignature(Mapping.class.getMethod("columnize", Object.class, ObjectMapColumnizer.class)));
			cec.load(1);
			cec.cast(clazz);
			cec.objectStore(3);

			for (Field field : clazz.getPropertyFields()) {
				Clazz<?> fieldClazz = Clazz.of(field);
				Token column = Token.camelHump(field.getName());
				Method fieldMethod = getGetter(field, column);
				MethodSignature getterInfo = new MethodSignature(fieldMethod);
				Method fieldMethodSetter = getSetter(field);
				MethodSignature setterInfo = new MethodSignature(fieldMethodSetter);
				ce.objectLoad(3);
				ce.load(2);
				ce.push(Token.camelHump(field.getName()).snake_case());
				ce.invoke(Token.class.getMethod("snake_case", String.class));

				ce1.objectLoad(3);
				ce1.load(1);
				ce1.push(Token.camelHump(field.getName()).snake_case());
				ce1.invoke(Token.class.getMethod("snake_case", String.class));

				cec.objectLoad(3);
				cec.load(2);
				cec.push(Token.camelHump(field.getName()).snake_case());
				cec.invoke(Token.class.getMethod("snake_case", String.class));
				cec.load(1);
				cec.cast(clazz);
				cec.invoke(getterInfo);

				if (field.getType() == byte[].class) {
					ce.invoke(ObjectMapHydrator.class.getMethod("getBytes", Token.class));
					ce.cast(Clazz.of(field));

					ce1.invoke(ObjectMapHydrator.class.getMethod("getBytes", Token.class));
					ce1.cast(Clazz.of(field));

					cec.invoke(ObjectMapColumnizer.class.getMethod("set", Token.class, byte[].class));
				} else if (field.getType().isEnum()) {
					ce.push(Clazz.of(field));
					ce.invoke(ObjectMapHydrator.class.getMethod("getEnum", Token.class, Class.class));
					ce.cast(Clazz.of(field));

					ce1.push(Clazz.of(field));
					ce1.invoke(ObjectMapHydrator.class.getMethod("getEnum", Token.class, Class.class));
					ce1.cast(Clazz.of(field));

					cec.invoke(ObjectMapColumnizer.class.getMethod("set", Token.class, Enum.class));
				} else if (fieldClazz.isPrimitive() || fieldClazz.isBoxedPrimitive()) {
					ce.invoke(ObjectMapHydrator.class.getMethod("get"+fieldClazz.getBoxed().getSimpleName(), Token.class));
					ce1.invoke(ObjectMapHydrator.class.getMethod("get"+fieldClazz.getBoxed().getSimpleName(), Token.class));
					if (fieldMethodSetter.getParameterTypes()[0].isPrimitive()) {
						ce.unbox(fieldClazz);
						ce1.unbox(fieldClazz);
					}
					if (fieldMethod.getReturnType().isPrimitive()) {
						cec.box(fieldClazz);
					}
					cec.invoke(new MethodSignature(ObjectMapColumnizer.class.getMethod("set", Token.class, fieldClazz.getBoxed().clazz)));
				} else if (Collection.class.isAssignableFrom(field.getType())) {
					ce.push(fieldClazz.generics.get(0));
					ce.push(fieldClazz);
					ce.invoke(ObjectMapHydrator.class.getMethod("getCollection", Token.class, Class.class, Class.class));

					ce1.push(fieldClazz.generics.get(0));
					ce1.push(fieldClazz);
					ce1.invoke(ObjectMapHydrator.class.getMethod("getCollection", Token.class, Class.class, Class.class));

					cec.cast(Clazz.of(Collection.class));
					cec.invoke(ObjectMapColumnizer.class.getMethod("set", Token.class, Collection.class));
				} else {
					ce.invoke(ObjectMapHydrator.class.getMethod("get" + fieldClazz.getSimpleName(), Token.class));
					ce1.invoke(ObjectMapHydrator.class.getMethod("get" + fieldClazz.getSimpleName(), Token.class));
					cec.invoke(ObjectMapColumnizer.class.getMethod("set", Token.class, fieldClazz.clazz));
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
			throw  new RuntimeException(e);
		}
	}

	private Method getSetter(Field field) throws NoSuchMethodException {
		return aClass.getMethod("set" + Token.camelHump(field.getName()).javaGetter(), field.getType());
	}

	private Method getGetter(Field field, Token column) throws NoSuchMethodException {
		return aClass.getMethod((field.getType().isPrimitive() && field.getType().equals(boolean.class) ? "is" : "get") + column.javaGetter());
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
			gvce.load(2);
			gvce.invoke(Property.class.getMethod("getToken"));
			gvce.invoke(Token.class.getMethod("snake_case"));
			gvce.objectStore(3);
			gvce.load(1);
			gvce.cast(clazz);
			gvce.objectStore(4);

			List<String> fields = new ArrayList<>();

			for (Field field : clazz.getPropertyFields()) {
				Token column = Token.camelHump(field.getName());
				Method fieldMethod = getGetter(field, column);
				gvce.load(3);
				fields.add(Token.camelHump(field.getName()).snake_case());
				gvce.push(Token.camelHump(field.getName()).snake_case());
				gvce.invoke(String.class.getMethod("equals", Object.class));
				gvce.ifThen(c -> {
					gvce.load(4);
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
					mw.load(3);
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.push(". Must be one of: "+String.join(", ",fields));
					mw.invoke(StringBuilder.class.getMethod("append", String.class));
					mw.invoke(StringBuilder.class.getMethod("toString"));
				});
				gvce.end();

			}
		} catch (Exception e) {
			throw  new RuntimeException(e);
		}

	}

	private void createOther() {
		try {

			Clazz resolverClazz = Clazz.ofClazzes(Resolver.class);
			field(Access.Private, "_resolver", resolverClazz, null);

			MethodWriter mv = method(Access.Public, new MethodSignature(clazz, "_resolverInject", Clazz.getVoid(), Clazz.ofClazzes(Resolver.class)));
			mv.addAnnotation(Clazz.of(Inject.class), true);

			{
				mv.load(0);
				mv.load(1);
				mv.cast(Clazz.of(Resolver.class));
				mv.putfield(getSelf(), "_resolver", resolverClazz);
				mv.returnNothing();
				mv.end();
			}

			for (Field field : clazz.getRelationFields()) {
				Token column = Token.camelHump(field.getName());
				Method fieldMethod = getGetter(field, column);
				Relation resolver = field.getAnnotation(Relation.class);
				if (resolver != null) {
					String relationLoaded = "_relationLoaded" + column.CamelBack();
					field(Access.Private, relationLoaded, Clazz.of(boolean.class), false);
					Method fieldMethodSetter = aClass.getMethod("set" + column.CamelBack(), field.getType());
					boolean isCollection = Collection.class.isAssignableFrom(field.getType());
					String fieldName = column.camelHump();
					Class<?> elementType = isCollection ? resolver.collectionElementType() : field.getType();
					MethodSignature sig = new MethodSignature(fieldMethod);
					MethodWriter ce = method(Access.of(fieldMethod.getModifiers()), sig);
					{
						// if (super.get{column.CamelBack()}() == null
						ce.load(0);
						ce.invokeSuper(sig);
						ce.ifNull(innerIf -> {
							// && !_relationLoaded{column.CamelBack()} ) {
							innerIf.load(0);
							innerIf.getField(getSelf(), relationLoaded, Clazz.of(boolean.class));
							innerIf.ifNegateBoolean(c -> {
								MethodSignature resolverMethod = null;
								if (isCollection) {
									resolverMethod = new MethodSignature(Clazz.ofClasses(Resolver.class,aClass, elementType) ,"getCollection", Clazz.of(Collection.class), Clazz.of(Class.class), Clazz.of(String.class), Clazz.of(Object.class));
								} else {
									resolverMethod = new MethodSignature(Clazz.of(Resolver.class) ,"get" , Clazz.of(Object.class),Clazz.of(Class.class), Clazz.of(String.class), Clazz.of(Object.class));
								}

								c.load(0); // this

								c.load(0);

								c.getField(getSelf(), "_resolver", Clazz.of(Resolver.class));

								// call method resolverMethod with clazz, Token.camelHump(fieldName).snake_case() and this
								c.push(clazz);
								c.push(Token.camelHump(fieldName).snake_case());
								c.load(0);
								c.invoke(resolverMethod);

								// cast the result of resolverMethod._resolver to Clazz.of(field)
								c.cast(Clazz.of(field));

								// invoke setter of the field with the cast object
								c.invoke(fieldMethodSetter);

								// _relationLoaded{column.CamelBack()}=true
								c.load(0);
								c.push(Boolean.TRUE);
								c.putfield(mapperClazz, "_relationLoaded" + column.CamelBack(), Clazz.of(boolean.class));
							});
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
			throw  new RuntimeException(e);
		}

	}

}
