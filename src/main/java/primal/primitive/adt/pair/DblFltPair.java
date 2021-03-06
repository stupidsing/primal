package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.DblPrim;
import primal.primitive.FltPrim;

public class DblFltPair {

	private static DblFltPair none_ = DblFltPair.of(DblPrim.EMPTYVALUE, FltPrim.EMPTYVALUE);

	public double t0;
	public float t1;

	public interface MapFst {
		public double apply(double c);
	}

	public interface MapSnd {
		public float apply(float c);
	}

	public interface Map<T> {
		public T apply(double c, float f);
	}

	public static Iterate<DblFltPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<DblFltPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static DblFltPair none() {
		return none_;
	}

	public static DblFltPair of(double t0, float t1) {
		return new DblFltPair(t0, t1);
	}

	protected DblFltPair(double t0, float t1) {
		update(t0, t1);
	}

	public static Comparator<DblFltPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Double.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Float.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<DblFltPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Double.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static double fst(DblFltPair pair) {
		return pair.t0;
	}

	public static float snd(DblFltPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(double t0_, float t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == DblFltPair.class) {
			var other = (DblFltPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(t0) + 31 * Float.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
