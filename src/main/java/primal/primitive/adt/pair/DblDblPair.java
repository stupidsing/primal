package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.DblPrim;

public class DblDblPair {

	private static DblDblPair none_ = DblDblPair.of(DblPrim.EMPTYVALUE, DblPrim.EMPTYVALUE);

	public double t0;
	public double t1;

	public interface MapFst {
		public double apply(double c);
	}

	public interface MapSnd {
		public double apply(double c);
	}

	public interface Map<T> {
		public T apply(double c, double f);
	}

	public static Iterate<DblDblPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<DblDblPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static DblDblPair none() {
		return none_;
	}

	public static DblDblPair of(double t0, double t1) {
		return new DblDblPair(t0, t1);
	}

	protected DblDblPair(double t0, double t1) {
		update(t0, t1);
	}

	public static Comparator<DblDblPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Double.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Double.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<DblDblPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Double.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static double fst(DblDblPair pair) {
		return pair.t0;
	}

	public static double snd(DblDblPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(double t0_, double t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == DblDblPair.class) {
			var other = (DblDblPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(t0) + 31 * Double.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
