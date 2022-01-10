package io.ran.schema;

public interface IndexActionDelegate {
	String execute(TableAction tableAction, IndexAction ia);
}
