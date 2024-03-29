/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.ran.testclasses.AssertHelpers.*;
import static org.junit.Assert.*;

@SuppressWarnings({"InnerClassMayBeStatic", "OptionalGetWithoutIsPresent"})
public class ClazzTest {

	@Test
	public void classWithGenericArgument() {
		Clazz<?> clazz = Clazz.of(GenericClass.class);
		assertEquals(1, clazz.generics.size());
		assertEquals(Object.class, clazz.generics.get(0).clazz);
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());

		assertClazz(clazz, GenericClass.class, Object.class);
		assertMethod(clazz, method, Void.TYPE, Object.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
	}

	@Test
	public void interfaceWithGenericArgument() {
		Clazz<?> clazz = Clazz.of(GenericInterface.class);
		assertEquals(1, clazz.generics.size());
		assertEquals(Object.class, clazz.generics.get(0).clazz);
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(Object.class), method.parameters().get(0).getClazz());
		ClazzMethod method2 = clazz.methods().stream().filter(m -> m.getName().equals("method2")).findFirst().get();

		assertClazz(clazz, GenericInterface.class, Object.class);
		assertMethod(clazz, method, Void.TYPE, Object.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(clazz, method2, Object.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());
	}

	@Test
	public void clazzWithGenericArgumentInParentClass() {
		Clazz<?> clazz = Clazz.of(GenericClassImpl.class);
		assertEquals(Clazz.of(String.class), clazz.getSuper().generics.get(0));
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getClazz());

		assertClazz(clazz, GenericClassImpl.class);
		assertMethod(g(GenericClass.class, String.class), method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());

		Clazz<?> parent = clazz.getSuper();
		method = parent.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();

		assertClazz(parent, GenericClass.class, String.class);
		assertMethod(parent, method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());

		parent = clazz.findGenericSuper(GenericClass.class);
		method = parent.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();

		assertClazz(parent, GenericClass.class, String.class);
		assertMethod(parent, method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
	}

	@Test
	public void clazzWithGenericArgumentInInterface() {
		Clazz<?> clazz = Clazz.of(GenericInterfaceImpl.class);
		Clazz<?> superI = clazz.findGenericSuper(GenericInterface.class);
		assertEquals(Clazz.of(String.class), superI.generics.get(0));
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getClazz());
		ClazzMethod method2 = clazz.methods().stream().filter(m -> m.getName().equals("method2")).findFirst().get();
		assertEquals(Clazz.of(String.class), method2.getReturnType());

		assertClazz(clazz, GenericInterfaceImpl.class);
		assertMethod(clazz, method, Void.TYPE, String.class);
		assertFalse(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(clazz, method2, String.class, int.class);
		assertFalse(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());

		method = superI.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getClazz());
		method2 = superI.methods().stream().filter(m -> m.getName().equals("method2")).findFirst().get();

		assertClazz(superI, GenericInterface.class, String.class);
		assertMethod(superI, method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(superI, method2, String.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());
	}

