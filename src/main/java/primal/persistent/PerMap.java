package primal.persistent;

import java.util.Iterator;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.adt.Opt;
import primal.adt.Pair;
import primal.streamlet.Streamlet;
import primal.streamlet.Streamlet2;

public class PerMap<K extends Comparable<K>, V> implements Iterable<Pair<K, V>> {

	private static PerMap<?, ?> empty = new PerMap<Integer, Object>();
	private PerTree<Pair<K, V>> tree = new PbTree<>(Pair.comparatorByFirst());

	public static <K extends Comparable<K>, V> PerMap<K, V> empty() {
		@SuppressWarnings("unchecked")
		var m = (PerMap<K, V>) empty;
		return m;
	}

	private PerMap() {
	}

	public PerMap(PerTree<Pair<K, V>> tree) {
		this.tree = tree;
	}

	@Override
	public Iterator<Pair<K, V>> iterator() {
		return streamlet().iterator();
	}

	public Streamlet<Pair<K, V>> streamlet() {
		return tree.streamlet();
	}

	public Streamlet2<K, V> streamlet2() {
		return tree.streamlet().map2(Pair::fst, Pair::snd);
	}

	public Opt<V> getOpt(K k) {
		return tree.findOpt(Pair.of(k, (V) null)).map(Pair::snd);
	}

	public V getOrFail(K k) {
		return tree.findOpt(Pair.of(k, (V) null)).map(Pair::snd).get();
	}

	public V getOrNull(K k) {
		return tree.findOpt(Pair.of(k, (V) null)).map(Pair::snd).or(null);
	}

	public PerMap<K, V> put(K k, V v) {
		return new PerMap<>(tree.add(Pair.of(k, v)));
	}

	public PerMap<K, V> replace(K k, V v) {
		return new PerMap<>(tree.replace(Pair.of(k, v)));
	}

	public PerMap<K, V> remove(K k) {
		return new PerMap<>(tree.remove(Pair.of(k, (V) null)));
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == PerMap.class && Equals.ab(streamlet(), ((PerMap<?, ?>) object).streamlet());
	}

	@Override
	public int hashCode() {
		return tree.streamlet().hashCode();
	}

	@Override
	public String toString() {
		return tree.streamlet().map(e -> e + ", ").toJoinedString("{ ", "", "}");
	}

}
