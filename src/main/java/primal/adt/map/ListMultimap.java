package primal.adt.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import primal.fp.Funs.Fun;
import primal.primitive.fp.AsInt;
import primal.puller.Puller2;

public class ListMultimap<K, V> {

	public final Map<K, List<V>> map;

	public ListMultimap() {
		this(new HashMap<>());
	}

	public ListMultimap(Map<K, List<V>> map) {
		this.map = map;
	}

	public <T> T apply(Fun<ListMultimap<K, V>, T> fun) {
		return fun.apply(this);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public List<V> get(K k) {
		var list = map.get(k);
		return list != null ? list : List.of();
	}

	public List<V> getMutable(K k) {
		return get_(k);
	}

	public boolean isEmpty() {
		return Puller2.of(map).values().isAny(list -> !list.isEmpty());
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public void put(K k, V v) {
		get_(k).add(v);
	}

	public void remove(K k, V v) {
		get_(k).remove(v);
	}

	public int size() {
		return AsInt.sum(List<V>::size).apply(Puller2.of(map).values());
	}

	@Override
	public String toString() {
		return Puller2.of(map).map((k, v) -> k + "=" + v + ", ").toJoinedString("{", "", "}");
	}

	private List<V> get_(K k) {
		return map.computeIfAbsent(k, k_ -> new ArrayList<>());
	}

}
