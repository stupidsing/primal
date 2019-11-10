package primal.persistent;

import primal.adt.Opt;
import primal.streamlet.Streamlet;

public interface PerTree<T> {

	public Streamlet<T> streamlet();

	public Opt<T> findOpt(T t);

	public PerTree<T> add(T t);

	public PerTree<T> replace(T t);

	public PerTree<T> remove(T t);

}
