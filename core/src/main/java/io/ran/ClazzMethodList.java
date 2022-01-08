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
		return stream().filter(cm -> cm.getName().equals(method)).filter(cm -> cm.getReturnType().clazz.equals(returnType)).filter(cm -> {
			if (cm.parameters().size() != parameterTypes.size()) {
				return false;
			}
			for(int i=0;i<cm.parameters().size();i++) {
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
