package io.ran.schema;

public interface ColumnActionDelegate {
	String execute(TableAction table, ColumnAction ca);
}
