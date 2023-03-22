package io.ran;

import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ReadonlyWrapperWriter<WRAPPER extends WRAPPEE, WRAPPEE> extends AutoMapperClassWriter {
	private String signature;
	private String internalName;
	Clazz wrapperGenerated;
	Clazz wrappeeClass;

	public ReadonlyWrapperWriter(Class<WRAPPEE> wrappee) {
		super(wrappee);
		postFix = "Readonly";

		this.wrappeeClass = Clazz.of(wrappee);
		internalName = this.wrapperClazz.getInternalName();

		if (internalName.startsWith("java/")) {
			internalName = "io/ran/" + internalName;
		}
		String fullname = internalName + postFix;
		wrapperGenerated = Clazz.of(fullname);

		signature = this.wrapperClazz.getSignature();
		if (signature.startsWith("Ljava/")) {
			signature = "Lio/ran/" + signature.substring(1);
		}
		visit(Opcodes.V1_8, Access.Public.getOpCode(), fullname, this.wrapperClazz.generics.isEmpty() ? null : signature, wrapperClazz.getInternalName(), new String[]{Clazz.ofClazzes(Wrappee.class, wrapperClazz, wrappeeClass).getInternalName()});

		field(Access.Private, "_wrappee", wrappeeClass, false);

		Arrays.asList(wrapperClazz.clazz.getConstructors()).forEach(c -> {
			MethodWriter mw = method(Access.of(c.getModifiers()), new MethodSignature(c));
			mw.load(0);
			int i = 0;
			for (Parameter p : c.getParameters()) {
				mw.load(++i, Clazz.of(p.getType()));
			}

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
		Set<String> defined = new HashSet<>();

		wrappeeClass.methods().stream().sorted(Comparator.comparing((ClazzMethod cm) -> cm.getDeclaringClazz().equals(wrappeeClass))).forEach(cm -> {
			if (defined.contains(cm.getSignature().getMethodSignature())) {
				return;
			}
			buildMethod(cm);
			defined.add(cm.getSignature().getMethodSignature());
		});

	}

	private void buildMethod(ClazzMethod cm) {
		if (cm.isPublic() && !cm.isStatic()) {

			try {

				if (cm.parameters().size() > 0 && (cm.getName().startsWith("set") || cm.getName().startsWith("add"))) {
					MethodWriter mw = method(cm.getAccess(), cm.getSignature());
					mw.throwException(Clazz.of(RuntimeException.class), (imw) -> {
						imw.newInstance(Clazz.of(StringBuilder.class));
						imw.dup();
						imw.invoke(new MethodSignature(StringBuilder.class.getConstructor()));
						imw.push("Setter called on readonly object: " + cm.getDeclaringClazz().getSimpleName() + "." + cm.getSignature().getName());
						imw.invoke(StringBuilder.class.getMethod("append", String.class));
						imw.invoke(StringBuilder.class.getMethod("toString"));
					});
					mw.end();
				} else {
					MethodWriter mw = method(cm.getAccess(), cm.getSignature());
					if (!cm.getReturnType().clazz.isPrimitive() && !cm.getReturnType().isBoxedPrimitive()) {
						mw.push(Clazz.of(ReadonlyWrapper.class));
						mw.push(cm.getReturnType());
					}
					mw.load(0);
					mw.invoke(Wrappee.class.getMethod("wrappee"));
					mw.cast(wrappeeClass);
					int i = 0;
					for (ClazzMethodParameter p : cm.parameters()) {
						mw.load(++i, p.getClazz());
					}
					mw.invoke(cm.getSignature());

					if (!cm.getReturnType().clazz.isPrimitive() && !cm.getReturnType().isBoxedPrimitive()) {

						mw.invoke(ReadonlyWrapper.class.getMethod("readonlyWrap", Class.class, Object.class));
						mw.cast(cm.getReturnType());
					}
					mw.returnOf(cm.getReturnType());
					mw.end();
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