	@Test
	public void interfaceWithGenericArgumentOnParentInterface() {
		Clazz<?> clazz = Clazz.of(NonGenericInterface.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		ClazzMethod method2 = clazz.methods().stream().filter(m -> m.getName().equals("method2")).findFirst().get();
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);

		assertMethod(g(GenericInterface.class, String.class), method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(g(GenericInterface.class, String.class), method2, String.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());

		Clazz<?> superI = clazz.findGenericSuper(GenericInterface.class);
		method = superI.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		assertEquals(Clazz.of(String.class), method.parameters().get(0).getClazz());
		method2 = superI.methods().stream().filter(m -> m.getName().equals("method2")).findFirst().get();

		assertClazz(superI, GenericInterface.class, String.class);
		assertMethod(superI, method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(superI, method2, String.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());
	}

	@Test
	public void interfaceWithGenericArgumentOnParentParentInterface() {
		Clazz<?> clazz = Clazz.of(NonGenericInterface2.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("method")).findFirst().get();
		ClazzMethod method2 = clazz.methods().stream().filter(m -> m.getName().equals("method2")).findFirst().get();
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);

		assertClazz(clazz, NonGenericInterface2.class);
		assertMethod(g(GenericInterface.class, String.class), method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(g(GenericInterface.class, String.class), method2, String.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());
	}

	@Test
	public void interfaceWithGenericArgumentAndExplicitImplementation() {
		Clazz<?> clazz = Clazz.of(NonGenericInterfaceExplicit.class);
		assertEquals(0, clazz.generics.size());
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().find("method", void.class, String.class).get();
		assertEquals(Clazz.of(String.class).clazz, method.parameters().get(0).getClazz().clazz);
		ClazzMethod method2 = clazz.methods().find("method2", String.class, int.class).get();
		assertEquals(Clazz.of(String.class).clazz, method2.getReturnType().clazz);

		assertClazz(clazz, NonGenericInterfaceExplicit.class);
		assertMethod(clazz, method, Void.TYPE, String.class);
		assertFalse(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(g(GenericInterface.class, String.class), method2, String.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());

		Clazz<?> parent = clazz.findGenericSuper(GenericInterface.class);
		method = parent.methods().find("method", void.class, String.class).get();
		assertEquals(Clazz.of(String.class).clazz, method.parameters().get(0).getClazz().clazz);
		method2 = parent.methods().find("method2", String.class, int.class).get();

		assertClazz(parent, GenericInterface.class, String.class);
		assertMethod(parent, method, Void.TYPE, String.class);
		assertTrue(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(parent, method2, String.class, int.class);
		assertTrue(method2.hasGenericFromClass());
		assertFalse(method2.hasGenericFromMethod());
	}

	@Test
	public void forClassWithMethodsOfDifferentVisibility() {
		Clazz<?> clazz = Clazz.of(ClassWithMethodsOfDifferentVisibility.class);
		Set<String> expected = new HashSet<>(Arrays.asList(
				"privateMethod", "protectedMethod", "packagePrivateMethod", "publicMethod"));
		assertEquals(expected, clazz.methods().stream().map(ClazzMethod::getName).collect(Collectors.toSet()));
	}

	@Test
	public void testGenericMethodOfReturnType() {
		Clazz<?> clazz = Clazz.of(MyArrayParent.class);
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().stream().filter(m -> m.getName().equals("getArray")).findFirst().get();
		Clazz<?> retType = method.getReturnType();
		ClazzMethod addMethod = retType.methods().find("add", boolean.class, String.class).get();
		assertEquals(Clazz.of(String.class).clazz, addMethod.parameters().get(0).getClazz().clazz);

		assertClazz(clazz, MyArrayParent.class);
		assertMethod(clazz, method, MyArray.class);
		assertFalse(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
		assertMethod(g(ArrayList.class, String.class), addMethod, boolean.class, String.class);
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
	}

	@Test
	public void testManyLayersOfGenerics() {
		Clazz<?> clazz = Clazz.of(MyArray.class);

		clazz.methods().forEach(method -> {
			// Check no exceptions are thrown
			assertNotNull(method.getReturnType());
		});

		ClazzMethod addMethod = clazz.methods().find("add", boolean.class, String.class).get();
		assertEquals(Clazz.of(String.class).clazz, addMethod.parameters().get(0).getClazz().clazz);

		ClazzMethod getMethod = clazz.methods().find("get", String.class, int.class).get();
		assertEquals(Clazz.of(String.class).clazz, getMethod.getReturnType().clazz);

		ClazzMethod sublistMethod = clazz.methods().find("subList", List.class, int.class, int.class).get();
		assertEquals(Clazz.of(List.class).clazz, sublistMethod.getReturnType().clazz);
		assertEquals(Clazz.of(String.class).clazz, sublistMethod.getReturnType().generics.get(0).clazz);

		assertClazz(clazz, MyArray.class);
		assertMethod(g(ArrayList.class, String.class), addMethod, boolean.class, String.class);
		assertTrue(addMethod.hasGenericFromClass());
		assertFalse(addMethod.hasGenericFromMethod());
		assertMethod(g(ArrayList.class, String.class), getMethod, String.class, int.class);
		assertTrue(getMethod.hasGenericFromClass());
		assertFalse(getMethod.hasGenericFromMethod());
		assertMethod(g(ArrayList.class, String.class), sublistMethod, g(List.class, String.class), int.class, int.class);
		assertTrue(sublistMethod.hasGenericFromClass());
		assertFalse(sublistMethod.hasGenericFromMethod());
	}

	@Test
	public void testNonGenericMethodReturnType() {
		Clazz<?> clazz = Clazz.of(MyArrayParent.class);
		assertEquals(2, clazz.methods().size());
		ClazzMethod method = clazz.methods().find("nonGenericMethod", String.class, int.class).get();
		assertEquals(Clazz.of(String.class).clazz, method.getReturnType().clazz);
		assertEquals(Clazz.of(int.class).clazz, method.parameters().get(0).getClazz().clazz);

		assertClazz(clazz, MyArrayParent.class);
		assertMethod(clazz, method, String.class, int.class);
		assertFalse(method.hasGenericFromClass());
		assertFalse(method.hasGenericFromMethod());
	}

	@Test
	public void testIsArray() {
		Clazz<?> arrClazz = Clazz.of(String[].class);
		assertEquals(0, arrClazz.generics.size());
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
		Clazz<?> arrClazz = Clazz.of(long[].class);
		assertTrue(arrClazz.isArray());
		assertEquals(long.class, arrClazz.getComponentType().clazz);
	}

	@Test
	public void testNotArray_primitive() {
		Clazz<?> arrClazz = Clazz.of(long.class);
		assertFalse(arrClazz.isArray());
		assertEquals(long[].class, arrClazz.getArrayType().clazz);
	}

	@Test
	public void testSelfReferencing1() {
		{
			Clazz<?> gg = Clazz.of(GG.class);
			assertClazz(gg, GG.class, Object.class, self());
		}
		{
			Clazz<?> gi = Clazz.of(GI.class);
			assertClazz(gi, GI.class, g(GG.class, Integer.class, self()));
			Clazz<?> gi_gg = gi.findGenericSuper(GG.class);
			assertClazz(gi_gg, GG.class, Integer.class, self());
		}
		{
			Clazz<?> ii = Clazz.of(II.class);
			assertClazz(ii, II.class);
			Clazz<?> ii_gi = ii.findGenericSuper(GI.class);
			assertClazz(ii_gi, GI.class, II.class);
			Clazz<?> ii_gg = ii.findGenericSuper(GG.class);
			assertClazz(ii_gg, GG.class, Integer.class, II.class);
			Clazz<?> ii_gi_gg = ii_gi.findGenericSuper(GG.class);
			assertClazz(ii_gi_gg, GG.class, Integer.class, II.class);
		}
		{
			Clazz<?> ii2 = Clazz.of(II2.class);
			assertClazz(ii2, II2.class);
			Clazz<?> ii2_gi = ii2.findGenericSuper(GI.class);
			assertClazz(ii2_gi, GI.class, II.class);
			Clazz<?> ii2_gg = ii2.findGenericSuper(GG.class);
			assertClazz(ii2_gg, GG.class, Integer.class, II.class);
			Clazz<?> ii2_gi_gg = ii2_gi.findGenericSuper(GG.class);
			assertClazz(ii2_gi_gg, GG.class, Integer.class, II.class);
		}
		{
			Clazz<?> gi2 = Clazz.of(GI2.class);
			assertClazz(gi2, GI2.class, g(GI.class, self()));
			Clazz<?> gi2_gg = gi2.findGenericSuper(GG.class);
			assertClazz(gi2_gg, GG.class, Integer.class, g(GI.class, self()));
		}
		{
			Clazz<?> ii3 = Clazz.of(II3.class);
			assertClazz(ii3, II3.class);
			Clazz<?> ii3_gi2 = ii3.findGenericSuper(GI2.class);
			assertClazz(ii3_gi2, GI2.class, II.class);
			Clazz<?> ii3_gg = ii3.findGenericSuper(GG.class);
			assertClazz(ii3_gg, GG.class, Integer.class, II.class);
			Clazz<?> ii3_gi2_gg = ii3_gi2.findGenericSuper(GG.class);
			assertClazz(ii3_gi2_gg, GG.class, Integer.class, II.class);
		}
		{
			Clazz<?> gi3 = Clazz.of(GI3.class);
			assertClazz(gi3, GI3.class, Object.class);
			Clazz<?> gi3_gg = gi3.findGenericSuper(GG.class);
			assertClazz(gi3_gg, GG.class, Object.class, g(GI3.class, Object.class));
			Clazz<?> gi3s = Clazz.ofClasses(GI3.class, String.class);
			assertClazz(gi3s, GI3.class, String.class);
			Clazz<?> gi3s_gg = gi3s.findGenericSuper(GG.class);
			assertClazz(gi3s_gg, GG.class, String.class, g(GI3.class, String.class));
		}
		{
			Clazz<?> gi4 = Clazz.of(GI4.class);
			assertClazz(gi4, GI4.class, self());
			Clazz<?> gi4_gi = gi4.findGenericSuper(GI.class);
			assertClazz(gi4_gi, GI.class, g(GI4.class, self()));
			Clazz<?> gi4_gg = gi4.findGenericSuper(GG.class);
			assertClazz(gi4_gg, GG.class, Integer.class, g(GI4.class, self()));
			Clazz<?> gi4_gi_gg = gi4_gi.findGenericSuper(GG.class);
			assertClazz(gi4_gi_gg, GG.class, Integer.class, g(GI4.class, self()));
		}
		{
			Clazz<?> gi5 = Clazz.of(GI5.class);
			assertClazz(gi5, GI5.class, self());
			Clazz<?> gi5_gg = gi5.findGenericSuper(GG.class);
			assertClazz(gi5_gg, GG.class, g(GI5.class, self()), g(GI5.class, self()));
		}
		{
			Clazz<?> ii5 = Clazz.of(II5.class);
			assertClazz(ii5, II5.class);
			Clazz<?> ii5_gi3 = ii5.findGenericSuper(GI3.class);
			assertClazz(ii5_gi3, GI3.class, String.class);
			Clazz<?> ii5_gg = ii5.findGenericSuper(GG.class);
			assertClazz(ii5_gg, GG.class, String.class, g(GI3.class, String.class));
			Clazz<?> ii5_gi3_gg = ii5_gi3.findGenericSuper(GG.class);
			assertClazz(ii5_gi3_gg, GG.class, String.class, g(GI3.class, String.class));
		}
		{
			Clazz<?> ii6 = Clazz.of(II6.class);
			assertClazz(ii6, II6.class);
			Clazz<?> ii6_gg = ii6.findGenericSuper(GG.class);
			assertClazz(ii6_gg, GG.class, String.class, II6.class);
		}
		{
			Clazz<?> gg2 = Clazz.of(GG2.class);
			assertClazz(gg2, GG2.class, g(Comparable.class, self()), self());
			Clazz<?> gg2_gg = gg2.findGenericSuper(GG.class);
			assertClazz(gg2_gg, GG.class, g(Comparable.class, self()), g(GG2.class, g(Comparable.class, self()), self()));
		}
		{
			Clazz<?> gg3 = Clazz.of(GG3.class);
			assertClazz(gg3, GG3.class, self(), g(Comparable.class, self()));
			Clazz<?> gg3_gg = gg3.findGenericSuper(GG.class);
			assertClazz(gg3_gg, GG.class, g(Comparable.class, self()), g(GG3.class, self(), g(Comparable.class, self())));
		}
		{
			Clazz<?> ii7 = Clazz.of(II7.class);
			assertClazz(ii7, II7.class);
			Clazz<?> ii7_gg2 = ii7.findGenericSuper(GG2.class);
			assertClazz(ii7_gg2, GG2.class, String.class, II7.class);
			Clazz<?> ii7_gg = ii7.findGenericSuper(GG.class);
			assertClazz(ii7_gg, GG.class, String.class, II7.class);
			Clazz<?> ii7_gg2_gg = ii7_gg2.findGenericSuper(GG.class);
			assertClazz(ii7_gg2_gg, GG.class, String.class, II7.class);
		}
	}

	@Test
	public void testSelfReferencing2() {
		{
			Clazz<?> g1 = Clazz.of(G1.class);
			assertClazz(g1, G1.class, self(), self());
		}
		{
			Clazz<?> g2 = Clazz.of(G2.class);
			assertClazz(g2, G2.class, Object.class, self());
			Clazz<?> g2s = Clazz.ofClazzes(G2.class, Clazz.of(String.class), Clazz.raw(G2.class));
			assertClazz(g2s, G2.class, String.class, self());
		}
		{
			Clazz<?> g3 = Clazz.of(G3.class);
			assertClazz(g3, G3.class, self());
			Clazz<?> g3_g2 = g3.findGenericSuper(G2.class);
			assertClazz(g3_g2, G2.class, g(G3.class, self()), g(G3.class, self()));
		}
		{
			Clazz<?> g4 = Clazz.of(G4.class);
			assertClazz(g4, G4.class, self());
			Clazz<?> g4_g2 = g4.findGenericSuper(G2.class);
			assertClazz(g4_g2, G2.class, g(G4.class, self()), g(G4.class, self()));
		}
		{
			Clazz<?> g5 = Clazz.of(G5.class);
			assertClazz(g5, G5.class, self(), self());
		}
		{
			Clazz<?> g5x = Clazz.ofClazzes(G5X.class, Clazz.raw(G5X.class), Clazz.of(String.class), Clazz.raw(G5X.class));
			assertClazz(g5x, G5X.class, self(), String.class, self());
		}
		{
			Clazz<?> g6 = Clazz.of(G6.class);
			assertClazz(g6, G6.class, self(), self());
		}
		{
			Clazz<?> g6x = Clazz.ofClazzes(G6X.class, Clazz.raw(G6X.class), Clazz.raw(G6X.class), Clazz.of(String.class));
			assertClazz(g6x, G6X.class, self(), self(), String.class);
		}
		{
			Clazz<?> g7 = Clazz.of(G7.class);
			assertClazz(g7, G7.class, self(), self(), self());
		}
		{
			Clazz<?> g7x = Clazz.ofClazzes(G7X.class, Clazz.raw(G7X.class), Clazz.raw(G7X.class), Clazz.of(String.class), Clazz.raw(G7X.class));
			assertClazz(g7x, G7X.class, self(), self(), String.class, self());
		}
		{
			Clazz<?> g8 = Clazz.of(G8.class);
			assertClazz(g8, G8.class, self());
		}

	}

	@Test
	public void testLoopDeLoop() {
		{
			Clazz<?> loop = Clazz.of(Loop.class);
			assertTrue(2 < assertLooping(loop, Loop.class, DeLoop.class));
		}
		{
			Clazz<?> deLoop = Clazz.of(DeLoop.class);
			assertTrue(2 < assertLooping(deLoop, DeLoop.class, Loop.class));
		}
		{
			Clazz<?> loopDeLoop = Clazz.of(LoopDeLoop.class);
			assertClazz(loopDeLoop, LoopDeLoop.class, g(List.class, self()), g(List.class, self()));
		}
	}

	private int assertLooping(Clazz<?> current, Class<?> thisClass, Class<?> otherClass) {
		assertSame(thisClass, current.clazz);
		if (current.generics.isEmpty()) {
			return 1;
		}
		assertEquals(1, current.generics.size());
		return 1 + assertLooping(current.generics.get(0), otherClass, thisClass);
	}

	@Test
	public void testStuffThingsGroup() {
		Clazz<?> stringGroup = Clazz.of(StringGroup.class);
		Clazz<?> group = stringGroup.findGenericSuper(Group.class);
		assertClazz(group, Group.class, Number.class, String.class);
		Clazz<?> things = group.findGenericSuper(Things.class);
		assertClazz(things, Things.class, Number.class);
		Clazz<?> stuff = things.findGenericSuper(Stuff.class);
		assertClazz(stuff, Stuff.class, Number.class, Long.class);
		Clazz<?> stuff2 = stringGroup.findGenericSuper(Stuff.class);
		assertClazz(stuff2, Stuff.class, Number.class, Long.class);
	}

	@Test
	public void testUrlLink() {
		{
			Clazz<?> urlLink = Clazz.of(UrlLink.class);
			Clazz<?> urlLink_parameterizableLink = urlLink.findGenericSuper(ParameterizableLink.class);
			assertClazz(urlLink_parameterizableLink, ParameterizableLink.class, UrlLink.class);
		}
		{
			Clazz<?> badLink = Clazz.of(BadLink.class);
			Clazz<?> badLink_parameterizableLink = badLink.findGenericSuper(ParameterizableLink.class);
			assertClazz(badLink_parameterizableLink, r(ParameterizableLink.class));
		}
		{
			Clazz<?> weird = Clazz.of(Weird.class);
			weird.methods();
			Clazz<?> weird2 = Clazz.of(Weird2.class);
			weird2.methods();
		}
	}

	@Test
	public void testWildcardInBound() {
		Clazz<?> weird = Clazz.of(WildOuter.class);
		assertClazz(weird, WildOuter.class, g(WildInner.class, g(List.class, Object.class)));
	}

	@Test
	public void testSelfReferencingRaw1() {
		// quite useless i guess, but it's already there, so I leave it
		{
			Clazz<?> ggr = Clazz.of(GGr.class);
			assertClazz(ggr, GGr.class, Object.class, self());
		}
		{
			Clazz<?> gir1 = Clazz.of(GIr1.class);
			assertClazz(gir1, GIr1.class, g(GGr.class, Object.class, self()));
			Clazz<?> gir1_ggr = gir1.findGenericSuper(GGr.class);
			assertClazz(gir1_ggr, GGr.class, Integer.class, g(GGr.class, Object.class, self()));
		}
		{
			Clazz<?> gir2 = Clazz.of(GIr2.class);
			assertClazz(gir2, GIr2.class, g(GGr.class, Object.class, self()));
			Clazz<?> gir2_ggr = gir2.findGenericSuper(GGr.class);
			assertClazz(gir2_ggr, GGr.class, Object.class, self());
		}
		{
			Clazz<?> gir3 = Clazz.of(GIr3.class);
			assertClazz(gir3, GIr3.class, g(GGr.class, Integer.class, self()));
			Clazz<?> gir3_ggr = gir3.findGenericSuper(GGr.class);
			assertClazz(gir3_ggr, GGr.class, Object.class, self());
		}
		{
			Clazz<?> gir4 = Clazz.of(GIr4.class);
			assertClazz(gir4, GIr4.class, g(GGr.class, Integer.class, self()));
			Clazz<?> gir4_ggr = gir4.findGenericSuper(GGr.class);
			assertClazz(gir4_ggr, GGr.class, Integer.class, self());
		}
		{
			Clazz<?> gir5 = Clazz.of(GIr5.class);
			assertClazz(gir5, GIr5.class, g(GG.class, Object.class, self()));
			Clazz<?> gir5_gg = gir5.findGenericSuper(GG.class);
			assertClazz(gir5_gg, GG.class, Object.class, self());
		}
		{
			Clazz<?> gir6 = Clazz.of(GIr6.class);
			assertClazz(gir6, GIr6.class, g(GG.class, Integer.class, self()));
			Clazz<?> gir6_gg = gir6.findGenericSuper(GG.class);
			assertClazz(gir6_gg, GG.class, Object.class, self());
		}
		{
			Clazz<?> iir1 = Clazz.of(IIr1.class);
			assertClazz(iir1, IIr1.class);
			Clazz<?> iir1_gir1 = iir1.findGenericSuper(GIr1.class);
			assertClazz(iir1_gir1, GIr1.class, IIr1.class);
			Clazz<?> iir1_gir1_ggr = iir1_gir1.findGenericSuper(GGr.class);
			assertClazz(iir1_gir1_ggr, GGr.class, Integer.class, IIr1.class);
			Clazz<?> iir1_ggr = iir1.findGenericSuper(GGr.class);
			assertClazz(iir1_ggr, GGr.class, Integer.class, IIr1.class);
		}
		{
			Clazz<?> iir2 = Clazz.of(IIr2.class);
			assertClazz(iir2, IIr2.class);
			Clazz<?> iir2_gir2 = iir2.findGenericSuper(GIr2.class);
			assertClazz(iir2_gir2, GIr2.class, IIr2.class);
			Clazz<?> iir2_gir2_ggr = iir2_gir2.findGenericSuper(GGr.class);
			assertClazz(iir2_gir2_ggr, GGr.class, Object.class, self());
			Clazz<?> iir2_ggr = iir2.findGenericSuper(GGr.class);
			assertClazz(iir2_ggr, GGr.class, Object.class, self());
		}
		{
			Clazz<?> iir4 = Clazz.of(IIr4.class);
			assertClazz(iir4, IIr4.class);
			Clazz<?> iir4_gir4 = iir4.findGenericSuper(GIr4.class);
			assertClazz(iir4_gir4, GIr4.class, IIr4.class);
			Clazz<?> iir4_gir4_ggr = iir4_gir4.findGenericSuper(GGr.class);
			assertClazz(iir4_gir4_ggr, GGr.class, Integer.class, IIr4.class);
			Clazz<?> iir4_ggr = iir4.findGenericSuper(GGr.class);
			assertClazz(iir4_ggr, GGr.class, Integer.class, IIr4.class);
		}
		{
			Clazz<?> iir5 = Clazz.of(IIr5.class);
			assertClazz(iir5, IIr5.class);
			Clazz<?> iir5_gir5 = iir5.findGenericSuper(GIr5.class);
			assertClazz(iir5_gir5, GIr5.class, IIr5.class);
			Clazz<?> iir5_gir5_gg = iir5_gir5.findGenericSuper(GG.class);
			assertClazz(iir5_gir5_gg, GG.class, Object.class, self());
			Clazz<?> iir5_gg = iir5.findGenericSuper(GG.class);
			assertClazz(iir5_gg, GG.class, Object.class, self());
		}
		{
			Clazz<?> ii2r1 = Clazz.of(II2r1.class);
			assertClazz(ii2r1, II2r1.class);
			Clazz<?> ii2r1_gir1 = ii2r1.findGenericSuper(GIr1.class);
			assertClazz(ii2r1_gir1, GIr1.class, IIr1.class);
			Clazz<?> ii2r1_gir1_ggr = ii2r1_gir1.findGenericSuper(GGr.class);
			assertClazz(ii2r1_gir1_ggr, GGr.class, Integer.class, IIr1.class);
			Clazz<?> ii2r1_ggr = ii2r1.findGenericSuper(GGr.class);
			assertClazz(ii2r1_ggr, GGr.class, Integer.class, IIr1.class);
		}
		{
			Clazz<?> ii2r2 = Clazz.of(II2r2.class);
			assertClazz(ii2r2, II2r2.class);
			Clazz<?> ii2r2_gir2 = ii2r2.findGenericSuper(GIr2.class);
			assertClazz(ii2r2_gir2, GIr2.class, IIr2.class);
			Clazz<?> ii2r2_gir2_ggr = ii2r2_gir2.findGenericSuper(GGr.class);
			assertClazz(ii2r2_gir2_ggr, GGr.class, Object.class, self());
			Clazz<?> ii2r2_ggr = ii2r2.findGenericSuper(GGr.class);
			assertClazz(ii2r2_ggr, GGr.class, Object.class, self());
		}
		{
			Clazz<?> ii2r4 = Clazz.of(II2r4.class);
			assertClazz(ii2r4, II2r4.class);
			Clazz<?> ii2r4_gir4 = ii2r4.findGenericSuper(GIr4.class);
			assertClazz(ii2r4_gir4, GIr4.class, IIr4.class);
			Clazz<?> ii2r4_gir4_ggr = ii2r4_gir4.findGenericSuper(GGr.class);
			assertClazz(ii2r4_gir4_ggr, GGr.class, Integer.class, IIr4.class);
			Clazz<?> ii2r4_ggr = ii2r4.findGenericSuper(GGr.class);
			assertClazz(ii2r4_ggr, GGr.class, Integer.class, IIr4.class);
		}
		{
			Clazz<?> ii2r5 = Clazz.of(II2r5.class);
			assertClazz(ii2r5, II2r5.class);
			Clazz<?> ii2r5_gir5 = ii2r5.findGenericSuper(GIr5.class);
			assertClazz(ii2r5_gir5, GIr5.class, IIr5.class);
			Clazz<?> ii2r5_gir5_gg = ii2r5_gir5.findGenericSuper(GG.class);
			assertClazz(ii2r5_gir5_gg, GG.class, Object.class, self());
			Clazz<?> ii2r5_gg = ii2r5.findGenericSuper(GG.class);
			assertClazz(ii2r5_gg, GG.class, Object.class, self());
		}
	}

	@Test
	public void testSelfReferencingRaw2() {
		{
			Clazz<?> rawSelf = Clazz.of(RawSelf.class);
			assertClazz(rawSelf, RawSelf.class, self());
		}
		{
			Clazz<?> rawMe = Clazz.ofClasses(RawMe.class, Integer.class);
			assertClazz(rawMe, RawMe.class, Integer.class);
			Clazz<?> rawMe_rawSelf = rawMe.findGenericSuper(RawSelf.class);
			assertClazz(rawMe_rawSelf, RawSelf.class, g(RawMe.class, Integer.class));
		}
		{
			Clazz<?> rawMeWild = Clazz.ofClasses(RawMeWild.class, Integer.class);
			assertClazz(rawMeWild, RawMeWild.class, Integer.class);
			Clazz<?> rawMeWild_rawSelf = rawMeWild.findGenericSuper(RawSelf.class);
			assertClazz(rawMeWild_rawSelf, RawSelf.class, g(RawMeWild.class, Number.class));
		}
		{
			Clazz<?> rawMeRaw = Clazz.ofClasses(RawMeRaw.class, Integer.class);
			assertClazz(rawMeRaw, RawMeRaw.class, Integer.class);
			Clazz<?> rawMeRaw_rawSelf = rawMeRaw.findGenericSuper(RawSelf.class);
			assertClazz(rawMeRaw_rawSelf, RawSelf.class, g(RawMeRaw.class, Number.class));
		}
		{
			Clazz<?> rawMeRawSelf = Clazz.ofClasses(RawMeRawSelf.class, Integer.class);
			assertClazz(rawMeRawSelf, RawMeRawSelf.class, Integer.class);
			Clazz<?> rawMeRawSelf_rawSelf = rawMeRawSelf.findGenericSuper(RawSelf.class);
			assertClazz(rawMeRawSelf_rawSelf, RawSelf.class, self());
		}
		{
			Clazz<?> rawRawSelf = Clazz.ofClasses(RawRawSelf.class, Integer.class);
			assertClazz(rawRawSelf, RawRawSelf.class, Integer.class);
			Clazz<?> rawRawSelf_rawSelf = rawRawSelf.findGenericSuper(RawSelf.class);
			assertClazz(rawRawSelf_rawSelf, RawSelf.class, self());
		}
	}

	@Test
	public void testRawSuper() {
		{
			Clazz<?> rawHolder = Clazz.of(RawHolder.class);
			assertClazz(rawHolder, RawHolder.class);
			Clazz<?> rawHolder_StringHolder = rawHolder.findGenericSuper(StringHolder.class);
			assertClazz(rawHolder_StringHolder, StringHolder.class, String.class);
		}
		{
			Clazz<?> iRawHolder = Clazz.of(IRawHolder.class);
			assertClazz(iRawHolder, IRawHolder.class);
			Clazz<?> iRawHolder_IStringHolder = iRawHolder.findGenericSuper(IStringHolder.class);
			assertClazz(iRawHolder_IStringHolder, IStringHolder.class, String.class);
		}
		{
			Clazz<?> iRawHolderImpl = Clazz.of(IRawHolderImpl.class);
			assertClazz(iRawHolderImpl, IRawHolderImpl.class);
			Clazz<?> iRawHolderImpl_IStringHolder = iRawHolderImpl.findGenericSuper(IStringHolder.class);
			assertClazz(iRawHolderImpl_IStringHolder, IStringHolder.class, String.class);
		}
	}

	@Test
	public void testRawGenericBounds() {
		{
			Clazz<?> stringHolderHolder = Clazz.of(StringHolderHolder.class);
			assertClazz(stringHolderHolder, StringHolderHolder.class, g(StringHolder.class, String.class));
		}
		{
			Clazz<?> iStringHolderHolder = Clazz.of(IStringHolderHolder.class);
			assertClazz(iStringHolderHolder, IStringHolderHolder.class, g(StringHolder.class, String.class));
		}
		{
			Clazz<?> eyeStringHolderHolder = Clazz.of(iStringHolderHolder.class);
			assertClazz(eyeStringHolderHolder, iStringHolderHolder.class, g(IStringHolder.class, String.class));
		}
		{
			Clazz<?> iiStringHolderHolder = Clazz.of(IiStringHolderHolder.class);
			assertClazz(iiStringHolderHolder, IiStringHolderHolder.class, g(IStringHolder.class, String.class));
		}
	}

	@Test
	public void testCoverageFieldPattern() {
		Property.PropertyList list = Clazz.of(CoverageTest.class).getProperties();
		assertEquals(2, list.size());
		assertEquals("hi", list.get(0).getSnakeCase());
		assertEquals("__hello__", list.get(1).getSnakeCase());
	}

	@Test
	public void testInvalidGenerics() {
		Clazz<?> obj = Clazz.of(Object.class);
		Clazz<?> string = Clazz.of(String.class);
		Clazz<?> number = Clazz.of(Number.class);
		Clazz<?> integer = Clazz.of(Integer.class);
		Clazz<?> rawList = Clazz.raw(List.class);
		Clazz<?> objList = Clazz.ofClasses(List.class, Object.class);
		Clazz<?> numList = Clazz.ofClasses(List.class, Number.class);
		Clazz<?> intList = Clazz.ofClasses(List.class, Integer.class);
		Clazz<?> strList = Clazz.ofClasses(List.class, String.class);

		// non-generic class rejects generics
		new Clazz<>(String.class);
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(String.class, obj));

		// generic class requires 0 or correct number of generics
		new Clazz<>(Function.class);
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(Function.class, obj));
		new Clazz<>(Function.class, obj, obj);
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(List.class, obj, obj, obj));

		// top level bounds are enforced
		new Clazz<>(NumberHolder.class, number);
		new Clazz<>(NumberHolder.class, integer);
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(NumberHolder.class, obj));
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(NumberHolder.class, string));

		// inner bounds are enforced
		new Clazz<>(NumberListHolder.class);
		new Clazz<>(NumberListHolder.class, rawList);
		new Clazz<>(NumberListHolder.class, numList);
		new Clazz<>(NumberListHolder.class, intList);
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(StringHolder.class, objList));
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(StringHolder.class, strList));

		// inner bounds with arrays
		new Clazz<>(NumberListHolder[].class);
		new Clazz<>(NumberListHolder[].class, rawList);
		new Clazz<>(NumberListHolder[].class, numList);
		new Clazz<>(NumberListHolder[].class, intList);
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(StringHolder[].class, objList));
		assertThrows(IllegalArgumentException.class, () -> new Clazz<>(StringHolder[].class, strList));

		// feel free to add more
	}

	@SuppressWarnings("unused")
	public static class CoverageTest {
		public int hi;
		public int __hello__;
		public int __$hej$__;
	}

	interface Ninja {}

	public static class Normal<T extends Ninja> {
		@SuppressWarnings("unused")
		public T myTea() {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class Weird<T extends Normal> {
		@SuppressWarnings("unused")
		public T myTea() {
			return null;
		}
	}

	@SuppressWarnings({"rawtypes", "unused"})
	public static class Weird2<T extends Weird2> {}

	@SuppressWarnings("rawtypes")
	public static class BadLink extends ParameterizableLink {}

	public static class UrlLink extends ParameterizableLink<UrlLink> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public static class ParameterizableLink<T extends ParameterizableLink> {}

	@SuppressWarnings("unused")
	public static abstract class WildInner<T extends List<?>> {}

	@SuppressWarnings("unused")
	public static class WildOuter<T extends WildInner<?>> {}

	public static class GenericClass<T> {
		@SuppressWarnings("unused")
		public void method(T t) {

		}
	}

	public static class GenericClassImpl extends GenericClass<String> {

	}

	@SuppressWarnings("unused")
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
		@Override
		void method(String myString);
	}

	@SuppressWarnings("unused")
	public class Muh implements NonGenericInterfaceExplicit {
		@Override
		public void method(String myString) {

		}

		@Override
		public String method2(int i) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public class ClassWithMethodsOfDifferentVisibility {
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

	@SuppressWarnings("unused")
	public static class MyArrayParent {
		public MyArray getArray() {
			return new MyArray();
		}

		public String nonGenericMethod(int input) {
			return "Hello";
		}
	}

	@SuppressWarnings("unused")
	public interface GG<A, B extends GG<A, B>> {}

	public interface GI<X extends GG<Integer, X>> extends GG<Integer, X> {}

	public interface II extends GI<II> {}

	public interface II2 extends GI<II> {}

	public interface GI2<X extends GI<X>> extends GG<Integer, X> {}

	public interface II3 extends GI2<II> {}

	public interface GI3<X> extends GG<X, GI3<X>> {}

	public interface GI4<X extends GI4<X>> extends GI<X> {}

	public interface GI5<X extends GI5<X>> extends GG<GI5<X>, X> {}

	public interface II5 extends GI3<String> {}

	public interface II6 extends GG<String, II6> {}

	public interface GG2<H extends Comparable<H>, J extends GG2<H, J>> extends GG<H, J> {}

	public interface GG3<J extends GG3<J, H>, H extends Comparable<H>> extends GG<H, J> {}

	public interface II7 extends GG2<String, II7> {}

	@SuppressWarnings("unused")
	public interface G1<G extends G1<G, G>, H extends G1<H, H>> {}

	@SuppressWarnings("unused")
	public interface G2<X, SELF extends G2<X, SELF>> {}

	public interface G3<SELF extends G3<SELF>> extends G2<SELF, SELF> {}

	public interface G4<SELF extends G4<SELF>> extends G2<SELF, G4<SELF>> {}

	public interface G5<A extends G5<A, B>, B extends G5<A, B>> {}

	public interface G5X<A extends G5X<A, X, B>, X, B extends G5X<A, X, B>> {}

	public interface G6<A extends G6<B, A>, B extends G6<A, B>> {}

	public interface G6X<A extends G6X<B, A, X>, B extends G6X<A, B, X>, X> {}

	public interface G7<A extends G7<B, C, A>, B extends G7<C, A, B>, C extends G7<A, B, C>> {}

	public interface G7X<A extends G7X<B, C, X, A>, B extends G7X<C, A, X, B>, X, C extends G7X<A, B, X, C>> {}

	@SuppressWarnings("unused")
	public interface G8<T extends G8<? extends G8<T>>> {}

	@SuppressWarnings("unused")
	public interface Loop<X extends DeLoop<? extends Loop<X>>> {}

	@SuppressWarnings("unused")
	public interface DeLoop<X extends Loop<? extends DeLoop<X>>> {}

	public static class LoopDeLoop<F extends List<D>, D extends List<F>> {}

	@SuppressWarnings("unused")
	public interface Stuff<S, X> {}

	public interface Things<T> extends Stuff<T, Long> {}

	@SuppressWarnings("unused")
	public interface Group<G, B> extends Things<G> {}

	public interface StringGroup extends Group<Number, String> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface GGr<A, B extends GGr> {}

	@SuppressWarnings("rawtypes")
	public interface GIr1<X extends GGr> extends GGr<Integer, X> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface GIr2<X extends GGr> extends GGr {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface GIr3<X extends GGr<Integer, X>> extends GGr {}

	public interface GIr4<X extends GGr<Integer, X>> extends GGr<Integer, X> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface GIr5<X extends GG> extends GG {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface GIr6<X extends GG<Integer, X>> extends GG {}

	public interface IIr1 extends GIr1<IIr1> {}

	public interface IIr2 extends GIr2<IIr2> {}

	public interface IIr4 extends GIr4<IIr4> {}

	public interface IIr5 extends GIr5<IIr5> {}

	public interface II2r1 extends GIr1<IIr1> {}

	public interface II2r2 extends GIr2<IIr2> {}

	public interface II2r4 extends GIr4<IIr4> {}

	public interface II2r5 extends GIr5<IIr5> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface RawSelf<T extends RawSelf> {}

	public interface RawMe<T extends Number> extends RawSelf<RawMe<T>> {}

	@SuppressWarnings("unused")
	public interface RawMeWild<T extends Number> extends RawSelf<RawMeWild<?>> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface RawMeRaw<T extends Number> extends RawSelf<RawMeRaw> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface RawMeRawSelf<T extends Number> extends RawSelf<RawSelf> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface RawRawSelf<T extends Number> extends RawSelf {}

	@SuppressWarnings("unused")
	public static class StringHolder<T extends String> {}

	@SuppressWarnings("rawtypes")
	public static class RawHolder extends StringHolder {}

	@SuppressWarnings("unused")
	public interface IStringHolder<T extends String> {}

	@SuppressWarnings("rawtypes")
	public interface IRawHolder extends IStringHolder {}

	@SuppressWarnings("rawtypes")
	public static class IRawHolderImpl implements IStringHolder {}

	@SuppressWarnings({"rawtypes", "unused"})
	public static class StringHolderHolder<T extends StringHolder> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface IStringHolderHolder<T extends StringHolder> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public static class iStringHolderHolder<T extends IStringHolder> {}

	@SuppressWarnings({"rawtypes", "unused"})
	public interface IiStringHolderHolder<T extends IStringHolder> {}

	@SuppressWarnings("unused")
	public static class NumberHolder<T extends Number> {}

	@SuppressWarnings("unused")
	public static class NumberListHolder<T extends List<? extends Number>> {}
}
