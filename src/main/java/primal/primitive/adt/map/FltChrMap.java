package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.fp.Funs.Fun;
import primal.primitive.ChrPrim;
import primal.primitive.ChrPrim.Obj_Chr;
import primal.primitive.Chr_Chr;
import primal.primitive.FltChrSink;
import primal.primitive.FltChrSource;
import primal.primitive.FltPrim.FltObjPair_;
import primal.primitive.FltPrim.FltObjSource;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.Flt_Chr;
import primal.primitive.adt.pair.FltChrPair;
import primal.puller.Puller;
import primal.puller.primitive.FltObjPuller;

/**
 * Map with primitive float key and primitive char value. Character.MIN_VALUE is
 * not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class FltChrMap {

	private static char empty = ChrPrim.EMPTYVALUE;

	private int size;
	private float[] ks;
	private char[] vs;

	public static <T> Fun<Puller<T>, FltChrMap> collect(Obj_Flt<T> kf0, Obj_Chr<T> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return puller -> {
			var source = puller.source();
			var map = new FltChrMap();
			T t;
			while ((t = source.g()) != null)
				map.put(kf1.apply(t), vf1.apply(t));
			return map;
		};
	}

	public FltChrMap() {
		this(8);
	}

	public FltChrMap(int capacity) {
		allocate(capacity);
	}

	public char computeIfAbsent(float key, Flt_Chr fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FltChrMap other) {
			var b = size == other.size;
			for (var pair : puller())
				b &= other.get(pair.k) == pair.v;
			return b;
		} else
			return false;
	}

	public void forEach(FltChrSink sink) {
		var pair = FltChrPair.of((float) 0, (char) 0);
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

	public char get(float key) {
		var index = index(key);
		return ks[index] == key ? vs[index] : empty;
	}

	public FltObjPuller<Character> puller() {
		return FltObjPuller.of(new FltObjSource<Character>() {
			private FltChrSource source0 = source_();
			private FltChrPair pair0 = FltChrPair.of((float) 0, (char) 0);

			public boolean source2(FltObjPair_<Character> pair) {
				var b = source0.source2(pair0);
				pair.update(pair0.t0, pair0.t1);
				return b;
			}
		});
	}

	public void put(float key, char v) {
		size++;
		store(key, v);
		rehash();
	}

	@Override
	public String toString() {
		return puller().map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	public void update(float key, Chr_Chr fun) {
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

	public FltChrSource source() {
		return source_();
	}

	private void rehash() {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var ks0 = ks;
			var vs0 = vs;
			char v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					store(ks0[i], v_);
		}
	}

	private void store(float key, char v1) {
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

	private FltChrSource source_() {
		return new FltChrSource() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(FltChrPair pair) {
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
		vs = new char[capacity];
		Arrays.fill(vs, empty);
	}

}
