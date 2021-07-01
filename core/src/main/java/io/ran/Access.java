package io.ran;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;

public enum Access {
	Public(Opcodes.ACC_PUBLIC), Protected(Opcodes.ACC_PROTECTED), Private(Opcodes.ACC_PRIVATE);

	private int opCode;

	Access(int opCode) {
		this.opCode = opCode;
	}

	public static Access of(int modifiers) {
		return Arrays.asList(values()).stream().filter(a -> (modifiers & a.opCode) == 1).findFirst().orElseThrow(() -> new RuntimeException("Could not find access for modifier: "+modifiers));
	}

	public int getOpCode() {
		return opCode;
	}
}
