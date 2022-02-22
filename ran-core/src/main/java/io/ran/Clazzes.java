package io.ran;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Clazzes extends ArrayList<Clazz> {

	public String getDescriptor() {
		return stream().map(Clazz::getDescriptor).collect(Collectors.joining());
	}

	public String getSignature() {
		return stream().map(Clazz::getSignature).collect(Collectors.joining());
	}


	public String[] toDescriptorArray() {
		return stream().map(Clazz::getDescriptor).toArray(String[]::new);
	}
}
