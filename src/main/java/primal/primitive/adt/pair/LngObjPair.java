package primal.primitive.adt.pair;

import java.util.Comparator;
import java.util.Objects;

import primal.Verbs.Compare;
import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Iterate;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.LngObjPair_;

public class LngObjPair<V> extends LngObjPair_<V> {

	private static LngObjPair<?> none_ = LngObjPair.of(LngPrim.EMPTYVALUE, null);

	public interface MapFst {
		public long apply(long c);
	}

	public interface Map<X, Y> {
		public Y apply(long c, X x);
	}

	public static <V> Iterate<LngObjPair<V>> mapFst(MapFst fun) {
		return pair -> of(fun.apply(pair.k), pair.v);
	}

	public static <V0, V1> Fun<LngObjPair<V0>, LngObjPair<V1>> mapSnd(Fun<V0, V1> fun) {
		return pair -> of(pair.k, fun.apply(pair.v));
	}

	@SuppressWarnings("unchecked")
	public static <V> LngObjPair<V> none() {
		return (LngObjPair<V>) none_;
	}

	public static <V> LngObjPair<V> of(long k, V v) {
		return new LngObjPair<>(k, v);
	}

	protected LngObjPair(long k, V v) {
		super(k, v);
	}

	public static <V extends Comparable<? super V>> Comparator<LngObjPair<V>> comparator() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Long.compare(pair0.k, pair1.k) : c;
			c = c == 0 ? Compare.objects(pair0.v, pair1.v) : c;
			return c;
		};
	}

	public static <V> Comparator<LngObjPair<V>> comparatorByFirst() {
		return (pair0, pair1) -> {
			var c = Boolean.compare(pair0 != null, pair1 != null);
			c = c == 0 ? Long.compare(pair0.k, pair1.k) : c;
			return c;
		};
	}

	public static long fst(LngObjPair<?> pair) {
		return pair.k;
	}

	public static <T> T snd(LngObjPair<T> pair) {
		return pair != null ? pair.v : null;
	}

	public <O> O map(Map<V, O> fun) {
		return fun.apply(k, v);
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == LngObjPair.class) {
			var other = (LngObjPair<?>) object;
			return k == other.k && Equals.ab(v, other.v);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(k) + 31 * Objects.hashCode(v);
	}

	@Override
	public String toString() {
		return k + ":" + v;
	}

}
