package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.Verbs.Equals;
import primal.primitive.FltPrim;
import primal.primitive.FltPrim.FltObjPair_;
import primal.primitive.FltPrim.FltObjSink;
import primal.primitive.FltPrim.FltObjSource;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.Flt_Flt;
import primal.primitive.adt.pair.FltObjPair;
import primal.primitive.fp.FltObjFunUtil;
import primal.puller.primitive.FltObjPuller;

/**
 * Map with generic object key and floatacter object value. Float.MIN_VALUE
 * is not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class ObjFltMap<K> {

	private static float empty = FltPrim.EMPTYVALUE;

	private int size;
	private Object[] ks;
	private float[] vs;

	public ObjFltMap() {
		this(8);
	}

	public ObjFltMap(int capacity) {
		allocate(capacity);
	}

	public float computeIfAbsent(K key, Obj_Flt<K> fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ObjFltMap) {
			@SuppressWarnings("unchecked")
			var other = (ObjFltMap<Object>) object;
			var b = size == other.size;
			for (var pair : FltObjFunUtil.iter(source_()))
				b &= other.get(pair.v) == pair.k;
			return b;
		} else
			return false;
	}

	public void forEach(FltObjSink<K> sink) {
		var pair = FltObjPair.<K> of((float) 0, null);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.k, pair.v);
	}

	public float get(K key) {
		var index = index(key);
		return Equals.ab(ks[index], key) ? vs[index] : empty;
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var pair : FltObjFunUtil.iter(source_())) {
			h = h * 31 + Float.hashCode(pair.k);
			h = h * 31 + Objects.hashCode(pair.v);
		}
		return h;
	}

	public void put(K key, float v) {
		size++;
		store(key, v);
		rehash();
	}

	public void update(K key, Flt_Flt fun) {
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

	public FltObjSource<K> source() {
		return source_();
	}

	@Override
	public String toString() {
		return FltObjPuller.of(source_()).map((v, k) -> k + ":" + v + ",").toJoinedString();
	}

	private void rehash() {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var ks0 = ks;
			var vs0 = vs;
			float v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					store(ks0[i], v_);
		}
	}

	private void store(Object key, float v1) {
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

	private FltObjSource<K> source_() {
		return new FltObjSource<>() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(FltObjPair_<K> pair) {
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
		vs = new float[capacity];
		Arrays.fill(vs, empty);
	}

	private K cast(Object o) {
		@SuppressWarnings("unchecked")
		var k = (K) o;
		return k;
	}

}
