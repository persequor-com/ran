package io.ran;

public class TestClassWrapper extends TestClass {
	public String toString() {
		return getId()+String.valueOf(getaShort())+"-"+String.valueOf(getInteger())+"-"+String.valueOf(getaLong());
	}

	public void setNumbers(int i, int i1, int i2) {
		setaShort((short) i);
		setInteger(i1);
		setaLong(i2);
	}
}
