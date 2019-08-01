package primal.primitive.adt.pair;

import java.util.Comparator;

import primal.Verbs.Get;
import primal.fp.Funs.Iterate;
import primal.primitive.ChrPrim;
import primal.primitive.FltPrim;

public class ChrFltPair {

	private static ChrFltPair none_ = ChrFltPair.of(ChrPrim.EMPTYVALUE, FltPrim.EMPTYVALUE);

	public char t0;
	public float t1;

	public interface MapFst {
		public char apply(char c);
	}

	public interface MapSnd {
		public float apply(float c);
	}

	public interface Map<T> {
		public T apply(char c, float f);
	}

	public static Iterate<ChrFltPair> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.t0), pair.t1);
	}

	public static Iterate<ChrFltPair> mapSnd(MapSnd fun) {
		return pair -> of(pair.t0, fun.apply(pair.t1));
	}

	public static ChrFltPair none() {
		return none_;
	}

	public static ChrFltPair of(char t0, float t1) {
		return new ChrFltPair(t0, t1);
	}

	protected ChrFltPair(char t0, float t1) {
		update(t0, t1);
	}

	public static Comparator<ChrFltPair> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Character.compare(pair0.t0, pair1.t0) : c;
			c = c == 0 ? Float.compare(pair0.t1, pair1.t1) : c;
			return c;
		};
	}

	public static Comparator<ChrFltPair> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Character.compare(pair0.t0, pair1.t0) : c;
			return c;
		};
	}

	public static char fst(ChrFltPair pair) {
		return pair.t0;
	}

	public static float snd(ChrFltPair pair) {
		return pair.t1;
	}

	public <O> O map(Map<O> fun) {
		return fun.apply(t0, t1);
	}

	public void update(char t0_, float t1_) {
		t0 = t0_;
		t1 = t1_;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == ChrFltPair.class) {
			var other = (ChrFltPair) object;
			return t0 == other.t0 && t1 == other.t1;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Character.hashCode(t0) + 31 * Float.hashCode(t1);
	}

	@Override
	public String toString() {
		return t0 + ":" + t1;
	}

}
