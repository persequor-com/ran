package io.ran;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MapperGenerator {
	public Wrapped generate(AutoMapperClassLoader classLoader, Clazz clazz) {
		try {

			Path path = Paths.get("/tmp/"+clazz.getSimpleName()+"Mapper.class");
			Path pathQuery = Paths.get("/tmp/"+clazz.getSimpleName()+"Query.class");

			MappingClassWriter visitor = new MappingClassWriter(clazz.clazz);
			byte[] bytes = visitor.toByteArray();
			CheckClassAdapter.verify(new ClassReader(bytes),false, new PrintWriter(System.out));
			Files.write(path, bytes);

			QueryClassWriter visitor2 = new QueryClassWriter(clazz.clazz);
			byte[] bytes2 = visitor2.toByteArray();
			CheckClassAdapter.verify(new ClassReader(bytes2),false, new PrintWriter(System.out));
			Files.write(pathQuery, bytes2);

			return new Wrapped(classLoader.define(visitor.getName(), bytes), classLoader.define(visitor2.getName(), bytes2));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class Wrapped {
		Class mapping;
		Class query;

		public Wrapped(Class mapping, Class query) {
			this.mapping = mapping;
			this.query = query;
		}
	}

}
