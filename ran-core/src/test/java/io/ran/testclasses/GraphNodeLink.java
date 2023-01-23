package io.ran.testclasses;

import io.ran.Key;
import io.ran.PrimaryKey;
import io.ran.Relation;

public class GraphNodeLink {
	@PrimaryKey
	private String fromId;
	@PrimaryKey
	@Key(name = "to_idx")
	private String toId;
	@Relation(fields = "fromId", relationFields = "id", autoSave = true)
	private GraphNode from;
	@Relation(fields = "toId", relationFields = "id", autoSave = true)
	private GraphNode to;

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public GraphNode getFrom() {
		return from;
	}

	public void setFrom(GraphNode from) {
		this.from = from;
	}

	public GraphNode getTo() {
		return to;
	}

	public void setTo(GraphNode to) {
		this.to = to;
	}
}
