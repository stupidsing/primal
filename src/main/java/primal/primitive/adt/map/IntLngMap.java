package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.fp.Funs.Fun;
import primal.primitive.IntLngSink;
import primal.primitive.IntLngSource;
import primal.primitive.IntPrim.IntObjSource;
import primal.primitive.IntPrim.Obj_Int;
import primal.primitive.Int_Lng;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.Obj_Lng;
import primal.primitive.Lng_Lng;
import primal.primitive.adt.pair.IntLngPair;
import primal.primitive.adt.pair.IntObjPair;
import primal.primitive.puller.IntObjPuller;
import primal.puller.Puller;

/**
 * Map with primitive int key and primitive long value. Long.MIN_VALUE is
 * not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class IntLngMap {

	private static long empty = LngPrim.EMPTYVALUE;

	private int size;
	private int[] ks;
	private long[] vs;

	public static <T> Fun<Puller<T>, IntLngMap> collect(Obj_Int<T> kf0, Obj_Lng<T> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return puller -> {
			var source = puller.source();
			var map = new IntLngMap();
			T t;
			while ((t = source.g()) != null)
				map.put(kf1.apply(t), vf1.apply(t));
			return map;
		};
	}

	public IntLngMap() {
		this(8);
	}

	public IntLngMap(int capacity) {
		allocate(capacity);
	}

	public long computeIfAbsent(int key, Int_Lng fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof IntLngMap) {
			var other = (IntLngMap) object;
			var b = size == other.size;
			for (var pair : puller())
				b &= other.get(pair.k) == pair.v;
			return b;
		} else
			return false;
	}

	public void forEach(IntLngSink sink) {
		var pair = IntLngPair.of((int) 0, (long) 0);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.t0, pair.t1);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var pair : puller()) {
			h = h * 31 + Integer.hashCode(pair.k);
			h = h * 31 + Objects.hashCode(pair.v);
		}
		return h;
	}

	public long get(int key) {
		var index = index(key);
		return ks[index] == key ? vs[index] : empty;
	}

	public IntObjPuller<Long> puller() {
		return IntObjPuller.of(new IntObjSource<Long>() {
			private IntLngSource source0 = source_();
			private IntLngPair pair0 = IntLngPair.of((int) 0, (long) 0);

			public boolean source2(IntObjPair<Long> pair) {
				var b = source0.source2(pair0);
				pair.update(pair0.t0, pair0.t1);
				return b;
			}
		});
	}

	public void put(int key, long v) {
		size++;
		store(key, v);
		rehash();
	}

	@Override
	public String toString() {
		return puller().map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	public void update(int key, Lng_Lng fun) {
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

	public IntLngSource source() {
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

	private void store(int key, long v1) {
		var index = index(key);
		if (vs[index] == empty) {
			ks[index] = key;
			vs[index] = v1;
		} else
			fail("duplicate key " + key);
	}

	private int index(int key) {
		var mask = vs.length - 1;
		var index = Integer.hashCode(key) & mask;
		while (vs[index] != empty && ks[index] != key)
			index = index + 1 & mask;
		return index;
	}

	private IntLngSource source_() {
		return new IntLngSource() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(IntLngPair pair) {
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
		ks = new int[capacity];
		vs = new long[capacity];
		Arrays.fill(vs, empty);
	}

}
