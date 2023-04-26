/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Door;
import io.ran.testclasses.Engine;
import io.ran.testclasses.GraphNode;
import io.ran.testclasses.GraphNodeLink;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class TypeDescriberTest {
	private GenericFactory factory;

	@BeforeClass
	public static void beforeClass() {

	}

	@Before
	public void setup() {
		factory = new GuiceHelper().factory;
	}

	@Test
	public void car() throws Throwable {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);
		assertEquals(8, describer.fields().size());
		assertEquals(1, describer.primaryKeys().size());
		assertEquals("id", describer.primaryKeys().get(0).getToken().snake_case());
		assertEquals(String.class, describer.primaryKeys().get(0).getType().clazz);
		assertEquals(4, describer.relations().size());
		assertEquals("id", describer.relations().get(Door.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Door.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals(Car.class, describer.relations().get(Door.class).get().getFromKeys().get(0).getOn().clazz);

		assertEquals("carId", describer.relations().get(Door.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Door.class).get().getToKeys().get(0).getType().clazz);
		assertEquals(Door.class, describer.relations().get(Door.class).get().getToKeys().get(0).getOn().clazz);

		assertEquals(RelationType.OneToMany, describer.relations().get(Door.class).get().getType());


		assertEquals("engineId", describer.relations().get(Engine.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Engine.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("id", describer.relations().get(Engine.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Engine.class).get().getToKeys().get(0).getType().clazz);
		assertEquals(RelationType.OneToOne, describer.relations().get(Engine.class).get().getType());

		assertEquals("id", describer.relations().get(HeadLights.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(HeadLights.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("on", describer.relations().get(HeadLights.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(HeadLights.class).get().getToKeys().get(0).getType().clazz);
		assertEquals(RelationType.OneToOne, describer.relations().get(HeadLights.class).get().getType());

	}

	@Test
	public void door() {
		TypeDescriber<Door> describer = TypeDescriberImpl.getTypeDescriber(Door.class);

		assertEquals("carId", describer.relations().get(Car.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Car.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("id", describer.relations().get(Car.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Car.class).get().getToKeys().get(0).getType().clazz);
	}


	@Test
	public void engine() {
		TypeDescriber<Engine> describer = TypeDescriberImpl.getTypeDescriber(Engine.class);

		assertEquals("id", describer.relations().get(Car.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Car.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("engineId", describer.relations().get(Car.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Car.class).get().getToKeys().get(0).getType().clazz);
	}

	@Test
	public void getValue() {
		TypeDescriber<Engine> describer = TypeDescriberImpl.getTypeDescriber(Engine.class);

		Engine engine = factory.get(Engine.class);
		Mapping engineMapping = (Mapping) engine;
		engine.setId(UUID.randomUUID());

		Object actual = engineMapping._getValue(describer.fields().get(Token.of("id")));
		assertEquals(engine.getId(), actual);
	}

	@Test
	public void getKey() {
		Engine engine = factory.get(Engine.class);
		engine.setId(UUID.randomUUID());

		CompoundKey actual = ((Mapping) engine)._getKey();
		assertEquals(1, actual.getValues().size());
		assertEquals(engine.getId(), actual.getValue(Token.of("id")));
	}

	@Test
	public void setRelation() {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);

		Engine engine = factory.get(Engine.class);
		Car car = factory.get(Car.class);
		Mapping carMapping = (Mapping) car;
		carMapping._setRelation(describer.relations().get(0), engine);

		assertSame(engine, car.getEngine());
	}

	@Test
	public void setCollectionRelation() {
		TypeDescriber<Engine> describer = TypeDescriberImpl.getTypeDescriber(Engine.class);

		Engine engine = factory.get(Engine.class);
		Mapping engineMapping = (Mapping) engine;
		Car car = new Car();
		engineMapping._setRelation(describer.relations().get(0), Collections.singletonList(car));

		assertSame(car, engine.getCars().stream().findFirst().get());
	}

	@Test
	public void getRelation() {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);
		Car car = factory.get(Car.class);
		Mapping carMapping = (Mapping) car;

		Object relation = carMapping._getRelation(describer.relations().get(0));
		assertNull(relation);

		Engine engine = new Engine();
		engine.setId(UUID.randomUUID());
		car.setEngine(engine);
		relation = carMapping._getRelation(describer.relations().get(0));
		assertSame(engine, relation);
	}

	@Test
	public void isChanged() {
		Car car = factory.get(Car.class);
		Mapping carMapping = (Mapping) car;

		assertFalse(carMapping._isChanged());

		car.setBrand(Brand.Porsche);

		assertTrue(carMapping._isChanged());
	}

	@Test
	public void handleGraphs() {
		TypeDescriber<GraphNode> describer = TypeDescriberImpl.getTypeDescriber(GraphNode.class);
		assertEquals(Clazz.of(GraphNodeLink.class), describer.relations().get("next_nodes").getToKeys().get(0).getOn());
		assertEquals("from_id", describer.relations().get("next_nodes").getToKeys().get(0).getProperty().getSnakeCase());
		assertEquals("id", describer.relations().get("next_nodes").getFromKeys().get(0).getProperty().getSnakeCase());
		List<RelationDescriber> via = describer.relations().get("next_nodes").getVia();
		assertEquals("id", via.get(0).getFromKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNode.class), via.get(0).getFromKeys().get(0).getOn());
		assertEquals("from_id", via.get(0).getToKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNodeLink.class), via.get(0).getToKeys().get(0).getOn());
		assertEquals("to_id", via.get(1).getFromKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNodeLink.class), via.get(1).getFromKeys().get(0).getOn());
		assertEquals("id", via.get(1).getToKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNode.class), via.get(1).getToKeys().get(0).getOn());

		assertEquals(Clazz.of(GraphNodeLink.class), describer.relations().get("previous_nodes").getToKeys().get(0).getOn());
		assertEquals("to_id", describer.relations().get("previous_nodes").getToKeys().get(0).getProperty().getSnakeCase());
		assertEquals("id", describer.relations().get("previous_nodes").getFromKeys().get(0).getProperty().getSnakeCase());
		via = describer.relations().get("previous_nodes").getVia();
		assertEquals("id", via.get(0).getFromKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNode.class), via.get(0).getFromKeys().get(0).getOn());
		assertEquals("to_id", via.get(0).getToKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNodeLink.class), via.get(0).getToKeys().get(0).getOn());
		assertEquals("from_id", via.get(1).getFromKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNodeLink.class), via.get(1).getFromKeys().get(0).getOn());
		assertEquals("id", via.get(1).getToKeys().get(0).getProperty().getSnakeCase());
		assertEquals(Clazz.of(GraphNode.class), via.get(1).getToKeys().get(0).getOn());
	}

	@Test
	public void keys_multi() {
		KeySet keys = Clazz.of(RelationVia.class).getKeys().getPrimary();

		assertEquals(2, keys.size());
		assertEquals(Token.of("relation", "from", "id"), keys.get(0).getToken());
		assertEquals(Token.of("relation", "to", "id"), keys.get(1).getToken());
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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
}
