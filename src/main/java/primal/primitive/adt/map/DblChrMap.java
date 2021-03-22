package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.fp.Funs.Fun;
import primal.primitive.ChrPrim;
import primal.primitive.ChrPrim.Obj_Chr;
import primal.primitive.Chr_Chr;
import primal.primitive.DblChrSink;
import primal.primitive.DblChrSource;
import primal.primitive.DblPrim.DblObjPair_;
import primal.primitive.DblPrim.DblObjSource;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.primitive.Dbl_Chr;
import primal.primitive.adt.pair.DblChrPair;
import primal.primitive.puller.DblObjPuller;
import primal.puller.Puller;

/**
 * Map with primitive double key and primitive char value. Character.MIN_VALUE is
 * not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class DblChrMap {

	private static char empty = ChrPrim.EMPTYVALUE;

	private int size;
	private double[] ks;
	private char[] vs;

	public static <T> Fun<Puller<T>, DblChrMap> collect(Obj_Dbl<T> kf0, Obj_Chr<T> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return puller -> {
			var source = puller.source();
			var map = new DblChrMap();
			T t;
			while ((t = source.g()) != null)
				map.put(kf1.apply(t), vf1.apply(t));
			return map;
		};
	}

	public DblChrMap() {
		this(8);
	}

	public DblChrMap(int capacity) {
		allocate(capacity);
	}

	public char computeIfAbsent(double key, Dbl_Chr fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof DblChrMap other) {
			var b = size == other.size;
			for (var pair : puller())
				b &= other.get(pair.k) == pair.v;
			return b;
		} else
			return false;
	}

	public void forEach(DblChrSink sink) {
		var pair = DblChrPair.of((double) 0, (char) 0);
		var source = source_();
		while (source.source2(pair))
			sink.sink2(pair.t0, pair.t1);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var pair : puller()) {
			h = h * 31 + Double.hashCode(pair.k);
			h = h * 31 + Objects.hashCode(pair.v);
		}
		return h;
	}

	public char get(double key) {
		var index = index(key);
		return ks[index] == key ? vs[index] : empty;
	}

	public DblObjPuller<Character> puller() {
		return DblObjPuller.of(new DblObjSource<Character>() {
			private DblChrSource source0 = source_();
			private DblChrPair pair0 = DblChrPair.of((double) 0, (char) 0);

			public boolean source2(DblObjPair_<Character> pair) {
				var b = source0.source2(pair0);
				pair.update(pair0.t0, pair0.t1);
				return b;
			}
		});
	}

	public void put(double key, char v) {
		size++;
		store(key, v);
		rehash();
	}

	@Override
	public String toString() {
		return puller().map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	public void update(double key, Chr_Chr fun) {
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

	public DblChrSource source() {
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

	private void store(double key, char v1) {
		var index = index(key);
		if (vs[index] == empty) {
			ks[index] = key;
			vs[index] = v1;
		} else
			fail("duplicate key " + key);
	}

	private int index(double key) {
		var mask = vs.length - 1;
		var index = Double.hashCode(key) & mask;
		while (vs[index] != empty && ks[index] != key)
			index = index + 1 & mask;
		return index;
	}

	private DblChrSource source_() {
		return new DblChrSource() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(DblChrPair pair) {
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
		ks = new double[capacity];
		vs = new char[capacity];
		Arrays.fill(vs, empty);
	}

}
