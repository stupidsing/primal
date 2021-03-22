package primal.primitive.adt.map;

import static primal.statics.Fail.fail;

import java.util.Arrays;
import java.util.Objects;

import primal.fp.Funs.Fun;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.primitive.Dbl_Dbl;
import primal.primitive.FltDblSink;
import primal.primitive.FltDblSource;
import primal.primitive.FltPrim.FltObjPair_;
import primal.primitive.FltPrim.FltObjSource;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.Flt_Dbl;
import primal.primitive.adt.pair.FltDblPair;
import primal.primitive.puller.FltObjPuller;
import primal.puller.Puller;

/**
 * Map with primitive float key and primitive double value. Double.MIN_VALUE is
 * not allowed in values. Not thread-safe.
 *
 * @author ywsing
 */
public class FltDblMap {

	private static double empty = DblPrim.EMPTYVALUE;

	private int size;
	private float[] ks;
	private double[] vs;

	public static <T> Fun<Puller<T>, FltDblMap> collect(Obj_Flt<T> kf0, Obj_Dbl<T> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return puller -> {
			var source = puller.source();
			var map = new FltDblMap();
			T t;
			while ((t = source.g()) != null)
				map.put(kf1.apply(t), vf1.apply(t));
			return map;
		};
	}

	public FltDblMap() {
		this(8);
	}

	public FltDblMap(int capacity) {
		allocate(capacity);
	}

	public double computeIfAbsent(float key, Flt_Dbl fun) {
		var v = get(key);
		if (v == empty)
			put(key, v = fun.apply(key));
		return v;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FltDblMap other) {
			var b = size == other.size;
			for (var pair : puller())
				b &= other.get(pair.k) == pair.v;
			return b;
		} else
			return false;
	}

	public void forEach(FltDblSink sink) {
		var pair = FltDblPair.of((float) 0, (double) 0);
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

	public double get(float key) {
		var index = index(key);
		return ks[index] == key ? vs[index] : empty;
	}

	public FltObjPuller<Double> puller() {
		return FltObjPuller.of(new FltObjSource<Double>() {
			private FltDblSource source0 = source_();
			private FltDblPair pair0 = FltDblPair.of((float) 0, (double) 0);

			public boolean source2(FltObjPair_<Double> pair) {
				var b = source0.source2(pair0);
				pair.update(pair0.t0, pair0.t1);
				return b;
			}
		});
	}

	public void put(float key, double v) {
		size++;
		store(key, v);
		rehash();
	}

	@Override
	public String toString() {
		return puller().map((k, v) -> k + ":" + v + ",").toJoinedString();
	}

	public void update(float key, Dbl_Dbl fun) {
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

	public FltDblSource source() {
		return source_();
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

	private void store(float key, double v1) {
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

	private FltDblSource source_() {
		return new FltDblSource() {
			private int capacity = vs.length;
			private int index = 0;

			public boolean source2(FltDblPair pair) {
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
		vs = new double[capacity];
		Arrays.fill(vs, empty);
	}

}
