package io.ran.schema;

import java.util.Collection;

public interface SchemaExecutor {
	void execute(Collection<TableAction> values);
}
