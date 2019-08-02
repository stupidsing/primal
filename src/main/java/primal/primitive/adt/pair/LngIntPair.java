package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.IntPrim;
import primal.primitive.LngPrim;

public class LngIntPair {

	private static LngIntPair none_ = LngIntPair.of(LngPrim.EMPTYVALUE, IntPrim.EMPTYVALUE);

	public long t0;
	public int t1;

	public interface MapFst {
		public long apply(long c);
	}

	public interface MapSnd {
		public int apply(int c);
	}

	public interface Map<T> {
		public T apply(long c, int f);
	}

	public static Iterate<LngIntPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<LngIntPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static LngIntPair none() {
		return none_;
	}

	public static LngIntPair of(long t0, int t1) {
		return new LngIntPair(t0, t1);
	}

	protected LngIntPair(long t0, int t1) {
		update(t0, t1);
	}

	public static Comparator<LngIntPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Long.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Integer.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<LngIntPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Long.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static long fst(LngIntPair pair) {
		return pair.t0;
	}

	public static int snd(LngIntPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(long t0_, int t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == LngIntPair.class) {
			var other = (LngIntPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(t0) + 31 * Integer.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
