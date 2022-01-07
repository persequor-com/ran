package io.ran;


import io.ran.token.Token;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Arrays;

public class QueryClassWriter extends AutoMapperClassWriter {
	public QueryClassWriter(Class clazz) {
		super(clazz);
		postFix = "Query";
		this.name = this.clazz.getInternalName().replace('/','.')+postFix;
		this.shortName = clazz.getSimpleName()+postFix;

		visit(Opcodes.V1_8, Access.Public.getOpCode(), this.clazz.getInternalName()+"Query", this.clazz.generics.isEmpty() ? null : this.clazz.getSignature(), this.clazz.getInternalName(), new String[]{Clazz.of(QueryWrapper.class).getInternalName()});


		buildConstructor();
		build();
		buildMethodRefs();
	}

	private void buildConstructor() {
		try {
			MethodWriter c = method(Access.Public, new MethodSignature(getSelf(), "<init>", Clazz.getVoid()));
			c.load(0);
			c.invokeSuper(new MethodSignature(clazz.clazz.getConstructor()));
			c.load(0);
			c.invoke(Property.class.getMethod("get"));
			c.putfield(getSelf(), "currentProperty", Clazz.of(Property.class));
			c.load(0);
			c.push(clazz);
			c.invoke(TypeDescriberImpl.class.getMethod("getTypeDescriber", Class.class));
			c.cast(Clazz.of(TypeDescriberImpl.class));
			c.putfield(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
			c.returnNothing();
			c.end();
		} catch (NoSuchMethodException exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void build() {
		try {

			MethodWriter g = method(Access.Public, new MethodSignature(QueryWrapper.class.getMethod("getCurrentProperty")));
			g.load(0);
			g.getField(getSelf(), "currentProperty", Clazz.of(Property.class));
			g.returnObject();
			g.end();

			field(Access.Private, "currentProperty", Clazz.of(Property.class), null);

			for (Method m : Arrays.asList(clazz.clazz.getMethods())) {
				if (!m.getName().matches("^(?:is|get|set).+") || m.getDeclaringClass() == Object.class) {
					continue;
				}
				String tokenSnake = Token.get(m.getName().replaceFirst("^(?:is|get|set)","")).snake_case();
				MethodWriter mw = method(Access.of(m.getModifiers()), new MethodSignature(m));
				mw.load(0);
				mw.getField(getSelf(), "currentProperty", Clazz.of(Property.class));
				mw.push(tokenSnake);
				mw.invoke(Token.class.getMethod("snake_case", String.class));
				mw.invoke(Property.class.getMethod("setToken", Token.class));
				if (m.getReturnType() == void.class) {
					mw.returnNothing();
				} else if (m.getReturnType().isPrimitive()) {
					mw.push(Clazz.of(m.getReturnType()).getDefaultValue());
					mw.returnPrimitive(Clazz.of(m.getReturnType()));
				} else {
					mw.nullConst();
					mw.returnObject();
				}
				mw.end();
			}
		} catch (NoSuchMethodException exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void buildMethodRefs() {
		try {
			MethodWriter g = method(Access.Public, new MethodSignature(QueryWrapper.class.getMethod("getCurrentMethod")));
			g.load(0);
			g.getField(getSelf(), "currentMethod", Clazz.of(ClazzMethod.class));
			g.returnObject();
			g.end();

			field(Access.Private, "currentMethod", Clazz.of(ClazzMethod.class), null);
			field(Access.Private, "typeDescriber", Clazz.of(TypeDescriberImpl.class), null);


			for (ClazzMethod m : clazz.methods()) {
				if (m.getMethod().getDeclaringClass() == Object.class || m.getName().matches("^(?:is|get|set).+")) {
					continue;
				}
				MethodWriter mw = method(Access.of(m.getModifiers()), new MethodSignature(m.getMethod()));
				mw.load(0);
				mw.load(0);
				mw.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
				mw.push(m.getMethod().toString());
				mw.invoke(TypeDescriberImpl.class.getMethod("method", String.class));
				mw.putfield(getSelf(), "currentMethod", Clazz.of(ClazzMethod.class));
				if (m.getMethod().getReturnType() == void.class) {
					mw.returnNothing();
				} else if (m.getMethod().getReturnType().isPrimitive()) {
					mw.push(Clazz.of(m.getMethod().getReturnType()).getDefaultValue());
					mw.returnPrimitive(Clazz.of(m.getMethod().getReturnType()));
				} else {
					mw.nullConst();
					mw.returnObject();
				}
				mw.end();
			}
		} catch (NoSuchMethodException exception) {
			throw new RuntimeException(exception);
		}
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}
}
