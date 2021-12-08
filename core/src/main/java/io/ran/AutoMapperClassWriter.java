package io.ran;

import org.objectweb.asm.ClassWriter;

public class AutoMapperClassWriter extends ClassWriter {
	protected String name;
	protected String shortName;
	protected Class<?> aClass;
	protected Clazz<?> clazz;
	protected String postFix;

	public AutoMapperClassWriter(Class aClass) {
		super(COMPUTE_FRAMES);
		this.aClass = aClass;
		this.clazz = Clazz.of(aClass);
	}

	public AutoMapperClassWriter() {
		super(COMPUTE_FRAMES);
	}

	protected Clazz getSelf() {
		return Clazz.of(clazz.getInternalName()+postFix);
	}

	public MethodWriter method(Access access, MethodSignature signature) {
		return new MethodWriter(getSelf(), clazz, visitMethod(access.getOpCode(),
				signature.getName(),
				signature.getMethodDescriptor(),
				signature.getMethodSignature(),
				signature.getExceptions()
		), signature.getParameterCount());
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
