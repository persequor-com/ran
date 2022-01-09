package io.ran.schema;

public interface ColumnActionDelegate {
	String execute(TableActionType t, ColumnAction ca);
}
