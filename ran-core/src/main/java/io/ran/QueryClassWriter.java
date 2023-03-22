/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;


import io.ran.token.Token;
import org.objectweb.asm.Opcodes;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class QueryClassWriter extends AutoMapperClassWriter {
	public QueryClassWriter(Class clazz) {
		super(clazz);
		postFix = "$Ran$Query";
		this.name = this.wrapperClazz.getInternalName().replace('/', '.') + postFix;
		this.shortName = clazz.getSimpleName() + postFix;

		visit(Opcodes.V1_8, Access.Public.getOpCode(), this.wrapperClazz.getInternalName() + "$Ran$Query", this.wrapperClazz.generics.isEmpty() ? null : this.wrapperClazz.getSignature(), this.wrapperClazz.getInternalName(), new String[]{Clazz.of(QueryWrapper.class).getInternalName()});


		buildConstructor();
		build();
		buildMethodRefs();
	}

	private void buildConstructor() {
		try {
			for (Constructor<?> c : wrapperClazz.clazz.getConstructors()) {
				MethodWriter mw = method(Access.of(c.getModifiers()), new MethodSignature(c));

				if (c.getAnnotation(Inject.class) != null) {
					mw.addAnnotation(Clazz.of(Inject.class), true);
				}
				mw.load(0);
				int i = 0;
				for (Parameter p : Arrays.asList(c.getParameters())) {
					mw.load(++i);
				}
				mw.invoke(new MethodSignature(c));

				mw.load(0);
				mw.invoke(Property.class.getMethod("get"));
				mw.putfield(getSelf(), "currentProperty", Clazz.of(Property.class));
				mw.load(0);
				mw.push(wrapperClazz);
				mw.invoke(TypeDescriberImpl.class.getMethod("getTypeDescriber", Class.class));
				mw.cast(Clazz.of(TypeDescriberImpl.class));
				mw.putfield(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
				mw.returnNothing();
				mw.end();
			}


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

			for (Method m : Arrays.asList(wrapperClazz.clazz.getMethods())) {
				if (!m.getName().matches("^(?:is|get|set).+") || m.getDeclaringClass() == Object.class) {
					continue;
				}
				String tokenSnake = Token.get(m.getName().replaceFirst("^(?:is|get|set)", "")).snake_case();
				MethodWriter mw = method(Access.of(m.getModifiers()), new MethodSignature(m));
				mw.load(0);
				mw.load(0);
				mw.getField(getSelf(), "typeDescriber", Clazz.of(TypeDescriberImpl.class));
				mw.push(tokenSnake);
				mw.invoke(TypeDescriberImpl.class.getMethod("getPropertyFromSnakeCase", String.class));
				mw.putfield(getSelf(), "currentProperty", Clazz.of(Property.class));
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


			for (ClazzMethod m : wrapperClazz.methods()) {
				if (m.getMethod().getDeclaringClass() == Object.class || m.getName().matches("^(?:is|get|set).+")
						|| Access.isSyntheticMethod(m.getModifiers())) {
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
