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


		build();
	}

	protected void build() {
		try {
			MethodWriter c = method(Access.Public, new MethodSignature(getSelf(), "<init>", Clazz.getVoid()));
			c.load(0);
			c.invokeSuper(new MethodSignature(clazz.clazz.getConstructor()));
			c.load(0);
			c.invoke(Property.class.getMethod("get"));
			c.putfield(getSelf(), "currentProperty", Clazz.of(Property.class));
			c.returnNothing();
			c.end();

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



	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}
}
