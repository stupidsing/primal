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
import primal.primitive.DblObj_Dbl;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.DblObjPair_;
import primal.primitive.DblPrim.DblObjPredicate;
import primal.primitive.DblPrim.DblObjSink;
import primal.primitive.DblPrim.DblObjSource;
import primal.primitive.DblPrim.DblObj_Obj;
import primal.primitive.DblPrim.DblPred;
import primal.primitive.adt.pair.DblObjPair;
import primal.primitive.fp.DblObjFunUtil;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.puller.PullerDefaults;

public class DblObjPuller<V> implements PullerDefaults<DblObjPair<V>, DblObjPair<V>, DblObjPredicate<V>, DblObjSink<V>, DblObjSource<V>> {

	private static double empty = DblPrim.EMPTYVALUE;

	private DblObjSource<V> source;

	@SafeVarargs
	public static <V> DblObjPuller<V> concat(DblObjPuller<V>... outlets) {
		var sources = new ArrayList<DblObjSource<V>>();
		for (var outlet : outlets)
			sources.add(outlet.source);
		return of(DblObjFunUtil.concat(Take.from(sources)));
	}

	public static <V> DblObjPuller<V> empty() {
		return of(DblObjFunUtil.nullSource());
	}

	@SafeVarargs
	public static <V> DblObjPuller<V> of(DblObjPair<V>... kvs) {
		return of(new DblObjSource<>() {
			private int i;

			public boolean source2(DblObjPair_<V> pair) {
				var b = i < kvs.length;
				if (b) {
					var kv = kvs[i];
					pair.update(kv.k, kv.v);
				}
				return b;

			}
		});
	}

	public static <V> DblObjPuller<V> of(Iterable<DblObjPair<V>> col) {
		var iter = col.iterator();
		return of(new DblObjSource<>() {
			public boolean source2(DblObjPair_<V> pair) {
				var b = iter.hasNext();
				if (b) {
					var pair1 = iter.next();
					pair.update(pair1.k, pair1.v);
				}
				return b;
			}
		});
	}

	public static <V> DblObjPuller<V> of(DblObjSource<V> source) {
		return new DblObjPuller<>(source);
	}

	private DblObjPuller(DblObjSource<V> source) {
		this.source = source;
	}

	public Puller<DblObjPuller<V>> chunk(int n) {
		return Puller.of(FunUtil.map(DblObjPuller<V>::new, DblObjFunUtil.chunk(n, source)));
	}

	public DblObjPuller<V> closeAtEnd(Closeable c) {
		return of(pair -> {
			var b = pull(pair);
			if (!b)
				Close.quietly(c);
			return b;
		});
	}

	public <R> R collect(Fun<DblObjPuller<V>, R> fun) {
		return fun.apply(this);
	}

	public <O> Puller<O> concatMap(DblObj_Obj<V, Puller<O>> fun) {
		return Puller.of(FunUtil.concat(DblObjFunUtil.map((k, v) -> fun.apply(k, v).source(), source)));
	}

	public <K1, V1> Puller2<K1, V1> concatMap2(DblObj_Obj<V, Puller2<K1, V1>> fun) {
		return Puller2.of(FunUtil2.concat(DblObjFunUtil.map((k, v) -> fun.apply(k, v).source(), source)));
	}

	public <V1> DblObjPuller<V1> concatMapDblObj(DblObj_Obj<V, DblObjPuller<V1>> fun) {
		return of(DblObjFunUtil.concat(DblObjFunUtil.map((k, v) -> fun.apply(k, v).source, source)));
	}

