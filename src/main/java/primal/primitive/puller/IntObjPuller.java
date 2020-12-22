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
import primal.primitive.IntObj_Int;
import primal.primitive.IntPrim;
import primal.primitive.IntPrim.IntObjPair_;
import primal.primitive.IntPrim.IntObjPredicate;
import primal.primitive.IntPrim.IntObjSink;
import primal.primitive.IntPrim.IntObjSource;
import primal.primitive.IntPrim.IntObj_Obj;
import primal.primitive.IntPrim.IntPred;
import primal.primitive.adt.pair.IntObjPair;
import primal.primitive.fp.IntObjFunUtil;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.puller.PullerDefaults;

public class IntObjPuller<V> implements PullerDefaults<IntObjPair<V>, IntObjPair<V>, IntObjPredicate<V>, IntObjSink<V>, IntObjSource<V>> {

	private static int empty = IntPrim.EMPTYVALUE;

	private IntObjSource<V> source;

	@SafeVarargs
	public static <V> IntObjPuller<V> concat(IntObjPuller<V>... outlets) {
		var sources = new ArrayList<IntObjSource<V>>();
		for (var outlet : outlets)
			sources.add(outlet.source);
		return of(IntObjFunUtil.concat(Take.from(sources)));
	}

	public static <V> IntObjPuller<V> empty() {
		return of(IntObjFunUtil.nullSource());
	}

	@SafeVarargs
	public static <V> IntObjPuller<V> of(IntObjPair<V>... kvs) {
		return of(new IntObjSource<>() {
			private int i;

			public boolean source2(IntObjPair_<V> pair) {
				var b = i < kvs.length;
				if (b) {
					var kv = kvs[i];
					pair.update(kv.k, kv.v);
				}
				return b;

			}
		});
	}

	public static <V> IntObjPuller<V> of(Iterable<IntObjPair<V>> col) {
		var iter = col.iterator();
		return of(new IntObjSource<>() {
			public boolean source2(IntObjPair_<V> pair) {
				var b = iter.hasNext();
				if (b) {
					var pair1 = iter.next();
					pair.update(pair1.k, pair1.v);
				}
				return b;
			}
		});
	}

	public static <V> IntObjPuller<V> of(IntObjSource<V> source) {
		return new IntObjPuller<>(source);
	}

	private IntObjPuller(IntObjSource<V> source) {
		this.source = source;
	}

	public Puller<IntObjPuller<V>> chunk(int n) {
		return Puller.of(FunUtil.map(IntObjPuller<V>::new, IntObjFunUtil.chunk(n, source)));
	}

	public IntObjPuller<V> closeAtEnd(Closeable c) {
		return of(pair -> {
			var b = pull(pair);
			if (!b)
				Close.quietly(c);
			return b;
		});
	}

	public <R> R collect(Fun<IntObjPuller<V>, R> fun) {
		return fun.apply(this);
	}

	public <O> Puller<O> concatMap(IntObj_Obj<V, Puller<O>> fun) {
		return Puller.of(FunUtil.concat(IntObjFunUtil.map((k, v) -> fun.apply(k, v).source(), source)));
	}

	public <K1, V1> Puller2<K1, V1> concatMap2(IntObj_Obj<V, Puller2<K1, V1>> fun) {
		return Puller2.of(FunUtil2.concat(IntObjFunUtil.map((k, v) -> fun.apply(k, v).source(), source)));
	}

	public <V1> IntObjPuller<V1> concatMapIntObj(IntObj_Obj<V, IntObjPuller<V1>> fun) {
		return of(IntObjFunUtil.concat(IntObjFunUtil.map((k, v) -> fun.apply(k, v).source, source)));
	}

