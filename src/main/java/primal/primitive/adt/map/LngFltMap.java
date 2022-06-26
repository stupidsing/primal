package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.fp.Funs.Fun;
import primal.primitive.FltPrim;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.Flt_Flt;
import primal.primitive.LngFltSink;
import primal.primitive.LngFltSource;
import primal.primitive.LngPrim.LngObjPair_;
import primal.primitive.LngPrim.LngObjSource;
import primal.primitive.LngPrim.Obj_Lng;
import primal.primitive.Lng_Flt;
import primal.primitive.adt.pair.LngFltPair;
import primal.puller.Puller;
import primal.puller.primitive.LngObjPuller;

/**
 * Map with primitive long key and primitive float value. Float.MIN_VALUE is
 * not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class LngFltMap {

	private static float empty = FltPrim.EMPTYVALUE;

	private int size;
	private long[] ks;
	private float[] vs;

	public static <T> Fun<Puller<T>, LngFltMap> collect(Obj_Lng<T> kf0, Obj_Flt<T> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return puller -> {
			var source = puller.source();
			var map = new LngFltMap();
			T t;
			while ((t = source.g()) != null)
				map.put(kf1.apply(t), vf1.apply(t));
			return map;
		};
	}

	public LngFltMap() {
		this(8);
	}

	public LngFltMap(int capacity) {
		allocate(capacity);
	}

	public float computeIfAbsent(long key, Lng_Flt fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof LngFltMap other) {
			var b = size == other.size;
			for (var pair : puller())
				b &= other.get(pair.k) == pair.v;
			return b;
		} else
			return false;
	}

	public void forEach(LngFltSink sink) {
		var pair = LngFltPair.of((long) 0, (float) 0);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.t0, pair.t1);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var pair : puller()) {
			h = h * 31 + Long.hashCode(pair.k);
			h = h * 31 + Objects.hashCode(pair.v);
		}
		return h;
	}

	public float get(long key) {
		var index = index(key);
		return ks[index] == key ? vs[index] : empty;
	}

	public LngObjPuller<Float> puller() {
		return LngObjPuller.of(new LngObjSource<Float>() {
			private LngFltSource source0 = source_();
			private LngFltPair pair0 = LngFltPair.of((long) 0, (float) 0);

			public boolean source2(LngObjPair_<Float> pair) {
				var b = source0.source2(pair0);
				pair.update(pair0.t0, pair0.t1);
				return b;
			}
		});
	}

	public void put(long key, float v) {
		size++;
		store(key, v);
		rehash();
	}

	@Override
	public String toString() {
		return puller().map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	public void update(long key, Flt_Flt fun) {
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

	public LngFltSource source() {
		return source_();
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

	private void store(long key, float v1) {
		var index = index(key);
		if (vs[index] == empty) {
			ks[index] = key;
			vs[index] = v1;
		} else
			fail("duplicate key " + key);
	}

	private int index(long key) {
		var mask = vs.length - 1;
		var index = Long.hashCode(key) & mask;
		while (vs[index] != empty && ks[index] != key)
			index = index + 1 & mask;
		return index;
	}

	private LngFltSource source_() {
		return new LngFltSource() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(LngFltPair pair) {
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
		ks = new long[capacity];
		vs = new float[capacity];
		Arrays.fill(vs, empty);
	}

}