	public <V1> DblObjPuller<V1> concatMapValue(Fun<V, Puller<V1>> fun) {
		return of(DblObjFunUtil.concat(DblObjFunUtil.map((k, v) -> {
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

	public DblObjPuller<V> cons(double key, V value) {
		return of(DblObjFunUtil.cons(key, value, source));
	}

	public int count() {
		var pair = DblObjPair.of(empty, (V) null);
		var i = 0;
		while (pull(pair))
			i++;
		return i;
	}

	public DblObjPuller<V> distinct() {
		var set = new HashSet<>();
		return of(pair -> {
			boolean b;
			while ((b = pull(pair)) && !set.add(DblObjPair.of(pair.k, pair.v)))
				;
			return b;
		});
	}

	public DblObjPuller<V> drop(int n) {
		return of(DblObjFunUtil.drop(n, source));
	}

	public DblObjPuller<V> dropWhile(DblObjPredicate<V> fun) {
		return of(DblObjFunUtil.dropWhile(fun, source));
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == DblObjPuller.class) {
			@SuppressWarnings("unchecked")
			var outlet = (DblObjPuller<V>) (DblObjPuller<?>) object;
			var source2 = outlet.source;
			boolean b, b0, b1;
			var pair0 = DblObjPair.of(empty, (V) null);
			var pair1 = DblObjPair.of(empty, (V) null);
			while ((b = (b0 = source2.source2(pair0)) == (b1 = source2.source2(pair1))) //
					&& b0 //
					&& b1 //
					&& (b = Equals.ab(pair0, pair1)))
				;
			return b;
		} else
			return false;
	}

	public DblObjPuller<V> filter(DblObjPredicate<V> fun) {
		return of(DblObjFunUtil.filter(fun, source));
	}

	public DblObjPuller<V> filterKey(DblPred fun) {
		return of(DblObjFunUtil.filterKey(fun, source));
	}

	public DblObjPuller<V> filterValue(Predicate<V> fun) {
		return of(DblObjFunUtil.filterValue(fun, source));
	}

	public DblObjPair<V> first() {
		var pair = DblObjPair.of(empty, (V) null);
		return pull(pair) ? pair : null;
	}

	public <O> Puller<O> flatMap(DblObj_Obj<V, Iterable<O>> fun) {
		return Puller.of(FunUtil.flatten(DblObjFunUtil.map(fun, source)));
	}

	@Override
	public int hashCode() {
		var pair = DblObjPair.of(empty, (V) null);
		var h = 7;
		while (pull(pair))
			h = h * 31 + pair.hashCode();
		return h;
	}

	public boolean isAll(DblObjPredicate<V> pred) {
		return DblObjFunUtil.isAll(pred, source);
	}

	public boolean isAny(DblObjPredicate<V> pred) {
		return DblObjFunUtil.isAny(pred, source);
	}

	@Override
	public Iterator<DblObjPair<V>> iterator() {
		return DblObjFunUtil.iterator(source);
	}

	public DblPuller keys() {
		return DblPuller.of(() -> {
			var pair = DblObjPair.of(empty, (V) null);
			return pull(pair) ? pair.k : empty;
		});
	}

	public DblObjPair<V> last() {
		var pair = DblObjPair.of(empty, (V) null);
		if (pull(pair))
			while (pull(pair))
				;
		else
			pair = null;
		return pair;
	}

	public <O> Puller<O> map(DblObj_Obj<V, O> fun0) {
		return map_(fun0);
	}

	public <K1, V1> Puller2<K1, V1> map2(DblObj_Obj<V, K1> kf, DblObj_Obj<V, V1> vf) {
		return Puller2.of(DblObjFunUtil.map2(kf, vf, source));
	}

	public <V1> DblObjPuller<V1> mapDblObj(DblObj_Dbl<V> kf, DblObj_Obj<V, V1> vf) {
		return mapDblObj_(kf, vf);
	}

	public <V1> DblObjPuller<V1> mapValue(Fun<V, V1> fun) {
		return mapDblObj_((k, v) -> k, (k, v) -> fun.apply(v));
	}

	public DblObjPair<V> min(Comparator<DblObjPair<V>> comparator) {
		var pair = minOrNull(comparator);
		if (pair != null)
			return pair;
		else
			return fail("no result");
	}

	public DblObjPair<V> minOrNull(Comparator<DblObjPair<V>> comparator) {
		var pair = DblObjPair.of(empty, (V) null);
		var pair1 = DblObjPair.of(empty, (V) null);
		if (pull(pair)) {
			while (pull(pair1))
				if (0 < comparator.compare(pair, pair1))
					pair.update(pair1.k, pair1.v);
			return pair;
		} else
			return null;
	}

	public DblObjPuller<V> nonBlocking(Double k0, V v0) {
		var queue = new NullableSyncQueue<DblObjPair<V>>();

		new Thread(() -> {
			boolean b;
			do {
				var pair = DblObjPair.of(empty, (V) null);
				b = source.source2(pair);
				queue.offerQuietly(pair);
			} while (b);
		}).start();

		return new DblObjPuller<>(pair -> {
			var mutable = Mutable.<DblObjPair<V>> nil();
			var b = queue.poll(mutable);
			if (b) {
				var p = mutable.value();
				pair.update(p.k, p.v);
			} else
				pair.update(k0, v0);
			return b;
		});
	}

	public DblObjPair<V> opt() {
		var pair = DblObjPair.of(empty, (V) null);
		if (pull(pair))
			if (!pull(pair))
				return pair;
			else
				return fail("more than one result");
		else
			return DblObjPair.none();
	}

	public Puller<DblObjPair<V>> pairs() {
		return Puller.of(() -> {
			var pair = DblObjPair.of(empty, (V) null);
			return pull(pair) ? pair : null;
		});
	}

	public Pair<DblObjPuller<V>, DblObjPuller<V>> partition(DblObjPredicate<V> pred) {
		return Pair.of(filter(pred), filter((k, v) -> !pred.test(k, v)));
	}

	public DblObjPuller<V> reverse() {
		return of(Reverse.of(toList()));
	}

	public void sink(DblObjSink<V> sink0) {
		var sink1 = sink0.rethrow();
		var pair = DblObjPair.of(empty, (V) null);
		while (pull(pair))
			sink1.sink2(pair.k, pair.v);
	}

	public DblObjPuller<V> skip(int n) {
		var pair = DblObjPair.of(empty, (V) null);
		var end = false;
		for (var i = 0; !end && i < n; i++)
			end = pull(pair);
		return !end ? of(source) : empty();
	}

	public DblObjPuller<V> snoc(Double key, V value) {
		return of(DblObjFunUtil.snoc(key, value, source));
	}

	public DblObjPuller<V> sort(Comparator<DblObjPair<V>> comparator) {
		var list = new ArrayList<DblObjPair<V>>();
		DblObjPair<V> pair;
		while (pull(pair = DblObjPair.of(empty, null)))
			list.add(pair);
		return of(Sort.list(list, comparator));
	}

	public <O extends Comparable<? super O>> DblObjPuller<V> sortBy(DblObj_Obj<V, O> fun) {
		return sort((e0, e1) -> Compare.objects(fun.apply(e0.k, e0.v), fun.apply(e1.k, e1.v)));
	}

	public DblObjPuller<V> sortByKey(Comparator<Double> comparator) {
		return sort((e0, e1) -> comparator.compare(e0.k, e1.k));
	}

	public DblObjPuller<V> sortByValue(Comparator<V> comparator) {
		return sort((e0, e1) -> comparator.compare(e0.v, e1.v));
	}

	public DblObjSource<V> source() {
		return source;
	}

	public Puller<DblObjPuller<V>> split(DblObjPredicate<V> fun) {
		return Puller.of(FunUtil.map(DblObjPuller<V>::new, DblObjFunUtil.split(fun, source)));
	}

	public DblObjPuller<V> take(int n) {
		return of(DblObjFunUtil.take(n, source));
	}

	public DblObjPuller<V> takeWhile(DblObjPredicate<V> fun) {
		return of(DblObjFunUtil.takeWhile(fun, source));
	}

	public DblObjPair<V>[] toArray() {
		var list = toList();
		@SuppressWarnings("unchecked")
		DblObjPair<V>[] array = New.array(DblObjPair.class, list.size());
		return list.toArray(array);
	}

	public List<DblObjPair<V>> toList() {
		var list = new ArrayList<DblObjPair<V>>();
		DblObjPair<V> pair;
		while (pull(pair = DblObjPair.of(empty, null)))
			list.add(pair);
		return list;
	}

	public Set<DblObjPair<V>> toSet() {
		var set = new HashSet<DblObjPair<V>>();
		DblObjPair<V> pair;
		while (pull(pair = DblObjPair.of(empty, null)))
			set.add(pair);
		return set;

	}

	public Puller<V> values() {
		return map_((k, v) -> v);
	}

	private <O> Puller<O> map_(DblObj_Obj<V, O> fun0) {
		return Puller.of(DblObjFunUtil.map(fun0, source));
	}

	private <V1> DblObjPuller<V1> mapDblObj_(DblObj_Dbl<V> kf, DblObj_Obj<V, V1> vf) {
		return of(DblObjFunUtil.mapDblObj(kf, vf, source));
	}

	private boolean pull(DblObjPair_<V> pair) {
		return source.source2(pair);
	}

}
