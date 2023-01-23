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

import java.io.PrintWriter;

public class MapperGenerator {
	public Wrapped generate(AutoMapperClassLoader classLoader, Clazz clazz) {
		try {
//			Path path = Paths.get("/tmp/ran/"+clazz.getSimpleName()+"$Ran$Mapper.class");
//			Path pathQuery = Paths.get("/tmp/ran/"+clazz.getSimpleName()+"$Ran$Query.class");

			MappingClassWriter visitor = new MappingClassWriter(clazz.clazz);
			byte[] bytes = visitor.toByteArray();
//			Files.write(path, bytes);
			CheckClassAdapter.verify(new ClassReader(bytes), false, new PrintWriter(System.out));


			QueryClassWriter visitor2 = new QueryClassWriter(clazz.clazz);
			byte[] bytes2 = visitor2.toByteArray();
			CheckClassAdapter.verify(new ClassReader(bytes2), false, new PrintWriter(System.out));
//			Files.write(pathQuery, bytes2);


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
