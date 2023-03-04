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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getClazz());
	}

	@Test
	public void clazzWithGenericArgumentInInterface() {
		Clazz<?> clazz = Clazz.of(GenericImpl.class);
		assertEquals(Clazz.of(String.class), clazz.getSuper().generics.get(0));
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getClazz());
	}

	@Test
	public void classWithGenericArgument() {
		Clazz<?> clazz = Clazz.of(GenericClass.class);
		assertEquals(1, clazz.generics.size());
		assertEquals(Object.class, clazz.generics.get(0).clazz);
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
	}

	@Test
	public void interfaceWithGenericArgument() {
		Clazz<?> clazz = Clazz.of(GenericInterface.class);
		assertEquals(1, clazz.generics.size());
		assertEquals(Object.class, clazz.generics.get(0).clazz);
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
	}

	@Test
	public void interfaceWithGenericArgumentOnParentInterface() {
		Clazz<?> clazz = Clazz.of(NonGenericInterface.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		ClazzMethod method2 = clazz.methods().find("method2", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);	}

	@Test
	public void interfaceWithGenericArgumentOnParentParentInterface() {
		Clazz<?> clazz = Clazz.of(NonGenericInterface2.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		ClazzMethod method2 = clazz.methods().find("method2", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);
	}

	@Test
	public void interfaceWithGenericArgumentAndExplicitImplementation() {
		Clazz<?> clazz = Clazz.of(NonGenericInterfaceExplicit.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().find("method", void.class, String.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, method.parameters().get(0).getClazz().clazz);
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
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m->m.getName().equals("getArray")).findFirst().orElseThrow(RuntimeException::new);
		Clazz<?> retType = method.getReturnType();
		ClazzMethod addMethod = retType.methods().find("add", boolean.class, String.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, addMethod.parameters().get(0).getClazz().clazz);
	}

	@Test
	public void testManyLayersOfGenerics() {
		Clazz<?> clazz = Clazz.of(MyArray.class);

		clazz.methods().forEach(method -> {
			// Check no exceptions are thrown
			assertNotNull(method.getReturnType());
		});

		ClazzMethod addMethod = clazz.methods().find("add", boolean.class, String.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, addMethod.parameters().get(0).getClazz().clazz);

		ClazzMethod getMethod = clazz.methods().find("get", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, getMethod.getReturnType().clazz);

		ClazzMethod sublistMethod = clazz.methods().find("subList", List.class, int.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(List.class).clazz, sublistMethod.getReturnType().clazz);
		assertEquals(Clazz.of(String.class).clazz, sublistMethod.getReturnType().generics.get(0).clazz);
	}

	@Test
	public void testNonGenericMethodReturnType() {
		Clazz<?> clazz = Clazz.of(MyArrayParent.class);
		assertEquals(2, clazz.methods().size());
		ClazzMethod addMethod = clazz.methods().find("nonGenericMethod", String.class, int.class).orElseThrow(RuntimeException::new);
		assertEquals(Clazz.of(String.class).clazz, addMethod.getReturnType().clazz);
		assertEquals(Clazz.of(int.class).clazz, addMethod.parameters().get(0).getClazz().clazz);
	}

	@Test
	public void testIsArray() {
		Clazz<?> arrClazz = Clazz.of((new String[0]).getClass());
		assertTrue(arrClazz.isArray());
		assertEquals(String.class, arrClazz.getComponentType().clazz);
	}

	@Test
	public void testNotArray() {
		Clazz<?> arrClazz = Clazz.of(String.class);
		assertFalse(arrClazz.isArray());
		assertEquals(String[].class, arrClazz.getArrayType().clazz);
	}

	@Test
	public void testIsArray_primitive() {
		Clazz<?> arrClazz = Clazz.of((new long[0]).getClass());
		assertTrue(arrClazz.isArray());
		assertEquals(long.class, arrClazz.getComponentType().clazz);
	}

	@Test
	public void testNotArray_primitive() {
		Clazz<?> arrClazz = Clazz.of(long.class);
		assertFalse(arrClazz.isArray());
		assertEquals(long[].class, arrClazz.getArrayType().clazz);
	}

	public static class Holder {
		private final Class<?> type;
		private final Object[] generics;
		public Holder(Class<?> type, Object... generics) {
			this.type = type;
			this.generics = generics;
		}
	}

	public static void assertClazz(Clazz<?> clazz, Class<?> type, Object... generics) {
		assertSame(type, clazz.clazz);
		assertEquals(generics.length, clazz.generics.size());
		int i = 0;
		try {
			for (; i < generics.length; i++) {
				if (generics[i] == self()) {
					assertSame(clazz, clazz.generics.get(i));
				} else if (generics[i] instanceof Clazz) {
					assertSame(generics[i], clazz.generics.get(i));
				} else if (generics[i] instanceof Class) {
					assertClazz(clazz.generics.get(i), (Class) generics[i]);
				} else if (generics[i] instanceof Holder) {
					Holder holder = (Holder) generics[i];
					assertClazz(clazz.generics.get(i), holder.type, holder.generics);
				}
			}
		} catch (AssertionError e) {
			throw new AssertionError("Type mismatch for type " + i + " of " + clazz, e);
		}
	}

	private static Class<?> self() {
		return null;
	}

	@Test
	public void testMike() {
		Clazz<?> gg = Clazz.of(GG.class);
		assertClazz(gg, GG.class, Object.class, gg);

		Clazz<?> gi = Clazz.of(GI.class);
		assertClazz(gi, GI.class, new Holder(GG.class, Integer.class, self()));
		Clazz<?> gi_gg = gi.findGenericSuper(GG.class);
		assertClazz(gi_gg, GG.class, Integer.class, gi_gg);

		Clazz<?> ii = Clazz.of(II.class);
		assertClazz(ii, II.class);
		Clazz<?> ii_gi = ii.findGenericSuper(GI.class);
		assertClazz(ii_gi, GI.class, II.class);
		Clazz<?> ii_gg = ii.findGenericSuper(GG.class);
		assertClazz(ii_gg, GG.class, Integer.class, II.class); // todo forward generic types when looking in superclasses, so the last arg could be ii instead of II.class?
		Clazz<?> ii_gi_gg = ii_gi.findGenericSuper(GG.class);
		assertClazz(ii_gi_gg, GG.class, Integer.class, ii_gi.generics.get(0));

		Clazz<?> ii2 = Clazz.of(II2.class);
		assertClazz(ii2, II2.class);
		Clazz<?> ii2_gi = ii2.findGenericSuper(GI.class);
		assertClazz(ii2_gi, GI.class, II.class);
		Clazz<?> ii2_gg = ii2.findGenericSuper(GG.class);
		assertClazz(ii2_gg, GG.class, Integer.class, II.class);
		Clazz<?> ii2_gi_gg = ii2_gi.findGenericSuper(GG.class);
		assertClazz(ii2_gi_gg, GG.class, Integer.class, ii2_gi.generics.get(0));

		Clazz<?> gi2 = Clazz.of(GI2.class);
		assertClazz(gi2, GI2.class, new Holder(GI.class, self()));
		Clazz<?> gi2_gg = gi2.findGenericSuper(GG.class);
		assertClazz(gi2_gg, GG.class, Integer.class, gi2.generics.get(0));

		Clazz<?> ii3 = Clazz.of(II3.class);
		assertClazz(ii3, II3.class);
		Clazz<?> ii3_gi2 = ii3.findGenericSuper(GI2.class);
		assertClazz(ii3_gi2, GI2.class, II.class);
		Clazz<?> ii3_gg = ii3.findGenericSuper(GG.class);
		assertClazz(ii3_gg, GG.class, Integer.class, II.class);
		Clazz<?> ii3_gi2_gg = ii3_gi2.findGenericSuper(GG.class);
		assertClazz(ii3_gi2_gg, GG.class, Integer.class, ii3_gi2.generics.get(0));

		Clazz<?> ii4 = Clazz.of(II4.class);
		assertClazz(ii4, II4.class);
		Clazz<?> ii4_gi = ii4.findGenericSuper(GI.class);
		assertClazz(ii4_gi, GI.class, II4.class); // todo could be ii4
		Clazz<?> ii4_gg = ii4.findGenericSuper(GG.class);
		assertClazz(ii4_gg, GG.class, Integer.class, II4.class);
		Clazz<?> ii4_gi_gg = ii4_gi.findGenericSuper(GG.class);
		assertClazz(ii4_gi_gg, GG.class, Integer.class, ii4_gi.generics.get(0));

		Clazz<?> gi3 = Clazz.of(GI3.class);
		assertClazz(gi3, GI3.class, Object.class);
		Clazz<?> gi3_gg = gi3.findGenericSuper(GG.class);
		assertClazz(gi3_gg, GG.class, Object.class, new Holder(GI3.class, Object.class));

		Clazz<?> gi4 = Clazz.of(GI4.class);
		assertClazz(gi4, GI4.class, gi4);
		Clazz<?> gi4_gi = gi4.findGenericSuper(GI.class);
		assertClazz(gi4_gi, GI.class, gi4);
		Clazz<?> gi4_gg = gi4.findGenericSuper(GG.class);
		assertClazz(gi4_gg, GG.class, Integer.class, gi4);
		Clazz<?> gi4_gi_gg = gi4_gi.findGenericSuper(GG.class);
		assertClazz(gi4_gi_gg, GG.class, Integer.class, gi4);

		Clazz<?> gi5 = Clazz.of(GI5.class);
		assertClazz(gi5, GI5.class, gi5);
		Clazz<?> gi5_gg = gi5.findGenericSuper(GG.class);
		assertClazz(gi5_gg, GG.class, new Holder(GI5.class, self()), gi5);
		// todo should we even try to make it fit assertClazz(gi5_gg, GG.class, gi5, gi5); ?

		Clazz<?> ii5 = Clazz.of(II5.class);
		assertClazz(ii5, II5.class);
		Clazz<?> ii5_gi3 = ii5.findGenericSuper(GI3.class);
		assertClazz(ii5_gi3, GI3.class, String.class);
		Clazz<?> ii5_gg = ii5.findGenericSuper(GG.class);
		assertClazz(ii5_gg, GG.class, String.class, new Holder(GI3.class, String.class));
		Clazz<?> ii5_gi3_gg = ii5_gi3.findGenericSuper(GG.class);
		// todo could be assertClazz(ii5_gi3_gg, GG.class, String.class, ii5_gi3);
		assertClazz(ii5_gi3_gg, GG.class, String.class, new Holder(GI3.class, String.class));

		Clazz<?> ii6 = Clazz.of(II6.class);
		assertClazz(ii6, II6.class);
		Clazz<?> ii6_gg = ii6.findGenericSuper(GG.class);
		assertClazz(ii6_gg, GG.class, String.class, II6.class); // todo could be ii6

		Clazz<?> gg2 = Clazz.of(GG2.class);
		assertClazz(gg2, GG2.class, new Holder(Comparable.class, self()), gg2);
		Clazz<?> gg2_gg = gg2.findGenericSuper(GG.class);
		assertClazz(gg2_gg, GG.class, new Holder(Comparable.class, self()), gg2);

		Clazz<?> gg3 = Clazz.of(GG3.class);
		assertClazz(gg3, GG3.class, gg3, new Holder(Comparable.class, self()));
		Clazz<?> gg3_gg = gg3.findGenericSuper(GG.class);
		assertClazz(gg3_gg, GG.class, new Holder(Comparable.class, self()), gg3);

		Clazz<?> ii7 = Clazz.of(II7.class);
		assertClazz(ii7, II7.class);
		Clazz<?> ii7_gg2 = ii7.findGenericSuper(GG2.class);
		assertClazz(ii7_gg2, GG2.class, String.class, II7.class); // todo could be ii7
		Clazz<?> ii7_gg = ii7.findGenericSuper(GG.class);
		assertClazz(ii7_gg, GG.class, String.class, II7.class);
		Clazz<?> ii7_gg2_gg = ii7_gg2.findGenericSuper(GG.class); // todo could be ii7
		assertClazz(ii7_gg2_gg, GG.class, String.class, ii7_gg2.generics.get(1));

	}

	@Test
	public void testM() {
		Clazz<?> g1 = Clazz.of(G1.class);

		Clazz<?> fe = Clazz.of(Fe.class);

		Clazz<?> he = Clazz.of(He.class);

		Clazz<?> ke = Clazz.of(Ke.class);

		Clazz<?> g2 = Clazz.of(G2.class);
		assertClazz(g2, G2.class, Object.class, g2);

		Clazz<?> g3 = Clazz.of(G3.class);
		assertClazz(g3, G3.class, g3);
		Clazz<?> g3_g2 = g3.findGenericSuper(G2.class);
		assertClazz(g3_g2, G2.class, g3, new Holder(G3.class, self()));
	}

	@Test
	public void testLoopDeLoop() {
		// todo fix
		// Clazz<?> loop = Clazz.of(Loop.class);
		// Clazz<?> deLoop = Clazz.of(DeLoop.class);
		// Clazz<?> loopyLoop = Clazz.of(LoopyLoop.class);
	}

	@Test
	public void testStuffThingsGroup() {
		Clazz<?> group = Clazz.ofClasses(Group.class, String.class, Number.class);
		Map<String, String> map = group.initialTypeMap();
		Clazz<?> stuff = group.findGenericSuper(Stuff.class, map);
	}

	public interface Stuff<S, X> {}

	public interface Things<T> extends Stuff<T, Long> {}

	public interface Group<G, B> extends Things<G> {}

	public interface StringGroup extends Group<Number, String> {}

	public interface Loop<X extends DeLoop> {}

	public interface DeLoop<X extends Loop> {}

	public static class LoopyLoop<F extends List<D>, D extends List<F>> {}

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

	public interface GG<A, B extends GG<A, B>> {}

	public interface GI<X extends GG<Integer, X>> extends GG<Integer, X> {}

	public interface II extends GI<II> {}

	public interface II2 extends GI<II> {}

	public interface GI2<X extends GI<X>> extends GG<Integer, X> {}

	public interface II3 extends GI2<II> {}

	public interface II4 extends GI<II4> {}

	public interface GI3<X> extends GG<X, GI3<X>> {}

	public interface GI4<X extends GI4<X>> extends GI<X> {}

	public interface GI5<X extends GI5<X>> extends GG<GI5<X>, X> {}

	public interface II5 extends GI3<String> {}

	public interface II6 extends GG<String, II6> {}

	public interface GG2<H extends Comparable<H>, J extends GG2<H, J>> extends GG<H, J> {}

	public interface GG3<J extends GG3<J, H>, H extends Comparable<H>> extends GG<H, J> {}

	public interface II7 extends GG2<String, II7> {}

	public interface G1<G extends G1<G, G>, H extends G1<H, H>> {}

	public interface Fe extends G1<Fe, Fe> {}

	public interface He extends G1<He, He> {}

	public interface Ke extends G1<Fe, He> {}

	public interface G2<X, SELF extends G2<X, SELF>> {}

	public interface G3<SELF extends G3<SELF>> extends G2<SELF, SELF> {}

	public interface G4<SELF extends G4<SELF>> extends G2<SELF, G4<SELF>> {}

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

		public String nonGenericMethod(int input) { return "Hello"; }
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
