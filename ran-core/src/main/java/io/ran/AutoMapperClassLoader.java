/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

public class AutoMapperClassLoader extends ClassLoader {
	public AutoMapperClassLoader(ClassLoader parent) {
		super(parent);
	}

	public Class define(String name, byte[] bytes) {
		return defineClass(name, bytes, 0, bytes.length);
	}
}
