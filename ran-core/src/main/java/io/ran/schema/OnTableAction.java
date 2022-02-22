package io.ran.schema;

public interface OnTableAction {
	String apply(TableAction tableAction, OnTableAction ca);
}
