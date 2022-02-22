package io.ran;

import javax.inject.Inject;

public class WithConstructorInjection {
	@PrimaryKey
	private String id;
	private transient MyDep myDep;

	@Inject
	public WithConstructorInjection(MyDep myDep) {

		this.myDep = myDep;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String morphed() {
		return myDep.morphId(id);
	}
}
