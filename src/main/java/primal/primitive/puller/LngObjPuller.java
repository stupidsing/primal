package primal.primitive.puller;

import static primal.statics.Fail.fail;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import primal.NullableSyncQueue;
import primal.Verbs.Close;
import primal.Verbs.Compare;
import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.Verbs.New;
import primal.Verbs.Reverse;
import primal.Verbs.Sort;
import primal.Verbs.Take;
import primal.adt.Mutable;
import primal.adt.Pair;
import primal.fp.FunUtil;
import primal.fp.FunUtil2;
import primal.fp.Funs.Fun;
import primal.primitive.LngObj_Lng;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.LngObjPair_;
import primal.primitive.LngPrim.LngObjPredicate;
import primal.primitive.LngPrim.LngObjSink;
import primal.primitive.LngPrim.LngObjSource;
import primal.primitive.LngPrim.LngObj_Obj;
import primal.primitive.LngPrim.LngPred;
import primal.primitive.adt.pair.LngObjPair;
import primal.primitive.fp.LngObjFunUtil;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.puller.PullerDefaults;

public class LngObjPuller<V> implements PullerDefaults<LngObjPair<V>, LngObjPair<V>, LngObjPredicate<V>, LngObjSink<V>, LngObjSource<V>> {

	private static long empty = LngPrim.EMPTYVALUE;

	private LngObjSource<V> source;

	@SafeVarargs
	public static <V> LngObjPuller<V> concat(LngObjPuller<V>... outlets) {
		var sources = new ArrayList<LngObjSource<V>>();
		for (var outlet : outlets)
			sources.add(outlet.source);
		return of(LngObjFunUtil.concat(Take.from(sources)));
	}

	public static <V> LngObjPuller<V> empty() {
		return of(LngObjFunUtil.nullSource());
	}

	@SafeVarargs
	public static <V> LngObjPuller<V> of(LngObjPair<V>... kvs) {
		return of(new LngObjSource<>() {
			private int i;

			public boolean source2(LngObjPair_<V> pair) {
				var b = i < kvs.length;
				if (b) {
					var kv = kvs[i];
					pair.update(kv.k, kv.v);
				}
				return b;

			}
		});
	}

	public static <V> LngObjPuller<V> of(Iterable<LngObjPair<V>> col) {
		var iter = col.iterator();
		return of(new LngObjSource<>() {
			public boolean source2(LngObjPair_<V> pair) {
				var b = iter.hasNext();
				if (b) {
					var pair1 = iter.next();
					pair.update(pair1.k, pair1.v);
				}
				return b;
			}
		});
	}

	public static <V> LngObjPuller<V> of(LngObjSource<V> source) {
		return new LngObjPuller<>(source);
	}

	private LngObjPuller(LngObjSource<V> source) {
		this.source = source;
	}

	public Puller<LngObjPuller<V>> chunk(int n) {
		return Puller.of(FunUtil.map(LngObjPuller<V>::new, LngObjFunUtil.chunk(n, source)));
	}

	public LngObjPuller<V> closeAtEnd(Closeable c) {
		return of(pair -> {
			var b = pull(pair);
			if (!b)
				Close.quietly(c);
			return b;
		});
	}

	public <R> R collect(Fun<LngObjPuller<V>, R> fun) {
		return fun.apply(this);
	}

	public <O> Puller<O> concatMap(LngObj_Obj<V, Puller<O>> fun) {
		return Puller.of(FunUtil.concat(LngObjFunUtil.map((k, v) -> fun.apply(k, v).source(), source)));
	}

	public <K1, V1> Puller2<K1, V1> concatMap2(LngObj_Obj<V, Puller2<K1, V1>> fun) {
		return Puller2.of(FunUtil2.concat(LngObjFunUtil.map((k, v) -> fun.apply(k, v).source(), source)));
	}

	public <V1> LngObjPuller<V1> concatMapLngObj(LngObj_Obj<V, LngObjPuller<V1>> fun) {
		return of(LngObjFunUtil.concat(LngObjFunUtil.map((k, v) -> fun.apply(k, v).source, source)));
	}

	public <V1> LngObjPuller<V1> concatMapValue(Fun<V, Puller<V1>> fun) {
		return of(LngObjFunUtil.concat(LngObjFunUtil.map((k, v) -> {
			var source = fun.apply(v).source();
			return pair -> {
				var value1 = source.g();
				var b = value1 != null;
				if (b)
					pair.update(k, value1);
				return b;
			};
		}, source)));
	}

	public LngObjPuller<V> cons(long key, V value) {
		return of(LngObjFunUtil.cons(key, value, source));
	}

	public int count() {
		var pair = LngObjPair.of(empty, (V) null);
		var i = 0;
		while (pull(pair))
			i++;
		return i;
	}

