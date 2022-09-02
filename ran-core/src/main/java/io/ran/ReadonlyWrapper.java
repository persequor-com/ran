package io.ran;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadonlyWrapper {
	private static Map<Class, Class> wrapped = new HashMap<>();
	private static AutoMapperClassLoader classLoader = new AutoMapperClassLoader(AutoMapper.class.getClassLoader());


	public static <T, W extends T> W readonlyWrap(Class<W> wc, T t) {
		try {
			Class<W> tw = internalWrap(wc);
			W instance = tw.newInstance();
			Wrappee<W, T> wrappee = (Wrappee<W, T>) instance;
			wrappee.wrappee(t);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static  <T, W extends T> Class<W> internalWrap(Class<W> wc) {
		Class t = (List.class.isAssignableFrom(wc)) ? ArrayList.class : wc;

		return wrapped.computeIfAbsent(wc, c -> {
			try {
				Path path = Paths.get("/tmp/" + wc.getSimpleName() + "Readonly.class");

				ReadonlyWrapperWriter visitor = new ReadonlyWrapperWriter<W, T>(t);
				byte[] bytes = visitor.toByteArray();
				CheckClassAdapter.verify(new ClassReader(bytes), false, new PrintWriter(System.out));
				Files.write(path, bytes);
				return classLoader.define(visitor.getName(), bytes);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
