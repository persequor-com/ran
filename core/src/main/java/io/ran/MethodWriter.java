package io.ran;


import net.sf.cglib.core.Constants;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class MethodWriter {
	private static final String CONSTRUCTOR = "<init>";
	private Clazz owner;
	private Clazz parent;
	private MethodVisitor mv;
	private int locals = 1;
	private int curStack = 0;
	private int maxStack = 0;

	public MethodWriter(Clazz owner, Clazz parent, MethodVisitor mv, int parameterCount) {
		this.owner = owner;
		this.parent = parent;
		this.mv = mv;
		locals += parameterCount;
	}

	public void load(int arg) {
		mv.visitVarInsn(Opcodes.ALOAD, arg);
	}

	public void load(int arg, Clazz type) {
		if (type.isPrimitive()) {
			mv.visitVarInsn(Opcodes.ILOAD + type.getPrimitiveOffset(), arg);
		} else {
			load(arg);
		}
	}

	public void invoke(Method method) {
		invoke(new MethodSignature(method));
	}

	public void invokeSuper(MethodSignature methodSignature) {
		invoke(methodSignature, true);
	}

	public void invoke(MethodSignature methodSignature) {
		invoke(methodSignature, false);
	}

	private void invoke(MethodSignature methodSignature, boolean forceSpecial) {
		int opcode = Opcodes.INVOKEVIRTUAL;
		if (forceSpecial || methodSignature.isConstructor()) {
			opcode = Opcodes.INVOKESPECIAL;
		} else if (methodSignature.isStatic()) {
			opcode = Opcodes.INVOKESTATIC;
		} else if (methodSignature.getOwner().isInterface()) {
			opcode = Opcodes.INVOKEINTERFACE;
		}

		mv.visitMethodInsn(opcode,
				methodSignature.getOwner().getInternalName(),
				methodSignature.getName(),
				methodSignature.getMethodDescriptor(),
				methodSignature.getOwner().isInterface());
	}

	public void dup() {
		mv.visitInsn(Opcodes.DUP);
	}

	public void push(String s) {
		mv.visitLdcInsn(s);
	}

	public void push(boolean b) {
		mv.visitLdcInsn(b);
	}

	public void push(Clazz clazz) {
		mv.visitLdcInsn(Type.getType(clazz.clazz));
	}

	public void push(Object obj) {
		mv.visitLdcInsn(obj);
	}


	public <E extends Throwable> void ifNonNull(ThrowingConsumer<MethodWriter, E> o) throws E {
		Label endif = new Label();
		mv.visitJumpInsn(Opcodes.IFNULL,endif);
		o.accept(this);
		mv.visitLabel(endif);
	}

	public <E extends Throwable> void ifNull(ThrowingConsumer<MethodWriter, E> o) throws E {
		Label endif = new Label();
		mv.visitJumpInsn(Opcodes.IFNONNULL,endif);
		o.accept(this);
		mv.visitLabel(endif);
	}

	public <E extends Throwable> void ifThen(ThrowingConsumer<MethodWriter, E> o) throws E {
//		push(false);
		Label endif = new Label();
		mv.visitJumpInsn(Opcodes.IFEQ,endif);
		o.accept(this);
		mv.visitLabel(endif);
	}

	public <E extends Throwable> void ifElse(ThrowingConsumer<MethodWriter, E> t, ThrowingConsumer<MethodWriter, E> f) throws E {
//		push(false);
		Label endif = new Label();
		mv.visitJumpInsn(Opcodes.IFEQ,endif);
		t.accept(this);
		mv.visitLabel(endif);
	}

	public <E extends Throwable> void ifSame(ThrowingConsumer<MethodWriter, E> i, ThrowingConsumer<MethodWriter, E> e) throws E {
		Label endif = new Label();
		Label elseBlock = new Label();
		mv.visitJumpInsn(Opcodes.IF_ACMPEQ,elseBlock);
		i.accept(this);
		mv.visitJumpInsn(Opcodes.GOTO,endif);
		mv.visitLabel(elseBlock);
		e.accept(this);
		mv.visitLabel(endif);
	}

	public <E extends Throwable> void ifInstanceOf(Clazz clazz, ThrowingConsumer<MethodWriter, E> i, ThrowingConsumer<MethodWriter, E> e) throws E {
		Label endif = new Label();
		Label elseBlock = new Label();
		mv.visitTypeInsn(Opcodes.INSTANCEOF, clazz.getInternalName());
		mv.visitJumpInsn(Opcodes.IFEQ,elseBlock);
		i.accept(this);
		mv.visitJumpInsn(Opcodes.GOTO,endif);
		mv.visitLabel(elseBlock);
		e.accept(this);
		mv.visitLabel(endif);
	}

	public void cast(Clazz<?> of) {
		mv.visitTypeInsn(Opcodes.CHECKCAST, of.getInternalName());
	}

	public void returnNothing() {
		mv.visitInsn(Opcodes.RETURN);
	}

	public void defineVar(Clazz<CompoundKey> of) {

	}

	public void objectStore(int pos) {
		if (locals <= pos) {
			locals = pos+1;
		}
		mv.visitVarInsn(Opcodes.ASTORE, pos);
	}

	public void iStore(int pos) {
		if (locals <= pos) {
			locals = pos+1;
		}
		mv.visitVarInsn(Opcodes.ISTORE, pos);
	}


	public void objectLoad(int pos) {
		mv.visitVarInsn(Opcodes.ALOAD, pos);
	}

	public void nullConst() {
		mv.visitInsn(Constants.ACONST_NULL);
	}

	public void returnObject() {
		mv.visitInsn(Opcodes.ARETURN);
	}


	public void returnPrimitive(Clazz of) {
		mv.visitInsn(Opcodes.IRETURN+of.getPrimitiveOffset());
	}

	public void returnOf(Clazz<?> returnType) {
		if (returnType.isVoid()) {
			returnNothing();
		} else if (returnType.isPrimitive()) {
			returnPrimitive(returnType);
		} else {
			returnObject();
		}
	}

	public void end() {
		mv.visitMaxs(30,locals);
		mv.visitEnd();

	}

	public void box(Clazz clazz) {
		if (clazz.isBoxedPrimitive()) {
			clazz = clazz.getPrimitive();
		}
		if (clazz.isPrimitive()) {
			Clazz boxed = clazz.getBoxed();
			mv.visitTypeInsn(Opcodes.NEW, boxed.getInternalName());
			if (clazz.size() == 2) {
				// Pp -> Ppo -> oPpo -> ooPpo -> ooPp -> o
				mv.visitInsn(Opcodes.DUP_X2);
				mv.visitInsn(Opcodes.DUP_X2);
				mv.visitInsn(Opcodes.POP);
			} else {
				// p -> po -> opo -> oop -> o
				mv.visitInsn(Opcodes.DUP_X1);
				mv.visitInsn(Opcodes.SWAP);
			}
			invoke(new MethodSignature(boxed, CONSTRUCTOR, Clazz.getVoid(), clazz));
		}
	}

	public void unbox(Clazz boxed) {

		Clazz unBoxed;
		Method sig;
		if (boxed.isPrimitive()) {
			unBoxed = boxed;
			boxed = unBoxed.getBoxed();
			sig = boxed.getUnBoxSignature();
		} else {
			unBoxed = boxed.getUnBoxed();
			sig = boxed.getUnBoxSignature();
		}

		if (sig == null) {
			cast(unBoxed);
		} else {
			cast(boxed);
			invoke(sig);
		}
	}

	public void throwException(Clazz ex, ThrowingConsumer<MethodWriter,ReflectiveOperationException> messageGenerator) {
		try {
			mv.visitTypeInsn(Opcodes.NEW, ex.getInternalName());
			dup();
			messageGenerator.accept(this);
			invoke(new MethodSignature(ex, CONSTRUCTOR, Clazz.getVoid(), Clazz.of(String.class)));
			mv.visitInsn(Opcodes.ATHROW);
		} catch (ReflectiveOperationException exception) {

		}
	}

	public void addAnnotation(Clazz annotation, boolean b) {
		mv.visitAnnotation(annotation.getDescriptor(), b);
	}

	public void putfield(Clazz clazz, String name, Clazz of) {
		mv.visitFieldInsn(Opcodes.PUTFIELD, clazz.getInternalName(), name, of.getDescriptor());
	}

	public void getField(Clazz owner, String field, Clazz fieldType) {
		mv.visitFieldInsn(Opcodes.GETFIELD, owner.getInternalName(), field, fieldType.getDescriptor());
	}

	public void newInstance(Clazz of) {
		mv.visitTypeInsn(Opcodes.NEW, of.getInternalName());
	}
}
