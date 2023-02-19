/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClazzMethodList extends ArrayList<ClazzMethod> {
	public ClazzMethodList(Collection<ClazzMethod> values) {
		super(values);
	}

	private Optional<ClazzMethod> findInternal(String method, Class<?> returnType, List<Class> parameterTypes) {
		return stream()
				.filter(cm -> cm.getName().equals(method))
				.filter(cm -> cm.getReturnType().clazz.equals(returnType))
				.filter(cm -> {
			if (cm.parameters().size() != parameterTypes.size()) {
				return false;
			}
			for (int i = 0; i < cm.parameters().size(); i++) {
				if (!cm.parameters().get(i).getBestEffortClazz().clazz.equals(parameterTypes.get(i))) {
					return false;
				}
			}
			return true;
		}).findFirst();
	}

	public Optional<ClazzMethod> find(String method, Clazz<?> returnType, Clazz<?>... parameterTypes) {
		return findInternal(method, returnType.clazz, Stream.of(parameterTypes).map(c -> c.clazz).collect(Collectors.toList()));
	}

	public Optional<ClazzMethod> find(String method, Class<?> returnType, Class<?>... parameterTypes) {
		return findInternal(method, returnType, Stream.of(parameterTypes).collect(Collectors.toList()));
	}

	public Optional<ClazzMethod> find(String method, Clazz<?> returnType, List<ClazzMethodParameter> parameterTypes) {
		return findInternal(method, returnType.clazz, parameterTypes.stream().map(c -> c.getClazz().clazz).collect(Collectors.toList()));
	}

	public Optional<ClazzMethod> find(ClazzMethod cm) {
		return find(cm.getName(), cm.getReturnType(), cm.parameters());
	}
}
