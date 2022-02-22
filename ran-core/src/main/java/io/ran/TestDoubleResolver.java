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
		CrudRepository.InlineQuery<TO, ?> q =  getQuery();
		for(int i=0;i<relationDescriber.getFromKeys().size();i++) {
			Property fk = relationDescriber.getFromKeys().get(i).getProperty();
			Property tk = relationDescriber.getToKeys().get(i).getProperty();
			q.eq(tk.value(mappingHelper.getValue(from,fk)));
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
