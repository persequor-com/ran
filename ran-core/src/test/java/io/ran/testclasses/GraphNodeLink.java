/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
