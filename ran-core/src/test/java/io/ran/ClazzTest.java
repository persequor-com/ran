/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.testclasses.GraphNode;
import io.ran.testclasses.Regular;
import io.ran.token.Token;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ClazzTest {
	@Test
	public void relations_via() {
		List<RelationDescriber> relations = TypeDescriberImpl.getTypeDescriber(RelationFrom.class).relations();

		assertEquals(1, relations.size());
		assertEquals(2, relations.get(0).getVia().size());
		assertEquals(RelationFrom.class, relations.get(0).getVia().get(0).getFromClass().clazz);
		assertEquals(Token.of("id"), relations.get(0).getVia().get(0).getFromKeys().get(0).getToken());
		assertEquals(Token.of("relation", "from", "id"), relations.get(0).getVia().get(0).getToKeys().get(0).getToken());
		assertEquals(RelationVia.class, relations.get(0).getVia().get(0).getToClass().clazz);
		assertEquals(RelationVia.class, relations.get(0).getVia().get(1).getFromClass().clazz);
		assertEquals(RelationTo.class, relations.get(0).getVia().get(1).getToClass().clazz);
		assertEquals(Token.of("relation", "to", "id"), relations.get(0).getVia().get(1).getFromKeys().get(0).getToken());
		assertEquals(Token.of("id"), relations.get(0).getVia().get(1).getToKeys().get(0).getToken());
	}

	@Test
	public void relations_via_graph() {
		List<RelationDescriber> relations = TypeDescriberImpl.getTypeDescriber(GraphNode.class).relations();

		assertEquals(2, relations.size());
		assertEquals(2, relations.get(0).getVia().size());
		assertEquals(2, relations.get(1).getVia().size());

		assertEquals("previous_nodes", relations.get(0).getField().snake_case());
		assertEquals("id", relations.get(0).getFromKeys().get(0).getToken().snake_case());
		assertEquals("to_id", relations.get(0).getToKeys().get(0).getToken().snake_case());
		assertEquals("id", relations.get(0).getVia().get(0).getFromKeys().get(0).getToken().snake_case());
		assertEquals("to_id", relations.get(0).getVia().get(0).getToKeys().get(0).getToken().snake_case());
		assertEquals("from_id", relations.get(0).getVia().get(1).getFromKeys().get(0).getToken().snake_case());
		assertEquals("id", relations.get(0).getVia().get(1).getToKeys().get(0).getToken().snake_case());

		assertEquals("next_nodes", relations.get(1).getField().snake_case());
		assertEquals("id", relations.get(1).getFromKeys().get(0).getToken().snake_case());
		assertEquals("from_id", relations.get(1).getToKeys().get(0).getToken().snake_case());
		assertEquals("id", relations.get(1).getVia().get(0).getFromKeys().get(0).getToken().snake_case());
		System.out.println(relations.get(1).getVia().get(0).getFromKeys().get(0).getToken().snake_case());
		System.out.println(relations.get(1).getVia().get(0).getToKeys().get(0).getToken().snake_case());
		System.out.println(relations.get(1).getVia().get(1).getFromKeys().get(0).getToken().snake_case());
		System.out.println(relations.get(1).getVia().get(1).getToKeys().get(0).getToken().snake_case());
		assertEquals("from_id", relations.get(1).getVia().get(0).getToKeys().get(0).getToken().snake_case());
		assertEquals("to_id", relations.get(1).getVia().get(1).getFromKeys().get(0).getToken().snake_case());
		assertEquals("id", relations.get(1).getVia().get(1).getToKeys().get(0).getToken().snake_case());
	}

	@Test
	public void relations_viaDescribed() {
		List<RelationDescriber> relations = TypeDescriberImpl.getTypeDescriber(DescribedRelationFrom.class).relations();

		assertEquals(1, relations.size());
		assertEquals(2, relations.get(0).getVia().size());
		assertEquals(DescribedRelationFrom.class, relations.get(0).getVia().get(0).getFromClass().clazz);
		assertEquals(DescribedRelationVia.class, relations.get(0).getVia().get(0).getToClass().clazz);
		assertEquals(Token.of("muh"), relations.get(0).getVia().get(0).getFromKeys().get(0).getToken());
		assertEquals(Token.of("cat"), relations.get(0).getVia().get(0).getToKeys().get(0).getToken());
		assertEquals(DescribedRelationVia.class, relations.get(0).getVia().get(1).getFromClass().clazz);
		assertEquals(RelationTo.class, relations.get(0).getVia().get(1).getToClass().clazz);
		assertEquals(Token.of("horse"), relations.get(0).getVia().get(1).getFromKeys().get(0).getToken());
		assertEquals(Token.of("id"), relations.get(0).getVia().get(1).getToKeys().get(0).getToken());
	}

	@Test
	public void relations_inverse() {
		List<RelationDescriber> relations = TypeDescriberImpl.getTypeDescriber(DescribedRelationVia.class).relations();

		assertEquals(Token.of("muh"), relations.get(0).getToKeys().get(0).getToken());
		assertEquals(Token.of("cat"), relations.get(0).getFromKeys().get(0).getToken());

		assertEquals(Token.of("cat"), relations.get(0).inverse().getToKeys().get(0).getToken());
		assertEquals(Token.of("muh"), relations.get(0).inverse().getFromKeys().get(0).getToken());

	}

	@Test
	public void keys_multi() {
		KeySet keys = Clazz.of(RelationVia.class).getKeys().getPrimary();

		assertEquals(2, keys.size());
		assertEquals(Token.of("relation", "from", "id"), keys.get(0).getToken());
		assertEquals(Token.of("relation", "to", "id"), keys.get(1).getToken());
	}

	@Test
	public void fieldsOnSuperClass() {
		Property.PropertyList properties = Clazz.of(Regular.class).getProperties();
		assertEquals(2, properties.size());
		assertTrue(properties.stream().anyMatch(p -> p.getToken().equals(Token.of("reg"))));
		assertTrue(properties.stream().anyMatch(p -> p.getToken().equals(Token.of("sup"))));
	}

	@Test
	public void classWithRelationAndOtherKey() {
		List<RelationDescriber> relations = TypeDescriberImpl.getTypeDescriber(ClassWithRelationAndOtherKey.class).relations();

		assertEquals(relations.get(0).getFromKeys().size(), relations.get(0).getToKeys().size());
		assertEquals(1, relations.get(0).getToKeys().size());
		assertEquals("class_with_relation_and_other_key_id", relations.get(0).getToKeys().get(0).getToken().snake_case());
		assertEquals("id", relations.get(0).getFromKeys().get(0).getToken().snake_case());
	}


	@Test
	public void clazzWithGenericArgumentInParentClass() {
		Clazz<?> clazz = Clazz.of(GenericImpl.class);
		assertEquals(Clazz.of(String.class), clazz.getSuper().generics.get(0));
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getGenericClazz());
	}

	@Test
	public void clazzWithGenericArgumentInInterface() {
		Clazz<?> clazz = Clazz.of(GenericImpl.class);
		assertEquals(Clazz.of(String.class), clazz.getSuper().generics.get(0));
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getGenericClazz());
	}

	@Test
	public void classWithGenericArgument() {
		Clazz<?> clazz = Clazz.of(GenericClass.class);
		assertEquals(0, clazz.generics.size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
		assertNull(method.parameters().get(0).getGenericClazz());
	}

	@Test
	public void interfaceWithGenericArgument() {
		Clazz<?> clazz = Clazz.of(GenericInterface.class);
		assertEquals(0, clazz.generics.size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
		assertNull(method.parameters().get(0).getGenericClazz());
	}

	@Test
	public void interfaceWithGenericArgumentOnParentInterface() {
		Clazz<?> clazz = Clazz.of(NonGenericInterface.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class).clazz, method.parameters().get(0).getGenericClazz().clazz);
		ClazzMethod method2 = clazz.methods().find("method2", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);	}

	@Test
	public void interfaceWithGenericArgumentOnParentParentInterface() {
		Clazz<?> clazz = Clazz.of(NonGenericInterface2.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class).clazz, method.parameters().get(0).getGenericClazz().clazz);
		ClazzMethod method2 = clazz.methods().find("method2", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);
	}

	@Test
	public void interfaceWithGenericArgumentAndExplicitImplementation() {
		Clazz<?> clazz = Clazz.of(NonGenericInterfaceExplicit.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().find("method", void.class, String.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method.parameters().get(0).getBestEffortClazz().clazz);
		ClazzMethod method2 = clazz.methods().find("method2", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);
	}

	@Test
	public void forClassWithMethodsOfDifferentVisiblity() {
		Clazz<?> clazz = Clazz.of(ClassWithMethodsOfDifferentVisiblity.class);
		assertEquals(4, clazz.methods().size());
	}

	@Test
	public void testGenericMethodOfReturnType() {
		Clazz<?> clazz = Clazz.of(MyArrayParent.class);
		assertEquals(1, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().findFirst().orElseThrow(RuntimeException::new);
		Clazz<?> retType = method.getReturnType();
		ClazzMethod addMethod = retType.methods().find("add", boolean.class, String.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, addMethod.parameters().get(0).getBestEffortClazz().clazz);
	}

	@Test
	public void testManyLayersOfGenerics() {
		Clazz<?> clazz = Clazz.of(MyArray.class);
		ClazzMethod addMethod = clazz.methods().find("add", boolean.class, String.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, addMethod.parameters().get(0).getBestEffortClazz().clazz);

		ClazzMethod getMethod = clazz.methods().find("get", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, getMethod.getReturnType().clazz);
	}



	public static class RelationFrom {
		@PrimaryKey
		private int id;
		@Relation(collectionElementType = RelationTo.class, via = RelationVia.class)
		private transient List<RelationTo> to;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<RelationTo> getTo() {
			return to;
		}

		public void setTo(List<RelationTo> to) {
			this.to = to;
		}
	}

	public static class RelationVia {
		@PrimaryKey
		private int relationFromId;
		@PrimaryKey
		private int relationToId;

		public int getRelationFromId() {
			return relationFromId;
		}

		public void setRelationFromId(int relationFromId) {
			this.relationFromId = relationFromId;
		}

		public int getRelationToId() {
			return relationToId;
		}

		public void setRelationToId(int relationToId) {
			this.relationToId = relationToId;
		}
	}


	public static class DescribedRelationFrom {
		@PrimaryKey
		private int muh;
		@Relation(collectionElementType = RelationTo.class, via = DescribedRelationVia.class)
		private transient List<RelationTo> to;

		public int getMuh() {
			return muh;
		}

		public void setMuh(int muh) {
			this.muh = muh;
		}

		public List<RelationTo> getTo() {
			return to;
		}

		public void setTo(List<RelationTo> to) {
			this.to = to;
		}
	}

	public static class ClassWithRelationAndOtherKey {
		@PrimaryKey
		private int id;
		@Key(name = "otherKey")
		private String otherKey;
		@Relation(collectionElementType = ClassWithRelationAndOtherKeyRelationTo.class)
		private transient List<ClassWithRelationAndOtherKeyRelationTo> to;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<ClassWithRelationAndOtherKeyRelationTo> getTo() {
			return to;
		}

		public void setTo(List<ClassWithRelationAndOtherKeyRelationTo> to) {
			this.to = to;
		}

		public String getOtherKey() {
			return otherKey;
		}

		public void setOtherKey(String otherKey) {
			this.otherKey = otherKey;
		}
	}

	public static class ClassWithRelationAndOtherKeyRelationTo {
		@PrimaryKey
		private int id;
		private String otherKey;
		private int classWithRelationAndOtherKeyId;
		@Relation
		private ClassWithRelationAndOtherKey classWithRelationAndOtherKey;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getOtherKey() {
			return otherKey;
		}

		public void setOtherKey(String otherKey) {
			this.otherKey = otherKey;
		}

		public int getClassWithRelationAndOtherKeyId() {
			return classWithRelationAndOtherKeyId;
		}

		public void setClassWithRelationAndOtherKeyId(int classWithRelationAndOtherKeyId) {
			this.classWithRelationAndOtherKeyId = classWithRelationAndOtherKeyId;
		}

		public ClassWithRelationAndOtherKey getClassWithRelationAndOtherKey() {
			return classWithRelationAndOtherKey;
		}

		public void setClassWithRelationAndOtherKey(ClassWithRelationAndOtherKey classWithRelationAndOtherKey) {
			this.classWithRelationAndOtherKey = classWithRelationAndOtherKey;
		}
	}

	public static class DescribedRelationVia {
		@PrimaryKey
		private int cat;
		@PrimaryKey
		private int horse;

		@Relation(fields = "cat", relationFields = "muh")
		private transient DescribedRelationFrom samurai;

		@Relation(fields = "horse", relationFields = "id")
		private transient RelationTo ninja;

		public int getCat() {
			return cat;
		}

		public void setCat(int cat) {
			this.cat = cat;
		}

		public int getHorse() {
			return horse;
		}

		public void setHorse(int horse) {
			this.horse = horse;
		}

		public DescribedRelationFrom getSamurai() {
			return samurai;
		}

		public void setSamurai(DescribedRelationFrom samurai) {
			this.samurai = samurai;
		}

		public RelationTo getNinja() {
			return ninja;
		}

		public void setNinja(RelationTo ninja) {
			this.ninja = ninja;
		}
	}

	public static class RelationTo {
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	public static class GenericClass<T> {
		public void method(T t) {

		}
	}

	public static class GenericImpl extends GenericClass<String> {

	}

	public interface GenericInterface<T> {
		void method(T t);
		T method2(int i);
	}

	public static class GenericInterfaceImpl implements GenericInterface<String> {
		@Override
		public void method(String s) {

		}

		@Override
		public String method2(int i) {
			return null;
		}
	}

	public interface NonGenericInterface extends GenericInterface<String> {
		// This can be left empty, since method(T) is now method(String)
	}

	public interface NonGenericInterface2 extends NonGenericInterface {
		// This one is tricky because the method is declared deeper
	}

	public interface NonGenericInterfaceExplicit extends GenericInterface<String> {
		void method(String myString);
	}

	public class Muh implements NonGenericInterfaceExplicit {
		@Override
		public void method(String myString) {

		}

		@Override
		public String method2(int i) {
			return null;
		}
	}

	public class ClassWithMethodsOfDifferentVisiblity {
		private void privateMethod() {

		}

		protected void protectedMethod() {

		}

		void packagePrivateMethod() {

		}

		public void publicMethod() {

		}
	}

	public static class MyArray extends ArrayList<String> {

	}

	public static class MyArrayParent {
		public MyArray getArray() {
			return new MyArray();
		}
	}

	public static class Super0<T> {
		T method0(T input) {
			return null;
		}
	}
	public static class Super1<T> extends Super0<T> {
		T method1(T input) {
			return null;
		}
	}
	public static class Super2<T> extends Super1<T> {
		T method2(T input) {
			return null;
		}
	}
	public static class Super3 extends Super2<String> {

	}

}
