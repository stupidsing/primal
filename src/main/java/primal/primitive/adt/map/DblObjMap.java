package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Objects;

import primal.fp.Funs.Iterate;
import primal.primitive.DblPrim.DblObjPair_;
import primal.primitive.DblPrim.DblObjSink;
import primal.primitive.DblPrim.DblObjSource;
import primal.primitive.DblPrim.Dbl_Obj;
import primal.primitive.adt.pair.DblObjPair;
import primal.primitive.fp.DblObjFunUtil;
import primal.puller.primitive.DblObjPuller;

/**
 * Map with primitive integer key and a generic object value. Null values are
 * not allowed. Not thread-safe.
 * 
 * @author ywsing
 */
public class DblObjMap<V> {

	private static Object EMPTYVALUE = null;

	private int size;
	private double[] ks;
	private Object[] vs;

	public DblObjMap() {
		this(8);
	}

	public DblObjMap(int capacity) {
		allocate(capacity);
	}

	public V computeIfAbsent(double key, Dbl_Obj<V> fun) {
		var v = get(key);
		if (v == EMPTYVALUE)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof DblObjMap<?> other) {
			var b = size == other.size;
			for (var pair : DblObjFunUtil.iter(source_()))
				b &= other.get(pair.k).equals(pair.v);
			return b;
		} else
			return false;
	}

	public void forEach(DblObjSink<V> sink) {
		var pair = DblObjPair.<V> of((double) 0, null);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.k, pair.v);
	}

	public V get(double key) {
		var index = index(key);
		@SuppressWarnings("unchecked")
		var v = ks[index] == key ? cast(vs[index]) : (V) EMPTYVALUE;
		return v;
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var pair : DblObjFunUtil.iter(source_())) {
			h = h * 31 + Double.hashCode(pair.k);
			h = h * 31 + Objects.hashCode(pair.v);
		}
		return h;
	}

	public void put(double key, V v1) {
		size++;
		store(key, v1);
		rehash();
	}

	public void update(double key, Iterate<V> fun) {
		var mask = vs.length - 1;
		var index = index(key);
		var v0 = cast(vs[index]);
		var v1 = fun.apply(v0);
		ks[index] = key;
		size += ((vs[index] = v1) != EMPTYVALUE ? 1 : 0) - (v0 != EMPTYVALUE ? 1 : 0);
		if (v1 == EMPTYVALUE)
			new Object() {
				private void rehash(int index) {
					var index1 = (index + 1) & mask;
					var v = vs[index1];
					if (v != EMPTYVALUE) {
						var k = ks[index1];
						vs[index1] = EMPTYVALUE;
						rehash(index1);
						store(k, v);
					}
				}
			}.rehash(index);
		rehash();
	}

	public int size() {
		return size;
	}

	public DblObjSource<V> source() {
		return source_();
	}

	@Override
	public String toString() {
		return DblObjPuller.of(source_()).map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	private void rehash() {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var ks0 = ks;
			var vs0 = vs;
			Object o;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((o = vs0[i]) != EMPTYVALUE)
					store(ks0[i], o);
		}
	}

	private void store(double key, Object v1) {
		var index = index(key);
		if (vs[index] == EMPTYVALUE) {
			ks[index] = key;
			vs[index] = v1;
		} else
			fail("duplicate key " + key);
	}

	private int index(double key) {
		var mask = vs.length - 1;
		var index = Double.hashCode(key) & mask;
		while (vs[index] != EMPTYVALUE && ks[index] != key)
			index = index + 1 & mask;
		return index;
	}

	private DblObjSource<V> source_() {
		return new DblObjSource<>() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(DblObjPair_<V> pair) {
				while (index < capacity) {
					var k = ks[index];
					var v = vs[index++];
					if (v != EMPTYVALUE) {
						pair.update(k, cast(v));
						return true;
					}
				}
				return false;
			}
		};
	}

	private void allocate(int capacity) {
		ks = new double[capacity];
		vs = new Object[capacity];
	}

	private V cast(Object o) {
		@SuppressWarnings("unchecked")
		var v = (V) o;
		return v;
	}

}