	public <V1> IntObjPuller<V1> concatMapValue(Fun<V, Puller<V1>> fun) {
		return of(IntObjFunUtil.concat(IntObjFunUtil.map((k, v) -> {
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

	public IntObjPuller<V> cons(int key, V value) {
		return of(IntObjFunUtil.cons(key, value, source));
	}

	public int count() {
		var pair = IntObjPair.of(empty, (V) null);
		var i = 0;
		while (pull(pair))
			i++;
		return i;
	}

	public IntObjPuller<V> distinct() {
		var set = new HashSet<>();
		return of(pair -> {
			boolean b;
			while ((b = pull(pair)) && !set.add(IntObjPair.of(pair.k, pair.v)))
				;
			return b;
		});
	}

	public IntObjPuller<V> drop(int n) {
		return of(IntObjFunUtil.drop(n, source));
	}

	public IntObjPuller<V> dropWhile(IntObjPredicate<V> fun) {
		return of(IntObjFunUtil.dropWhile(fun, source));
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == IntObjPuller.class) {
			@SuppressWarnings("unchecked")
			var outlet = (IntObjPuller<V>) (IntObjPuller<?>) object;
			var source2 = outlet.source;
			boolean b, b0, b1;
			var pair0 = IntObjPair.of(empty, (V) null);
			var pair1 = IntObjPair.of(empty, (V) null);
			while ((b = (b0 = source2.source2(pair0)) == (b1 = source2.source2(pair1))) //
					&& b0 //
					&& b1 //
					&& (b = Equals.ab(pair0, pair1)))
				;
			return b;
		} else
			return false;
	}

	public IntObjPuller<V> filter(IntObjPredicate<V> fun) {
		return of(IntObjFunUtil.filter(fun, source));
	}

	public IntObjPuller<V> filterKey(IntPred fun) {
		return of(IntObjFunUtil.filterKey(fun, source));
	}

	public IntObjPuller<V> filterValue(Predicate<V> fun) {
		return of(IntObjFunUtil.filterValue(fun, source));
	}

	public IntObjPair<V> first() {
		var pair = IntObjPair.of(empty, (V) null);
		return pull(pair) ? pair : null;
	}

	public <O> Puller<O> flatMap(IntObj_Obj<V, Iterable<O>> fun) {
		return Puller.of(FunUtil.flatten(IntObjFunUtil.map(fun, source)));
	}

	@Override
	public int hashCode() {
		var pair = IntObjPair.of(empty, (V) null);
		var h = 7;
		while (pull(pair))
			h = h * 31 + pair.hashCode();
		return h;
	}

	public boolean isAll(IntObjPredicate<V> pred) {
		return IntObjFunUtil.isAll(pred, source);
	}

	public boolean isAny(IntObjPredicate<V> pred) {
		return IntObjFunUtil.isAny(pred, source);
	}

	@Override
	public Iterator<IntObjPair<V>> iterator() {
		return IntObjFunUtil.iterator(source);
	}

	public IntPuller keys() {
		return IntPuller.of(() -> {
			var pair = IntObjPair.of(empty, (V) null);
			return pull(pair) ? pair.k : empty;
		});
	}

	public IntObjPair<V> last() {
		var pair = IntObjPair.of(empty, (V) null);
		if (pull(pair))
			while (pull(pair))
				;
		else
			pair = null;
		return pair;
	}

	public <O> Puller<O> map(IntObj_Obj<V, O> fun0) {
		return map_(fun0);
	}

	public <K1, V1> Puller2<K1, V1> map2(IntObj_Obj<V, K1> kf, IntObj_Obj<V, V1> vf) {
		return Puller2.of(IntObjFunUtil.map2(kf, vf, source));
	}

	public <V1> IntObjPuller<V1> mapIntObj(IntObj_Int<V> kf, IntObj_Obj<V, V1> vf) {
		return mapIntObj_(kf, vf);
	}

	public <V1> IntObjPuller<V1> mapValue(Fun<V, V1> fun) {
		return mapIntObj_((k, v) -> k, (k, v) -> fun.apply(v));
	}

	public IntObjPair<V> min(Comparator<IntObjPair<V>> comparator) {
		var pair = minOrNull(comparator);
		if (pair != null)
			return pair;
		else
			return fail("no result");
	}

	public IntObjPair<V> minOrNull(Comparator<IntObjPair<V>> comparator) {
		var pair = IntObjPair.of(empty, (V) null);
		var pair1 = IntObjPair.of(empty, (V) null);
		if (pull(pair)) {
			while (pull(pair1))
				if (0 < comparator.compare(pair, pair1))
					pair.update(pair1.k, pair1.v);
			return pair;
		} else
			return null;
	}

	public IntObjPuller<V> nonBlocking(Integer k0, V v0) {
		var queue = new NullableSyncQueue<IntObjPair<V>>();

		new Thread(() -> {
			boolean b;
			do {
				var pair = IntObjPair.of(empty, (V) null);
				b = source.source2(pair);
				queue.offerQuietly(pair);
			} while (b);
		}).start();

		return new IntObjPuller<>(pair -> {
			var mutable = Mutable.<IntObjPair<V>> nil();
			var b = queue.poll(mutable);
			if (b) {
				var p = mutable.value();
				pair.update(p.k, p.v);
			} else
				pair.update(k0, v0);
			return b;
		});
	}

	public IntObjPair<V> opt() {
		var pair = IntObjPair.of(empty, (V) null);
		if (pull(pair))
			if (!pull(pair))
				return pair;
			else
				return fail("more than one result");
		else
			return IntObjPair.none();
	}

	public Puller<IntObjPair<V>> pairs() {
		return Puller.of(() -> {
			var pair = IntObjPair.of(empty, (V) null);
			return pull(pair) ? pair : null;
		});
	}

	public Pair<IntObjPuller<V>, IntObjPuller<V>> partition(IntObjPredicate<V> pred) {
		return Pair.of(filter(pred), filter((k, v) -> !pred.test(k, v)));
	}

	public IntObjPuller<V> reverse() {
		return of(Reverse.of(toList()));
	}

	public void sink(IntObjSink<V> sink0) {
		var sink1 = sink0.rethrow();
		var pair = IntObjPair.of(empty, (V) null);
		while (pull(pair))
			sink1.sink2(pair.k, pair.v);
	}

	public IntObjPuller<V> skip(int n) {
		var pair = IntObjPair.of(empty, (V) null);
		var end = false;
		for (var i = 0; !end && i < n; i++)
			end = pull(pair);
		return !end ? of(source) : empty();
	}

	public IntObjPuller<V> snoc(Integer key, V value) {
		return of(IntObjFunUtil.snoc(key, value, source));
	}

	public IntObjPuller<V> sort(Comparator<IntObjPair<V>> comparator) {
		var list = new ArrayList<IntObjPair<V>>();
		IntObjPair<V> pair;
		while (pull(pair = IntObjPair.of(empty, null)))
			list.add(pair);
		return of(Sort.list(list, comparator));
	}

	public <O extends Comparable<? super O>> IntObjPuller<V> sortBy(IntObj_Obj<V, O> fun) {
		return sort((e0, e1) -> Compare.objects(fun.apply(e0.k, e0.v), fun.apply(e1.k, e1.v)));
	}

	public IntObjPuller<V> sortByKey(Comparator<Integer> comparator) {
		return sort((e0, e1) -> comparator.compare(e0.k, e1.k));
	}

	public IntObjPuller<V> sortByValue(Comparator<V> comparator) {
		return sort((e0, e1) -> comparator.compare(e0.v, e1.v));
	}

	public IntObjSource<V> source() {
		return source;
	}

	public Puller<IntObjPuller<V>> split(IntObjPredicate<V> fun) {
		return Puller.of(FunUtil.map(IntObjPuller<V>::new, IntObjFunUtil.split(fun, source)));
	}

	public IntObjPuller<V> take(int n) {
		return of(IntObjFunUtil.take(n, source));
	}

	public IntObjPuller<V> takeWhile(IntObjPredicate<V> fun) {
		return of(IntObjFunUtil.takeWhile(fun, source));
	}

	public IntObjPair<V>[] toArray() {
		var list = toList();
		@SuppressWarnings("unchecked")
		IntObjPair<V>[] array = New.array(IntObjPair.class, list.size());
		return list.toArray(array);
	}

	public List<IntObjPair<V>> toList() {
		var list = new ArrayList<IntObjPair<V>>();
		IntObjPair<V> pair;
		while (pull(pair = IntObjPair.of(empty, null)))
			list.add(pair);
		return list;
	}

	public Set<IntObjPair<V>> toSet() {
		var set = new HashSet<IntObjPair<V>>();
		IntObjPair<V> pair;
		while (pull(pair = IntObjPair.of(empty, null)))
			set.add(pair);
		return set;

	}

	public Puller<V> values() {
		return map_((k, v) -> v);
	}

	private <O> Puller<O> map_(IntObj_Obj<V, O> fun0) {
		return Puller.of(IntObjFunUtil.map(fun0, source));
	}

	private <V1> IntObjPuller<V1> mapIntObj_(IntObj_Int<V> kf, IntObj_Obj<V, V1> vf) {
		return of(IntObjFunUtil.mapIntObj(kf, vf, source));
	}

	private boolean pull(IntObjPair_<V> pair) {
		return source.source2(pair);
	}

}
