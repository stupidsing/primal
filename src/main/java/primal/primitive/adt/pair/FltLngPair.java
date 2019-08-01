package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.FltPrim;
import primal.primitive.LngPrim;

public class FltLngPair {

	private static FltLngPair none_ = FltLngPair.of(FltPrim.EMPTYVALUE, LngPrim.EMPTYVALUE);

	public float t0;
	public long t1;

	public interface MapFst {
		public float apply(float c);
	}

	public interface MapSnd {
		public long apply(long c);
	}

	public interface Map<T> {
		public T apply(float c, long f);
	}

	public static Iterate<FltLngPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<FltLngPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static FltLngPair none() {
		return none_;
	}

	public static FltLngPair of(float t0, long t1) {
		return new FltLngPair(t0, t1);
	}

	protected FltLngPair(float t0, long t1) {
		update(t0, t1);
	}

	public static Comparator<FltLngPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Float.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Long.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<FltLngPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Float.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static float fst(FltLngPair pair) {
		return pair.t0;
	}

	public static long snd(FltLngPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(float t0_, long t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == FltLngPair.class) {
			var other = (FltLngPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(t0) + 31 * Long.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
