/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-04-28
 */
package io.ran.testclasses;

import io.ran.PrimaryKey;
import io.ran.Relation;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {
	@PrimaryKey
	private String id;
	@Relation(via = GraphNodeLink.class, fields = "id", relationFields = "toId", autoSave = true)
	private List<GraphNode> previousNodes = new ArrayList<>();
	@Relation(via = GraphNodeLink.class, fields = "id", relationFields = "fromId", autoSave = true)
	private List<GraphNode> nextNodes = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<GraphNode> getPreviousNodes() {
		return previousNodes;
	}

	public void setPreviousNodes(List<GraphNode> previousNodes) {
		this.previousNodes = previousNodes;
	}

	public List<GraphNode> getNextNodes() {
		return nextNodes;
	}

	public void setNextNodes(List<GraphNode> nextNodes) {
		this.nextNodes = nextNodes;
	}
}
