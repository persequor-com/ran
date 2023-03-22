/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import org.objectweb.asm.Opcodes;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Stream;

public class AutoWrapperWriter<WRAPPER extends WRAPPEE, WRAPPEE> extends AutoMapperClassWriter {
	private Clazz superClazz;
	Clazz wrapperGenerated;
	Clazz wrappeeClass;

	public AutoWrapperWriter(Class<WRAPPEE> wrappee, Class<WRAPPER> wrapper) {
		this(Clazz.of(wrappee).getInternalName() + "$Ran$Wrapper", wrappee, wrapper, null, null);
	}

	public AutoWrapperWriter(String className, Class<WRAPPEE> wrappee, Class<WRAPPER> wrapper, Class<? extends AutoWrappedFactory> factory, String identifier) {
		super(wrapper);
		postFix = "$Ran$Wrapper";
		this.wrappeeClass = Clazz.of(wrappee);
		wrapperGenerated = Clazz.of(className);
		this.superClazz = this.wrapperClazz.isInterface() ? Clazz.of(Object.class) : this.wrapperClazz;
		visit(Opcodes.V1_8
				, Access.Public.getOpCode()
				, className
				, this.wrapperClazz.generics.isEmpty() ? null : this.wrapperClazz.getSignature()
				, superClazz.getInternalName()
				, Stream.concat(
						Stream.of(Clazz.ofClazzes(Wrappee.class, wrapperClazz, wrappeeClass).getInternalName())
						, this.wrapperClazz.isInterface() ? Stream.of(this.wrapperClazz.getInternalName()) : Stream.empty()).toArray(String[]::new)
		);

		field(Access.Private, "_wrappee", wrappeeClass, null);


		Arrays.asList(wrapperClazz.clazz.getConstructors()).forEach(c -> {
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

		if (factory != null) {
			addFactoryMethodInjection(factory, identifier);
		}
		build();
	}

	public void addFactoryMethodInjection(Class<? extends AutoWrappedFactory> factory, String identifier) {
		try {
			MethodSignature ms = new MethodSignature(wrapperClazz, "<init>", Clazz.getVoid(), Clazz.of(factory));

			MethodWriter m = method(Access.Public, ms);
			m.load(0);
			m.invoke(new MethodSignature(superClazz, "<init>", Clazz.getVoid()));
			m.load(0);
			{
				m.load(1);
				m.push(identifier);
				m.invoke(new MethodSignature(DynamicClassIdentifier.class.getMethod("create", String.class)));
				m.addAnnotation(Clazz.of(Inject.class), true);
				m.invoke(new MethodSignature(AutoWrappedFactory.class.getMethod("get", DynamicClassIdentifier.class)));
				m.cast(wrappeeClass);
			}
			m.putfield(wrapperGenerated, "_wrappee", wrappeeClass);


			m.returnNothing();
			m.end();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void build() {
		buildWrappeeImplementations();
		buildMethods();
	}

	private void buildWrappeeImplementations() {
		try {
			ClazzMethod cm = new ClazzMethod(Clazz.of(Wrappee.class), Wrappee.class.getMethod("wrappee"));
			if (!wrapperClazz.declaresMethod(cm)) {
				MethodWriter mw = method(Access.Public, cm.getSignature());
				mw.load(0);
				mw.getField(wrapperGenerated, "_wrappee", wrappeeClass);
				mw.returnObject();
				mw.end();
			}

			cm = new ClazzMethod(Clazz.of(Wrappee.class), Wrappee.class.getMethod("wrappee", Object.class));
			if (!wrapperClazz.declaresMethod(cm)) {
				MethodWriter mw = method(Access.Public, cm.getSignature());
				mw.load(0);
				mw.load(1, Clazz.of(Object.class));
				mw.cast(wrappeeClass);
				mw.putfield(wrapperGenerated, "_wrappee", wrappeeClass);
				mw.returnNothing();
				mw.end();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void buildMethods() {
		wrappeeClass.methods().forEach(cm -> {
			try {
				if (!wrapperClazz.declaresMethod(cm) || wrappeeClass.equals(wrapperClazz)) {
					MethodWriter mw = method(cm.getAccess(), cm.getSignature());
					mw.load(0);
					mw.invoke(Wrappee.class.getMethod("wrappee"));
					mw.cast(wrappeeClass);
					int i = 0;
					for (ClazzMethodParameter p : cm.parameters()) {
						mw.load(++i, p.getClazz());
					}
					mw.invoke(cm.getSignature());
					mw.returnOf(cm.getReturnType());
					mw.end();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

	}

}
