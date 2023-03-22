/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import javax.inject.Inject;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestDoubleResolver<T> implements DbResolver<T> {
	private GenericFactory genericFactory;
	private MappingHelper mappingHelper;
	private TestDoubleDb store;

	@Inject
	public TestDoubleResolver(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
		this.genericFactory = genericFactory;
		this.mappingHelper = mappingHelper;
		this.store = store;
	}

	protected abstract <X, XQ extends CrudRepository.InlineQuery<X, XQ>> CrudRepository.InlineQuery<X, XQ> getQuery();

	private <FROM, TO> Stream<TO> getStream(RelationDescriber relationDescriber, FROM from) {
		CrudRepository.InlineQuery<TO, ?> q = getQuery();
		for (int i = 0; i < relationDescriber.getFromKeys().size(); i++) {
			Property fk = relationDescriber.getFromKeys().get(i).getProperty();
			Property tk = relationDescriber.getToKeys().get(i).getProperty();
			q.eq(tk.value(mappingHelper.getValue(from, fk)));
		}
		return q.execute();
	}

	@Override
	public <FROM, TO> TO get(RelationDescriber relationDescriber, FROM from) {
		return (TO) getStream(relationDescriber, from).findFirst().orElse(null);
	}

	@Override
	public <FROM, TO> Collection<TO> getCollection(RelationDescriber relationDescriber, FROM from) {
		return (Collection<TO>) getStream(relationDescriber, from).collect(Collectors.toList());
	}
}
