package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.Verbs.Equals;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.DblObjSink;
import primal.primitive.DblPrim.DblObjSource;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.primitive.Dbl_Dbl;
import primal.primitive.adt.pair.DblObjPair;
import primal.primitive.fp.DblObjFunUtil;
import primal.primitive.puller.DblObjPuller;

/**
 * Map with generic object key and doubleacter object value. Double.MIN_VALUE
 * is not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class ObjDblMap<K> {

	private static double empty = DblPrim.EMPTYVALUE;

	private int size;
	private Object[] ks;
	private double[] vs;

	public ObjDblMap() {
		this(8);
	}

	public ObjDblMap(int capacity) {
		allocate(capacity);
	}

	public double computeIfAbsent(K key, Obj_Dbl<K> fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ObjDblMap) {
			@SuppressWarnings("unchecked")
			var other = (ObjDblMap<Object>) object;
			var b = size == other.size;
			for (var pair : DblObjFunUtil.iter(source_()))
				b &= other.get(pair.v) == pair.k;
			return b;
		} else
			return false;
	}

	public void forEach(DblObjSink<K> sink) {
		var pair = DblObjPair.<K> of((double) 0, null);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.k, pair.v);
	}

	public double get(K key) {
		var index = index(key);
		return Equals.ab(ks[index], key) ? vs[index] : empty;
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

	public void put(K key, double v) {
		size++;
		store(key, v);
		rehash();
	}

	public void update(K key, Dbl_Dbl fun) {
		var mask = vs.length - 1;
		var index = index(key);
		var v0 = vs[index];
		var v1 = vs[index] = fun.apply(v0);
		ks[index] = key;
		size += (v1 != empty ? 1 : 0) - (v0 != empty ? 1 : 0);
		if (v1 == empty)
			new Object() {
				private void rehash(int index) {
					var index1 = (index + 1) & mask;
					var v = vs[index1];
					if (v != empty) {
						var k = ks[index1];
						vs[index1] = empty;
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

	public DblObjSource<K> source() {
		return source_();
	}

	@Override
	public String toString() {
		return DblObjPuller.of(source_()).map((v, k) -> k + ":" + v + ",").toJoinedString();
	}

	private void rehash() {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var ks0 = ks;
			var vs0 = vs;
			double v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					store(ks0[i], v_);
		}
	}

	private void store(Object key, double v1) {
		var index = index(key);
		if (vs[index] == empty) {
			ks[index] = key;
			vs[index] = v1;
		} else
			fail("duplicate key " + key);
	}

	private int index(Object key) {
		var mask = vs.length - 1;
		var index = key.hashCode() & mask;
		while (vs[index] != empty && !ks[index].equals(key))
			index = index + 1 & mask;
		return index;
	}

	private DblObjSource<K> source_() {
		return new DblObjSource<>() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(DblObjPair<K> pair) {
				while (index < capacity) {
					var k = ks[index];
					var v = vs[index++];
					if (v != empty) {
						pair.update(v, cast(k));
						return true;
					}
				}
				return false;
			}
		};
	}

	private void allocate(int capacity) {
		ks = new Object[capacity];
		vs = new double[capacity];
		Arrays.fill(vs, empty);
	}

	private K cast(Object o) {
		@SuppressWarnings("unchecked")
		var k = (K) o;
		return k;
	}

}
