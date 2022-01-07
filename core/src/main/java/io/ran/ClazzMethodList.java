package io.ran;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ClazzMethodList extends ArrayList<ClazzMethod> {
	public ClazzMethodList(Collection<ClazzMethod> values) {
		super(values);
	}

	public Optional<ClazzMethod> find(String method, Class<?>... parameterTypes) {
		return stream().filter(cm -> cm.getName().equals(method)).filter(cm -> {
			if (cm.parameters().size() != parameterTypes.length) {
				return false;
			}
			for(int i=0;i<cm.parameters().size();i++) {
				if (!cm.parameters().get(i).getBestEffortClazz().clazz.equals(parameterTypes[i])) {
					return false;
				}
			}
			return true;
		}).findFirst();
	}
}
