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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Primitives {
	private static int DEFAULT_BOOLEAN;
	private static byte DEFAULT_BYTE;
	private static short DEFAULT_SHORT;
	private static int DEFAULT_INT;
	private static long DEFAULT_LONG;
	private static float DEFAULT_FLOAT;
	private static double DEFAULT_DOUBLE;
	private static char DEFAULT_CHAR = Character.MIN_VALUE;

	private static final Map<Class, Primitive> primitives = new HashMap<>();
	private static final Map<Class, Class> boxedToPrimitive = new HashMap<>();

	private static void add(Primitive primitive) {
		primitives.put(primitive.primitive, primitive);
		boxedToPrimitive.put(primitive.boxed, primitive.primitive);
	}

	private static Object getDefaultValue(Class clazz) {
		if (clazz.equals(boolean.class)) {
			return DEFAULT_BOOLEAN;
		} else if (clazz.equals(byte.class)) {
			return DEFAULT_BYTE;
		} else if (clazz.equals(short.class)) {
			return DEFAULT_SHORT;
		} else if (clazz.equals(int.class)) {
			return DEFAULT_INT;
		} else if (clazz.equals(long.class)) {
			return DEFAULT_LONG;
		} else if (clazz.equals(float.class)) {
			return DEFAULT_FLOAT;
		} else if (clazz.equals(double.class)) {
			return DEFAULT_DOUBLE;
		} else if (clazz.equals(char.class)) {
			return DEFAULT_CHAR;
		} else {
			throw new IllegalArgumentException(
					"Class type " + clazz + " not supported");
		}
	}

	static {
		try {
			add(new Primitive(boolean.class, Boolean.class, "Z", Boolean.class.getMethod("booleanValue"), 0));
			add(new Primitive(byte.class, Byte.class, "B", Byte.class.getMethod("byteValue"), 0));
			add(new Primitive(char.class, Character.class, "C", Character.class.getMethod("charValue"), 0));
			add(new Primitive(double.class, Double.class, "D", Double.class.getMethod("doubleValue"), (Opcodes.DALOAD - Opcodes.IALOAD)));
			add(new Primitive(float.class, Float.class, "F", Float.class.getMethod("floatValue"), (Opcodes.FALOAD - Opcodes.IALOAD)));
			add(new Primitive(int.class, Integer.class, "I", Integer.class.getMethod("intValue"), 0));
			add(new Primitive(long.class, Long.class, "J", Long.class.getMethod("longValue"), (Opcodes.LALOAD - Opcodes.IALOAD)));
			add(new Primitive(short.class, Short.class, "S", Short.class.getMethod("shortValue"), 0));
			add(new Primitive(void.class, Void.class, "V", null, 4));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Primitive get(Class clazz) {
		Class working = clazz;
		if (boxedToPrimitive.containsKey(clazz)) {
			working = boxedToPrimitive.get(clazz);
		}
		if (!primitives.containsKey(working)) {
			throw new NonExistingPrimitiveException(clazz);
		}
		return primitives.get(working);
	}

	public static boolean isBoxedPrimitive(Class clazz) {
		return boxedToPrimitive.containsKey(clazz);
	}

	public static class Primitive {
		private int primitiveOffset;
		private Class primitive;
		private Class boxed;
		private String descriptor;
		private Method constructorSignature;

		public Primitive(Class primitive, Class boxed, String descriptor, Method constructorSignature, int primitiveOffset) {
			this.primitive = primitive;
			this.boxed = boxed;
			this.descriptor = descriptor;
			this.constructorSignature = constructorSignature;
			this.primitiveOffset = primitiveOffset;
		}

		public int getPrimitiveOffset() {
			return primitiveOffset;
		}

		public Class getPrimitive() {
			return primitive;
		}

		public Class getBoxed() {
			return boxed;
		}

		public String getDescriptor() {
			return descriptor;
		}

		public Method getConstructorSignature() {
			return constructorSignature;
		}

		public Object getDefaultValue() {
			return Primitives.getDefaultValue(primitive);
		}
	}

	public static class NonExistingPrimitiveException extends RuntimeException {
		public NonExistingPrimitiveException(Class clazz) {
			super("Could not find primitive by: " + clazz.getName() + ". This is a coding error.");
		}
	}
}
