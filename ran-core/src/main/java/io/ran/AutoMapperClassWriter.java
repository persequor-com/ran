package io.ran;

import org.objectweb.asm.ClassWriter;

public class AutoMapperClassWriter extends ClassWriter {
	protected String name;
	protected String shortName;
	protected Class<?> wrapperClass;
	protected Clazz<?> wrapperClazz;
	protected String postFix;

	public AutoMapperClassWriter(Class wrapperClass) {
		super(COMPUTE_FRAMES);
		this.wrapperClass = wrapperClass;
		this.wrapperClazz = Clazz.of(wrapperClass);
	}

	public AutoMapperClassWriter() {
		super(COMPUTE_FRAMES);
	}

	protected Clazz getSelf() {
		return Clazz.of(wrapperClazz.getInternalName() + postFix);
	}

	public MethodWriter method(Access access, MethodSignature signature) {
		return new MethodWriter(getSelf(), wrapperClazz, visitMethod(access.getOpCode(),
				signature.getName(),
				signature.getMethodDescriptor(),
				signature.getMethodSignature(),
				signature.getExceptions()
		), signature.getParameterCount());
	}

	public void addAnnotation(Clazz annotation, boolean visibleAtRuntime) {
		visitAnnotation(annotation.getDescriptor(), visibleAtRuntime);
	}

	public void field(Access access, String name, Clazz type, Object value) {
		visitField(access.getOpCode(), name, type.getDescriptor(), type.generics.isEmpty() ? null : type.getSignature(), value);
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}
}
