package primal.primitive.streamlet;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.adt.Pair;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.fp.Funs2.Fun2;
import primal.primitive.LngOpt;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.LngComparator;
import primal.primitive.LngPrim.LngObjPair_;
import primal.primitive.LngPrim.LngObjSource;
import primal.primitive.LngPrim.LngObj_Obj;
import primal.primitive.LngPrim.LngPred;
import primal.primitive.LngPrim.LngSink;
import primal.primitive.LngPrim.LngSource;
import primal.primitive.LngPrim.Lng_Obj;
import primal.primitive.Lng_Lng;
import primal.primitive.adt.Longs;
import primal.primitive.adt.Longs.LongsBuilder;
import primal.primitive.adt.map.LngObjMap;
import primal.primitive.adt.map.ObjLngMap;
import primal.primitive.adt.set.LngSet;
import primal.primitive.puller.LngObjPuller;
import primal.primitive.puller.LngPuller;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.streamlet.Streamlet;
import primal.streamlet.Streamlet2;
import primal.streamlet.StreamletDefaults;

public class LngStreamlet implements StreamletDefaults<Long, LngOpt, LngPred, LngPuller, LngSink, LngSource> {

	private Source<LngPuller> in;

	public LngStreamlet(Source<LngPuller> in) {
		this.in = in;
	}

	public <R> R apply(Fun<LngStreamlet, R> fun) {
		return fun.apply(this);
	}

	public long average() {
		return spawn().average();
	}

	public Streamlet<LngPuller> chunk(int n) {
		return streamlet(() -> spawn().chunk(n));
	}

	public LngStreamlet closeAtEnd(Closeable c) {
		return longStreamlet(() -> {
			var in = spawn();
			in.closeAtEnd(c);
			return in;
		});
	}

	public <O> Streamlet<O> concatMap(Lng_Obj<Streamlet<O>> fun) {
		return concatMap_(fun);
	}

	public <K, V> Streamlet2<K, V> concatMap2(Lng_Obj<Streamlet2<K, V>> fun) {
		return concatMap2_(fun);
	}

	public LngStreamlet cons(long c) {
		return longStreamlet(() -> spawn().cons(c));
	}

	public LngStreamlet collect() {
		var longs = toList_();
		return longStreamlet(() -> LngPuller.of(longs.cs, longs.start, longs.end, 1));
	}

	public <U, O> Streamlet<O> cross(Streamlet<U> st1, LngObj_Obj<U, O> fun) {
		return streamlet(() -> spawn().cross(st1.toList(), fun));
	}

	public LngStreamlet distinct() {
		return longStreamlet(() -> spawn().distinct());
	}

	public LngStreamlet drop(int n) {
		return longStreamlet(() -> spawn().drop(n));
	}

	public LngStreamlet dropWhile(LngPred fun) {
		return longStreamlet(() -> spawn().dropWhile(fun));
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == LngStreamlet.class ? Equals.ab(spawn(), ((LngStreamlet) object).spawn()) : false;
	}

	public LngStreamlet filter(LngPred fun) {
		return longStreamlet(() -> spawn().filter(fun));
	}

	public long first() {
		return spawn().first();
	}

	public <O> Streamlet<O> flatMap(Lng_Obj<Iterable<O>> fun) {
		return streamlet(() -> spawn().flatMap(fun));
	}

	public <R> R fold(R init, LngObj_Obj<R, R> fun) {
		return spawn().fold(init, fun);
	}

	public <U, V, W> W forkJoin(Fun<LngStreamlet, U> fork0, Fun<LngStreamlet, V> fork1, Fun2<U, V, W> join) {
		return join.apply(fork0.apply(this), fork1.apply(this));
	}

	public <V> LngObjStreamlet<LongsBuilder> groupBy() {
		return longObjStreamlet(this::groupBy_);
	}

	public <V> LngObjStreamlet<V> groupBy(Fun<LngStreamlet, V> fun) {
		return new LngObjStreamlet<V>(() -> groupBy_().mapValue(list -> fun.apply(longStreamlet(()-> list.toLongs().puller()))));
	}

	@Override
	public int hashCode() {
		return spawn().hashCode();
	}

	public LngObjStreamlet<Integer> index() {
		return longObjStreamlet(() -> LngObjPuller.of(new LngObjSource<>() {
			private LngPuller puller = spawn();
			private int i = 0;

			public boolean source2(LngObjPair_<Integer> pair) {
				var c = puller.pull();
				if (c != LngPrim.EMPTYVALUE) {
					pair.update(c, i++);
					return true;
				} else
					return false;
			}
		}));
	}

	@Override
	public Iterator<Long> iterator() {
		return spawn().iterator();
	}

	public <O> Streamlet2<Long, O> join2(Streamlet<O> streamlet) {
		return concatMap2_(t -> streamlet.map2(v -> t, v -> v));
	}

	public long last() {
		return spawn().last();
	}

	public <O> Streamlet<O> map(Lng_Obj<O> fun) {
		return map_(fun);
	}

	public <K, V> Streamlet2<K, V> map2(Lng_Obj<K> kf, Lng_Obj<V> vf) {
		return map2_(kf, vf);
	}

	public LngStreamlet mapLng(Lng_Lng fun) {
		return longStreamlet(() -> spawn().mapLng(fun));
	}

