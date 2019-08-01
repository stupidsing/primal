package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.DblPrim;
import primal.primitive.ChrPrim;

public class DblChrPair {

	private static DblChrPair none_ = DblChrPair.of(DblPrim.EMPTYVALUE, ChrPrim.EMPTYVALUE);

	public double t0;
	public char t1;

	public interface MapFst {
		public double apply(double c);
	}

	public interface MapSnd {
		public char apply(char c);
	}

	public interface Map<T> {
		public T apply(double c, char f);
	}

	public static Iterate<DblChrPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<DblChrPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static DblChrPair none() {
		return none_;
	}

	public static DblChrPair of(double t0, char t1) {
		return new DblChrPair(t0, t1);
	}

	protected DblChrPair(double t0, char t1) {
		update(t0, t1);
	}

	public static Comparator<DblChrPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Double.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Character.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<DblChrPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Double.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static double fst(DblChrPair pair) {
		return pair.t0;
	}

	public static char snd(DblChrPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(double t0_, char t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == DblChrPair.class) {
			var other = (DblChrPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(t0) + 31 * Character.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
