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

	@Override
	public String toString() {
		return "HeadLights{" +
				"on='" + on + '\'' +
				'}';
	}
}
