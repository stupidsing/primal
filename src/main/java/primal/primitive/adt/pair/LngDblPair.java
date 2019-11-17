package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.DblPrim;
import primal.primitive.LngPrim;

public class LngDblPair {

	private static LngDblPair none_ = LngDblPair.of(LngPrim.EMPTYVALUE, DblPrim.EMPTYVALUE);

	public long t0;
	public double t1;

	public interface MapFst {
		public long apply(long c);
	}

	public interface MapSnd {
		public double apply(double c);
	}

	public interface Map<T> {
		public T apply(long c, double f);
	}

	public static Iterate<LngDblPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<LngDblPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static LngDblPair none() {
		return none_;
	}

	public static LngDblPair of(long t0, double t1) {
		return new LngDblPair(t0, t1);
	}

	protected LngDblPair(long t0, double t1) {
		update(t0, t1);
	}

	public static Comparator<LngDblPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Long.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Double.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<LngDblPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Long.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static long fst(LngDblPair pair) {
		return pair.t0;
	}

	public static double snd(LngDblPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(long t0_, double t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == LngDblPair.class) {
			var other = (LngDblPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(t0) + 31 * Double.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
