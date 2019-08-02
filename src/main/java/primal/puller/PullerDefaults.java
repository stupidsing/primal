package primal.puller;

public interface PullerDefaults<T, Opt, Pred, Sink, Source> extends Iterable<T> {

	public int count();

	public Opt opt();

	public boolean isAll(Pred pred);

	public boolean isAny(Pred pred);

	public void sink(Sink sink);

	public Source source();

}
