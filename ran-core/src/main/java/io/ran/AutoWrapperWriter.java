package io.ran;

import org.objectweb.asm.Opcodes;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

public class AutoWrapperWriter<WRAPPER extends WRAPPEE, WRAPPEE> extends AutoMapperClassWriter {
	Clazz wrapperGenerated;
	Clazz wrappeeClass;
	public AutoWrapperWriter(Class<WRAPPEE> wrappee, Class<WRAPPER> wrapper) {
		super(wrapper);
		postFix = "Wrapper";
		this.wrappeeClass = Clazz.of(wrappee);
		wrapperGenerated = Clazz.of(this.clazz.getInternalName()+postFix);
		visit(Opcodes.V1_8, Access.Public.getOpCode(), this.clazz.getInternalName()+postFix, this.clazz.generics.isEmpty() ? null : this.clazz.getSignature(), this.clazz.getInternalName(), new String[]{Clazz.ofClazzes(Wrappee.class, clazz, wrappeeClass).getInternalName()});

		field(Access.Private, "_wrappee", wrappeeClass, false);

		Arrays.asList(clazz.clazz.getConstructors()).forEach(c -> {
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
		buildWrappeeImplementations();
		buildMethods();
	}

	private void buildWrappeeImplementations() {
		try {
			ClazzMethod cm = new ClazzMethod(Clazz.of(Wrappee.class), Wrappee.class.getMethod("wrappee"));
			if(!clazz.declaresMethod(cm)) {
				MethodWriter mw = method(Access.Public, cm.getSignature());
				mw.load(0);
				mw.getField(wrapperGenerated, "_wrappee", wrappeeClass);
				mw.returnObject();
				mw.end();
			}

			cm = new ClazzMethod(Clazz.of(Wrappee.class), Wrappee.class.getMethod("wrappee", Object.class));
			if(!clazz.declaresMethod(cm)) {
				MethodWriter mw = method(Access.Public, cm.getSignature());
				mw.load(0);
				mw.load(1, Clazz.of(Object.class));
				mw.cast(wrappeeClass);
				mw.putfield(wrapperGenerated, "_wrappee", wrappeeClass);
				mw.returnNothing();
				mw.end();
			}
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	private void buildMethods() {
		wrappeeClass.methods().forEach(cm -> {
			try {
				if (!clazz.declaresMethod(cm)) {
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