	public <K, V> LngObjStreamlet<V> mapLngObj(Lng_Obj<V> fun0) {
		return longObjStreamlet(() -> spawn().mapLngObj(fun0));
	}

	public long max() {
		return spawn().min((c0, c1) -> Long.compare(c1, c0));
	}

	public long min() {
		return spawn().min((c0, c1) -> Long.compare(c0, c1));
	}

	public long min(LngComparator comparator) {
		return spawn().min(comparator);
	}

	public long minOrEmpty(LngComparator comparator) {
		return spawn().minOrEmpty(comparator);
	}

	public Pair<LngStreamlet, LngStreamlet> partition(LngPred pred) {
		return Pair.of(filter(pred), filter(t -> !pred.test(t)));
	}

	public LngPuller puller() {
		return spawn();
	}

	public LngStreamlet reverse() {
		return longStreamlet(() -> spawn().reverse());
	}

	public LngStreamlet skip(int n) {
		return longStreamlet(() -> spawn().skip(n));
	}

	public LngStreamlet snoc(long c) {
		return longStreamlet(() -> spawn().snoc(c));
	}

	public LngStreamlet sort() {
		return longStreamlet(() -> spawn().sort());
	}

	public long sum() {
		return spawn().sum();
	}

	public LngStreamlet take(int n) {
		return longStreamlet(() -> spawn().take(n));
	}

	public LngStreamlet takeWhile(LngPred fun) {
		return longStreamlet(() -> spawn().takeWhile(fun));
	}

	public long[] toArray() {
		return spawn().toArray();
	}

	public Longs toList() {
		return toList_();
	}

	public <K> LngObjMap<LongsBuilder> toListMap() {
		return toListMap_();
	}

	public <K> LngObjMap<LongsBuilder> toListMap(Lng_Lng valueFun) {
		return toListMap_(valueFun);
	}

	public <K> ObjLngMap<K> toMap(Lng_Obj<K> kf0) {
		var puller = spawn();
		var kf1 = kf0.rethrow();
		var map = new ObjLngMap<K>();
		long c;
		while ((c = puller.pull()) != LngPrim.EMPTYVALUE)
			map.put(kf1.apply(c), c);
		return map;
	}

	public <K, V> Map<K, V> toMap(Lng_Obj<K> keyFun, Lng_Obj<V> valueFun) {
		return spawn().toMap(keyFun, valueFun);
	}

	public LngSet toSet() {
		var puller = spawn();
		var set = new LngSet();
		long c;
		while ((c = puller.pull()) != LngPrim.EMPTYVALUE)
			set.add(c);
		return set;
	}

	public <K, V> Map<K, Set<V>> toSetMap(Lng_Obj<K> keyFun, Lng_Obj<V> valueFun) {
		return spawn().map2(keyFun, valueFun).groupBy().mapValue(values -> streamlet(values).toSet()).toMap();
	}

	public long uniqueResult() {
		return spawn().opt().g();
	}

	public <U, V> Streamlet<V> zip(Iterable<U> list1, LngObj_Obj<U, V> fun) {
		return streamlet(() -> spawn().zip(Puller.of(list1), fun));
	}

	private <O> Streamlet<O> concatMap_(Lng_Obj<Streamlet<O>> fun) {
		return streamlet(() -> spawn().concatMap(t -> fun.apply(t).puller()));
	}

	private <K, V> Streamlet2<K, V> concatMap2_(Lng_Obj<Streamlet2<K, V>> fun) {
		return streamlet2(() -> spawn().concatMap2(t -> fun.apply(t).puller()));
	}

	private <V> LngObjPuller<LongsBuilder> groupBy_() {
		return LngObjPuller.of(toListMap_().source());
	}

	private <O> Streamlet<O> map_(Lng_Obj<O> fun) {
		return streamlet(() -> spawn().map(fun));
	}

	private <K, V> Streamlet2<K, V> map2_(Lng_Obj<K> kf, Lng_Obj<V> vf) {
		return streamlet2(() -> spawn().map2(kf, vf));
	}

	private Longs toList_() {
		var list = spawn().toList();
		return Longs.of(list.cs, 0, list.size);
	}

	private LngObjMap<LongsBuilder> toListMap_() {
		return toListMap_(value -> value);
	}

	private LngObjMap<LongsBuilder> toListMap_(Lng_Lng valueFun) {
		var puller = spawn();
		var map = new LngObjMap<LongsBuilder>();
		long c;
		while ((c = puller.pull()) != LngPrim.EMPTYVALUE)
			map.computeIfAbsent(c, k_ -> new LongsBuilder()).append(valueFun.apply(c));
		return map;
	}

	private LngPuller spawn() {
		return in.g();
	}

	private static <T> Streamlet<T> streamlet(Iterable<T> col) {
		return streamlet(() -> Puller.of(col));
	}

	private static LngStreamlet longStreamlet(Source<LngPuller> in) {
		return new LngStreamlet(in);
	}

	private static <V> LngObjStreamlet<V> longObjStreamlet(Source<LngObjPuller<V>> in) {
		return new LngObjStreamlet<>(in);
	}

	private static <K, V> Streamlet2<K, V> streamlet2(Source<Puller2<K, V>> in) {
		return new Streamlet2<>(in);
	}

	private static <T> Streamlet<T> streamlet(Source<Puller<T>> in) {
		return new Streamlet<>(in);
	}

}
