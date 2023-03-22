/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
