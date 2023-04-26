/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AutoWrapper {
	private static Map<String, Class> wrapped = new HashMap<>();
	private static AutoMapperClassLoader classLoader = new AutoMapperClassLoader(AutoMapper.class.getClassLoader());

	private AutoWrapperGenericFactory factory;

	@Inject
	public AutoWrapper(AutoWrapperGenericFactory factory) {
		this.factory = factory;
	}

	public <T, W extends T> W wrap(Class<W> wc, T t) {
		W tw = factory.wrapped(wrapToClass(wc, (Class<T>) t.getClass()));
		Wrappee<W, T> wrappee = (Wrappee<W, T>) tw;
		wrappee.wrappee(t);
		return tw;
	}

	public <W> Class<W> wrapToClassWithFactoryInjector(String className, Class<W> interfaceClass, Class<? extends AutoWrappedFactory> factory, String identifier) {
		return wrapped.computeIfAbsent(className, c -> {
			try {
//				Path path = Paths.get("/tmp/" + className + "$Ran$Wrapper.class");

				AutoWrapperWriter<W, W> visitor = new AutoWrapperWriter<>(className, interfaceClass, interfaceClass, factory, identifier);
				byte[] bytes = visitor.toByteArray();
//				try(FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
//					outputStream.write(bytes);
//				}

				CheckClassAdapter.verify(new ClassReader(bytes), false, new PrintWriter(System.out));
				return classLoader.define(visitor.getName(), bytes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	public <T, W extends T> Class<W> wrapToClass(Class<W> wc, Class<T> tc) {
		return wrapped.computeIfAbsent(wc.getName(), c -> {
			try {
//				Path path = Paths.get("/tmp/" + wc.getSimpleName() + "Wrapper.class");

				AutoWrapperWriter visitor = new AutoWrapperWriter<W, T>(tc, wc);
				byte[] bytes = visitor.toByteArray();
				CheckClassAdapter.verify(new ClassReader(bytes), false, new PrintWriter(System.out));
				return classLoader.define(visitor.getName(), bytes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
