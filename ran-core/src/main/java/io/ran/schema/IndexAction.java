/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran.schema;

import io.ran.token.ColumnToken;
import io.ran.token.IndexToken;

import java.util.HashMap;
import java.util.function.BiFunction;

public class IndexAction implements OnTableAction {
	private IndexToken name;
	private boolean isPrimary = false;
	private FormattingTokenList<ColumnToken> fields = new FormattingTokenList<ColumnToken>();
	private BiFunction<TableAction, IndexAction, String> action;
	private HashMap<String, Object> properties = new HashMap<>();

	public IndexAction(IndexToken name, FormattingTokenList<ColumnToken> fields, boolean isPrimary, BiFunction<TableAction, IndexAction, String> action) {
		this.name = name;
		this.fields = fields;
		this.isPrimary = isPrimary;
		this.action = action;
	}

	public IndexToken getName() {
		return name;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public FormattingTokenList<ColumnToken> getFields() {
		return fields;
	}

	public BiFunction<TableAction, IndexAction, String> getAction() {
		return action;
	}

	public void addProperty(String name, Object value) {
		properties.put(name, value);
	}

	public Object getProperty(String name) {
		return properties.getOrDefault(name, new Object());
	}

	@Override
	public String apply(TableAction tableAction, OnTableAction ca) {
		return action.apply(tableAction, (IndexAction) ca);
	}
}
