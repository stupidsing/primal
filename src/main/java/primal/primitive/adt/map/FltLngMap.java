package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.fp.Funs.Fun;
import primal.primitive.FltLngSink;
import primal.primitive.FltLngSource;
import primal.primitive.FltPrim.FltObjSource;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.Flt_Lng;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.Obj_Lng;
import primal.primitive.Lng_Lng;
import primal.primitive.adt.pair.FltLngPair;
import primal.primitive.adt.pair.FltObjPair;
import primal.primitive.puller.FltObjPuller;
import primal.puller.Puller;

/**
 * Map with primitive float key and primitive long value. Long.MIN_VALUE is
 * not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class FltLngMap {

	private static long empty = LngPrim.EMPTYVALUE;

	private int size;
	private float[] ks;
	private long[] vs;

	public static <T> Fun<Puller<T>, FltLngMap> collect(Obj_Flt<T> kf0, Obj_Lng<T> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return puller -> {
			var source = puller.source();
			var map = new FltLngMap();
			T t;
			while ((t = source.g()) != null)
				map.put(kf1.apply(t), vf1.apply(t));
			return map;
		};
	}

	public FltLngMap() {
		this(8);
	}

	public FltLngMap(int capacity) {
		allocate(capacity);
	}

	public long computeIfAbsent(float key, Flt_Lng fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FltLngMap) {
			var other = (FltLngMap) object;
			var b = size == other.size;
			for (var pair : puller())
				b &= other.get(pair.k) == pair.v;
			return b;
		} else
			return false;
	}

	public void forEach(FltLngSink sink) {
		var pair = FltLngPair.of((float) 0, (long) 0);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.t0, pair.t1);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var pair : puller()) {
			h = h * 31 + Float.hashCode(pair.k);
			h = h * 31 + Objects.hashCode(pair.v);
		}
		return h;
	}

	public long get(float key) {
		var index = index(key);
		return ks[index] == key ? vs[index] : empty;
	}

	public FltObjPuller<Long> puller() {
		return FltObjPuller.of(new FltObjSource<Long>() {
			private FltLngSource source0 = source_();
			private FltLngPair pair0 = FltLngPair.of((float) 0, (long) 0);

			public boolean source2(FltObjPair<Long> pair) {
				var b = source0.source2(pair0);
				pair.update(pair0.t0, pair0.t1);
				return b;
			}
		});
	}

	public void put(float key, long v) {
		size++;
		store(key, v);
		rehash();
	}

	@Override
	public String toString() {
		return puller().map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	public void update(float key, Lng_Lng fun) {
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
					var v_ = vs[index1];
					if (v_ != empty) {
						var k = ks[index1];
						vs[index1] = empty;
						rehash(index1);
						store(k, v_);
					}
				}
			}.rehash(index);
		rehash();
	}

	public int size() {
		return size;
	}

	public FltLngSource source() {
		return source_();
	}

	private void rehash() {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var ks0 = ks;
			var vs0 = vs;
			long v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					store(ks0[i], v_);
		}
	}

	private void store(float key, long v1) {
		var index = index(key);
		if (vs[index] == empty) {
			ks[index] = key;
			vs[index] = v1;
		} else
			fail("duplicate key " + key);
	}

	private int index(float key) {
		var mask = vs.length - 1;
		var index = Float.hashCode(key) & mask;
		while (vs[index] != empty && ks[index] != key)
			index = index + 1 & mask;
		return index;
	}

	private FltLngSource source_() {
		return new FltLngSource() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(FltLngPair pair) {
				while (index < capacity) {
					var k = ks[index];
					var v = vs[index++];
					if (v != empty) {
						pair.update(k, v);
						return true;
					}
				}
				return false;
			}
		};
	}

	private void allocate(int capacity) {
		ks = new float[capacity];
		vs = new long[capacity];
		Arrays.fill(vs, empty);
	}

}
