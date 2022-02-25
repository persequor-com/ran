package io.ran;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MapperGenerator {
	private final RanConfig config;
	public MapperGenerator(RanConfig config) {
		this.config = config;
	}
	public Wrapped generate(AutoMapperClassLoader classLoader, Clazz clazz) {
		try {

			MappingClassWriter visitor = new MappingClassWriter(clazz.clazz);
			byte[] bytes = visitor.toByteArray();
			CheckClassAdapter.verify(new ClassReader(bytes),false, new PrintWriter(System.out));

			QueryClassWriter visitor2 = new QueryClassWriter(clazz.clazz);
			byte[] bytes2 = visitor2.toByteArray();
			CheckClassAdapter.verify(new ClassReader(bytes2),false, new PrintWriter(System.out));

			writeClasses(clazz, bytes, bytes2);
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

	private void writeClasses(Clazz clazz, byte[] bytes, byte[] bytes2) throws IOException {
		if (config.enableRanClassesDebugging()) {
			Path path = buildPathForType(clazz, "Mapper");
			Path pathQuery = buildPathForType(clazz, "Query");
			Files.write(path, bytes);
			Files.write(pathQuery, bytes2);
		}
	}

	private Path buildPathForType(Clazz clazz, String type) {
		return Paths.get(config.projectBasePath() + "/tmp/" + clazz.getSimpleName()+ type + ".class");
	}

}
