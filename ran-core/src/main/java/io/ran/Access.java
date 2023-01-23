/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;

public enum Access {
	Public(Opcodes.ACC_PUBLIC), Protected(Opcodes.ACC_PROTECTED), Private(Opcodes.ACC_PRIVATE), Synthetic(Opcodes.ACC_SYNTHETIC);

	private int opCode;

	Access(int opCode) {
		this.opCode = opCode;
	}

	public static Access of(Integer modifiers) {
		return Arrays.stream(values())
				.filter(a -> (modifiers & a.opCode) == a.opCode).
				findFirst().orElseThrow(() -> new RuntimeException("Could not find access for modifier: " + modifiers));
	}

	public int getOpCode() {
		return opCode;
	}

	public static boolean isSyntheticMethod(Integer modifiers) {
		return (modifiers & Synthetic.opCode) == Synthetic.opCode;
	}
}