	public LngObjPuller<V> distinct() {
		var set = new HashSet<>();
		return of(pair -> {
			boolean b;
			while ((b = pull(pair)) && !set.add(LngObjPair.of(pair.k, pair.v)))
				;
			return b;
		});
	}

	public LngObjPuller<V> drop(int n) {
		var pair = LngObjPair.of(empty, (V) null);
		var isAvailable = true;
		while (0 < n && (isAvailable &= pull(pair)))
			n--;
		return isAvailable ? this : empty();
	}

	public LngObjPuller<V> dropWhile(LngObjPredicate<V> fun) {
		return of(new LngObjSource<>() {
			private boolean b = false;

			public boolean source2(LngObjPair_<V> pair) {
				return pull(pair) && (b |= !fun.test(pair.k, pair.v));
			}
		});
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == LngObjPuller.class) {
			@SuppressWarnings("unchecked")
			var outlet = (LngObjPuller<V>) (LngObjPuller<?>) object;
			var source2 = outlet.source;
			boolean b, b0, b1;
			var pair0 = LngObjPair.of(empty, (V) null);
			var pair1 = LngObjPair.of(empty, (V) null);
			while ((b = (b0 = source2.source2(pair0)) == (b1 = source2.source2(pair1))) //
					&& b0 //
					&& b1 //
					&& (b = Equals.ab(pair0, pair1)))
				;
			return b;
		} else
			return false;
	}

	public LngObjPuller<V> filter(LngObjPredicate<V> fun) {
		return of(LngObjFunUtil.filter(fun, source));
	}

	public LngObjPuller<V> filterKey(LngPred fun) {
		return of(LngObjFunUtil.filterKey(fun, source));
	}

	public LngObjPuller<V> filterValue(Predicate<V> fun) {
		return of(LngObjFunUtil.filterValue(fun, source));
	}

	public LngObjPair<V> first() {
		var pair = LngObjPair.of(empty, (V) null);
		return pull(pair) ? pair : null;
	}

	public <O> Puller<O> flatMap(LngObj_Obj<V, Iterable<O>> fun) {
		return Puller.of(FunUtil.flatten(LngObjFunUtil.map(fun, source)));
	}

	@Override
	public int hashCode() {
		var pair = LngObjPair.of(empty, (V) null);
		var h = 7;
		while (pull(pair))
			h = h * 31 + pair.hashCode();
		return h;
	}

	public boolean isAll(LngObjPredicate<V> pred) {
		return LngObjFunUtil.isAll(pred, source);
	}

	public boolean isAny(LngObjPredicate<V> pred) {
		return LngObjFunUtil.isAny(pred, source);
	}

	@Override
	public Iterator<LngObjPair<V>> iterator() {
		return LngObjFunUtil.iterator(source);
	}

	public LngPuller keys() {
		return LngPuller.of(() -> {
			var pair = LngObjPair.of(empty, (V) null);
			return pull(pair) ? pair.k : empty;
		});
	}

	public LngObjPair<V> last() {
		var pair = LngObjPair.of(empty, (V) null);
		if (pull(pair))
			while (pull(pair))
				;
		else
			pair = null;
		return pair;
	}

	public <O> Puller<O> map(LngObj_Obj<V, O> fun0) {
		return map_(fun0);
	}

	public <K1, V1> Puller2<K1, V1> map2(LngObj_Obj<V, K1> kf, LngObj_Obj<V, V1> vf) {
		return Puller2.of(LngObjFunUtil.map2(kf, vf, source));
	}

	public <V1> LngObjPuller<V1> mapLngObj(LngObj_Lng<V> kf, LngObj_Obj<V, V1> vf) {
		return mapLngObj_(kf, vf);
	}

	public <V1> LngObjPuller<V1> mapValue(Fun<V, V1> fun) {
		return mapLngObj_((k, v) -> k, (k, v) -> fun.apply(v));
	}

	public LngObjPair<V> min(Comparator<LngObjPair<V>> comparator) {
		var pair = minOrNull(comparator);
		if (pair != null)
			return pair;
		else
			return fail("no result");
	}

	public LngObjPair<V> minOrNull(Comparator<LngObjPair<V>> comparator) {
		var pair = LngObjPair.of(empty, (V) null);
		var pair1 = LngObjPair.of(empty, (V) null);
		if (pull(pair)) {
			while (pull(pair1))
				if (0 < comparator.compare(pair, pair1))
					pair.update(pair1.k, pair1.v);
			return pair;
		} else
			return null;
	}

	public LngObjPuller<V> nonBlocking(Long k0, V v0) {
		var queue = new NullableSyncQueue<LngObjPair<V>>();

		new Thread(() -> {
			boolean b;
			do {
				var pair = LngObjPair.of(empty, (V) null);
				b = source.source2(pair);
				queue.offerQuietly(pair);
			} while (b);
		}).start();

		return new LngObjPuller<>(pair -> {
			var mutable = Mutable.<LngObjPair<V>> nil();
			var b = queue.poll(mutable);
			if (b) {
				var p = mutable.value();
				pair.update(p.k, p.v);
			} else
				pair.update(k0, v0);
			return b;
		});
	}

	public LngObjPair<V> opt() {
		var pair = LngObjPair.of(empty, (V) null);
		if (pull(pair))
			if (!pull(pair))
				return pair;
			else
				return fail("more than one result");
		else
			return LngObjPair.none();
	}

	public Puller<LngObjPair<V>> pairs() {
		return Puller.of(() -> {
			var pair = LngObjPair.of(empty, (V) null);
			return pull(pair) ? pair : null;
		});
	}

	public Pair<LngObjPuller<V>, LngObjPuller<V>> partition(LngObjPredicate<V> pred) {
		return Pair.of(filter(pred), filter((k, v) -> !pred.test(k, v)));
	}

	public LngObjPuller<V> reverse() {
		return of(Reverse.of(toList()));
	}

	public void sink(LngObjSink<V> sink0) {
		var sink1 = sink0.rethrow();
		var pair = LngObjPair.of(empty, (V) null);
		while (pull(pair))
			sink1.sink2(pair.k, pair.v);
	}

	public LngObjPuller<V> skip(int n) {
		var pair = LngObjPair.of(empty, (V) null);
		var end = false;
		for (var i = 0; !end && i < n; i++)
			end = pull(pair);
		return !end ? of(source) : empty();
	}

	public LngObjPuller<V> snoc(Long key, V value) {
		return of(LngObjFunUtil.snoc(key, value, source));
	}

	public LngObjPuller<V> sort(Comparator<LngObjPair<V>> comparator) {
		var list = new ArrayList<LngObjPair<V>>();
		LngObjPair<V> pair;
		while (pull(pair = LngObjPair.of(empty, null)))
			list.add(pair);
		return of(Sort.list(list, comparator));
	}

	public <O extends Comparable<? super O>> LngObjPuller<V> sortBy(LngObj_Obj<V, O> fun) {
		return sort((e0, e1) -> Compare.objects(fun.apply(e0.k, e0.v), fun.apply(e1.k, e1.v)));
	}

	public LngObjPuller<V> sortByKey(Comparator<Long> comparator) {
		return sort((e0, e1) -> comparator.compare(e0.k, e1.k));
	}

	public LngObjPuller<V> sortByValue(Comparator<V> comparator) {
		return sort((e0, e1) -> comparator.compare(e0.v, e1.v));
	}

	public LngObjSource<V> source() {
		return source;
	}

	public Puller<LngObjPuller<V>> split(LngObjPredicate<V> fun) {
		return Puller.of(FunUtil.map(LngObjPuller<V>::new, LngObjFunUtil.split(fun, source)));
	}

	public LngObjPuller<V> take(int n) {
		return of(new LngObjSource<>() {
			private int count = n;

			public boolean source2(LngObjPair_<V> pair) {
				return 0 < count-- ? pull(pair) : false;
			}
		});
	}

	public LngObjPuller<V> takeWhile(LngObjPredicate<V> fun) {
		return of(new LngObjSource<>() {
			private boolean b = true;

			public boolean source2(LngObjPair_<V> pair) {
				return pull(pair) && (b &= fun.test(pair.k, pair.v));
			}
		});
	}

	public LngObjPair<V>[] toArray() {
		var list = toList();
		@SuppressWarnings("unchecked")
		LngObjPair<V>[] array = New.array(LngObjPair.class, list.size());
		return list.toArray(array);
	}

	public List<LngObjPair<V>> toList() {
		var list = new ArrayList<LngObjPair<V>>();
		LngObjPair<V> pair;
		while (pull(pair = LngObjPair.of(empty, null)))
			list.add(pair);
		return list;
	}

	public Set<LngObjPair<V>> toSet() {
		var set = new HashSet<LngObjPair<V>>();
		LngObjPair<V> pair;
		while (pull(pair = LngObjPair.of(empty, null)))
			set.add(pair);
		return set;

	}

	public Puller<V> values() {
		return map_((k, v) -> v);
	}

	private <O> Puller<O> map_(LngObj_Obj<V, O> fun0) {
		return Puller.of(LngObjFunUtil.map(fun0, source));
	}

	private <V1> LngObjPuller<V1> mapLngObj_(LngObj_Lng<V> kf, LngObj_Obj<V, V1> vf) {
		return of(LngObjFunUtil.mapLngObj(kf, vf, source));
	}

	private boolean pull(LngObjPair_<V> pair) {
		return source.source2(pair);
	}

}
