package io.ran;

@Mapper(dbType = TestDbType.class)
public class HeadLights {
	@PrimaryKey
	private String on;

	public String getOn() {
		return on;
	}

	public void setOn(String on) {
		this.on = on;
	}
}
