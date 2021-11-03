package io.ran;

import io.ran.testclasses.Regular;
import io.ran.token.Token;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClazzTest {
	@Test
	public void relations_via() {
		List<RelationDescriber> relations = Clazz.of(RelationFrom.class).getRelations();

		assertEquals(1, relations.size());
		assertEquals(2, relations.get(0).getVia().size());
		assertEquals(RelationFrom.class, relations.get(0).getVia().get(0).getFromClass().clazz);
		assertEquals(Token.of("id"), relations.get(0).getVia().get(0).getFromKeys().get(0).getToken());
		assertEquals(Token.of("relation","from","id"), relations.get(0).getVia().get(0).getToKeys().get(0).getToken());
		assertEquals(RelationVia.class, relations.get(0).getVia().get(0).getToClass().clazz);
		assertEquals(RelationVia.class, relations.get(0).getVia().get(1).getFromClass().clazz);
		assertEquals(RelationTo.class, relations.get(0).getVia().get(1).getToClass().clazz);
		assertEquals(Token.of("relation","to","id"), relations.get(0).getVia().get(1).getFromKeys().get(0).getToken());
		assertEquals(Token.of("id"), relations.get(0).getVia().get(1).getToKeys().get(0).getToken());
	}

	@Test
	public void relations_viaDescribed() {
		List<RelationDescriber> relations = Clazz.of(DescribedRelationFrom.class).getRelations();

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
		List<RelationDescriber> relations = Clazz.of(DescribedRelationVia.class).getRelations();

		assertEquals(Token.of("muh"), relations.get(0).getToKeys().get(0).getToken());
		assertEquals(Token.of("cat"), relations.get(0).getFromKeys().get(0).getToken());

		assertEquals(Token.of("cat"), relations.get(0).inverse().getToKeys().get(0).getToken());
		assertEquals(Token.of("muh"), relations.get(0).inverse().getFromKeys().get(0).getToken());

	}

	@Test
	public void keys_multi() {
		KeySet keys = Clazz.of(RelationVia.class).getKeys().getPrimary();

		assertEquals(2, keys.size());
		assertEquals(Token.of("relation","from","id"), keys.get(0).getToken());
		assertEquals(Token.of("relation","to","id"), keys.get(1).getToken());
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
		List<RelationDescriber> relations = Clazz.of(ClassWithRelationAndOtherKey.class).getRelations();
		assertEquals(relations.get(0).getFromKeys().size(), relations.get(0).getToKeys().size());
		assertEquals(1, relations.get(0).getToKeys().size());
		assertEquals("id", relations.get(0).getToKeys().get(0).getToken().snake_case());
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
		private int classWithRelationandOttherKeyId;
		@Relation
		private ClassWithRelationAndOtherKey classWithRelationandOttherKey;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getClassWithRelationandOttherKeyId() {
			return classWithRelationandOttherKeyId;
		}

		public void setClassWithRelationandOttherKeyId(int classWithRelationandOttherKeyId) {
			this.classWithRelationandOttherKeyId = classWithRelationandOttherKeyId;
		}

		public ClassWithRelationAndOtherKey getClassWithRelationandOttherKey() {
			return classWithRelationandOttherKey;
		}

		public void setClassWithRelationandOttherKey(ClassWithRelationAndOtherKey classWithRelationandOttherKey) {
			this.classWithRelationandOttherKey = classWithRelationandOttherKey;
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