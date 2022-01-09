package io.ran.schema;

public interface IndexActionDelegate {
	String execute(TableActionType t, IndexAction ia);
}
