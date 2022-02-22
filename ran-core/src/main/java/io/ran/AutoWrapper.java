package io.ran;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AutoWrapper {
	private static Map<Class, Class> wrapped = new HashMap<>();
	private static AutoMapperClassLoader classLoader = new AutoMapperClassLoader(AutoMapper.class.getClassLoader());

	private GenericFactory factory;

	public AutoWrapper(GenericFactory factory) {
		this.factory = factory;
	}

	public <T, W extends T> W wrap(Class<W> wc, T t) {
		 W tw = factory.wrapped(internalWrap(wc, (Class<T>)t.getClass()));
		 Wrappee<W,T> wrappee = (Wrappee<W,T>)tw;
		 wrappee.wrappee(t);
		 return tw;
	}

	private <T, W extends T> Class<W> internalWrap(Class<W> wc, Class<T> tc) {
		return wrapped.computeIfAbsent(wc, c -> {
			try {
				Path path = Paths.get("/tmp/" + wc.getSimpleName() + "Wrapper.class");

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
