package io.ran;

public class AutoMapperClassLoader extends ClassLoader {
	public AutoMapperClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Class define(String name, byte[] bytes) {
		return defineClass(name, bytes, 0, bytes.length);
	}
}
