package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.ChrPrim;
import primal.primitive.LngPrim;

public class ChrLngPair {

	private static ChrLngPair none_ = ChrLngPair.of(ChrPrim.EMPTYVALUE, LngPrim.EMPTYVALUE);

	public char t0;
	public long t1;

	public interface MapFst {
		public char apply(char c);
	}

	public interface MapSnd {
		public long apply(long c);
	}

	public interface Map<T> {
		public T apply(char c, long f);
	}

	public static Iterate<ChrLngPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<ChrLngPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static ChrLngPair none() {
		return none_;
	}

	public static ChrLngPair of(char t0, long t1) {
		return new ChrLngPair(t0, t1);
	}

	protected ChrLngPair(char t0, long t1) {
		update(t0, t1);
	}

	public static Comparator<ChrLngPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Character.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Long.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<ChrLngPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Character.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static char fst(ChrLngPair pair) {
		return pair.t0;
	}

	public static long snd(ChrLngPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(char t0_, long t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == ChrLngPair.class) {
			var other = (ChrLngPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Character.hashCode(t0) + 31 * Long.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
