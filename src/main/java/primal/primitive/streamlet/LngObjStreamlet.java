package primal.primitive.streamlet;

import static primal.statics.Fail.fail;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.Verbs.Take;
import primal.adt.Pair;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.LngObj_Lng;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.LngObjPredicate;
import primal.primitive.LngPrim.LngObjSink;
import primal.primitive.LngPrim.LngObjSource;
import primal.primitive.LngPrim.LngObj_Obj;
import primal.primitive.LngPrim.LngPred;
import primal.primitive.adt.map.LngObjMap;
import primal.primitive.adt.map.ObjLngMap;
import primal.primitive.adt.pair.LngObjPair;
import primal.primitive.fp.LngObjFunUtil;
import primal.primitive.puller.LngObjPuller;
import primal.primitive.puller.LngPuller;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.streamlet.Streamlet;
import primal.streamlet.Streamlet2;
import primal.streamlet.StreamletDefaults;

public class LngObjStreamlet<V> implements StreamletDefaults<LngObjPair<V>, LngObjPair<V>, LngObjPredicate<V>, LngObjPuller<V>, LngObjSink<V>, LngObjSource<V>> {

	private Source<LngObjPuller<V>> in;

	@SafeVarargs
	public static <V> LngObjStreamlet<V> concat(LngObjStreamlet<V>... streamlets) {
		return chrObjStreamlet(() -> {
			var source = Take.from(streamlets);
			return LngObjPuller.of(LngObjFunUtil.concat(FunUtil.map(st -> st.spawn().source(), source)));
		});
	}

	public LngObjStreamlet(Source<LngObjPuller<V>> in) {
		this.in = in;
	}

	public <R> R apply(Fun<LngObjStreamlet<V>, R> fun) {
		return fun.apply(this);
	}

	public Streamlet<LngObjPuller<V>> chunk(int n) {
		return streamlet(() -> spawn().chunk(n));
	}

	public LngObjStreamlet<V> closeAtEnd(Closeable c) {
		return chrObjStreamlet(() -> {
			var in = spawn();
			in.closeAtEnd(c);
			return in;
		});
	}

	public <O> Streamlet<O> concatMap(LngObj_Obj<V, Streamlet<O>> fun) {
		return concatMap_(fun);
	}

	public <K1, V1> Streamlet2<K1, V1> concatMap2(LngObj_Obj<V, Streamlet2<K1, V1>> fun) {
		return concatMap2_(fun);
	}

	public <V1> LngObjStreamlet<V1> concatMapLngObj(LngObj_Obj<V, LngObjStreamlet<V1>> fun) {
		return concatMapLngObj_(fun);
	}

	public <V1> LngObjStreamlet<V1> concatMapValue(Fun<V, Streamlet<V1>> fun) {
		Fun<V, Puller<V1>> f = v -> fun.apply(v).puller();
		return chrObjStreamlet(() -> LngObjPuller.of(spawn().concatMapValue(f)));
	}

	public LngObjStreamlet<V> cons(long key, V value) {
		return chrObjStreamlet(() -> spawn().cons(key, value));
	}

	public LngObjStreamlet<V> distinct() {
		return chrObjStreamlet(() -> spawn().distinct());
	}

	public LngObjStreamlet<V> drop(int n) {
		return chrObjStreamlet(() -> spawn().drop(n));
	}

	public LngObjStreamlet<V> dropWhile(LngObjPredicate<V> fun) {
		return chrObjStreamlet(() -> spawn().dropWhile(fun));
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == LngObjStreamlet.class ? Equals.ab(spawn(), ((LngObjStreamlet<?>) object).spawn())
				: false;
	}

	public LngObjStreamlet<V> filter(LngObjPredicate<V> fun) {
		return chrObjStreamlet(() -> spawn().filter(fun));
	}

	public LngObjStreamlet<V> filterKey(LngPred fun) {
		return chrObjStreamlet(() -> spawn().filterKey(fun));
	}

	public LngObjStreamlet<V> filterValue(Predicate<V> fun) {
		return chrObjStreamlet(() -> spawn().filterValue(fun));
	}

	public LngObjPair<V> first() {
		return spawn().first();
	}

	public <O> Streamlet<O> flatMap(LngObj_Obj<V, Iterable<O>> fun) {
		return streamlet(() -> spawn().flatMap(fun));
	}

	public LngObjStreamlet<List<V>> groupBy() {
		return chrObjStreamlet(this::groupBy_);
	}

	public <V1> LngObjStreamlet<V1> groupBy(Fun<Streamlet<V>, V1> fun) {
		return chrObjStreamlet(() -> groupBy_().mapValue(list -> fun.apply(new Streamlet<>(() -> Puller.of(list)))));
	}

	@Override
	public int hashCode() {
		return spawn().hashCode();
	}

	@Override
	public Iterator<LngObjPair<V>> iterator() {
		return spawn().iterator();
	}

	public LngStreamlet keys() {
		return chrStreamlet(() -> spawn().keys());
	}

	public LngObjPair<V> last() {
		return spawn().last();
	}

	public <O> Streamlet<O> map(LngObj_Obj<V, O> fun) {
		return map_(fun);
	}

	public <V1> Streamlet2<Long, V1> map2(LngObj_Obj<V, V1> vf) {
		return map2_((k, v) -> k, vf);
	}

	public <K1, V1> Streamlet2<K1, V1> map2(LngObj_Obj<V, K1> kf, LngObj_Obj<V, V1> vf) {
		return map2_(kf, vf);
	}

	public <V1> LngObjStreamlet<V1> mapLngObj(LngObj_Lng<V> kf, LngObj_Obj<V, V1> vf) {
		return mapLngObj_(kf, vf);
	}

	public <V1> LngObjStreamlet<V1> mapValue(Fun<V, V1> fun) {
		return chrObjStreamlet(() -> spawn().mapValue(fun));
	}

	public LngObjPair<V> min(Comparator<LngObjPair<V>> comparator) {
		return spawn().min(comparator);
	}

	public LngObjPair<V> minOrNull(Comparator<LngObjPair<V>> comparator) {
		return spawn().minOrNull(comparator);
	}

	public Streamlet<LngObjPair<V>> pairs() {
		return streamlet(() -> spawn().pairs());
	}

	public Pair<LngObjStreamlet<V>, LngObjStreamlet<V>> partition(LngObjPredicate<V> pred) {
		return Pair.of(filter(pred), filter((k, v) -> !pred.test(k, v)));
	}

	public LngObjPuller<V> puller() {
		return spawn();
	}

	public LngObjStreamlet<V> reverse() {
		return chrObjStreamlet(() -> spawn().reverse());
	}

	public LngObjStreamlet<V> skip(int n) {
		return chrObjStreamlet(() -> spawn().skip(n));
	}

	public LngObjStreamlet<V> snoc(long key, V value) {
		return chrObjStreamlet(() -> spawn().snoc(key, value));
	}

	public LngObjStreamlet<V> sort(Comparator<LngObjPair<V>> comparator) {
		return chrObjStreamlet(() -> spawn().sort(comparator));
	}

	public <O extends Comparable<? super O>> LngObjStreamlet<V> sortBy(LngObj_Obj<V, O> fun) {
		return chrObjStreamlet(() -> spawn().sortBy(fun));
	}

	public LngObjStreamlet<V> sortByKey(Comparator<Long> comparator) {
		return chrObjStreamlet(() -> spawn().sortByKey(comparator));
	}

	public LngObjStreamlet<V> sortByValue(Comparator<V> comparator) {
		return chrObjStreamlet(() -> spawn().sortByValue(comparator));
	}

	public LngObjStreamlet<V> take(int n) {
		return chrObjStreamlet(() -> spawn().take(n));
	}

	public LngObjStreamlet<V> takeWhile(LngObjPredicate<V> fun) {
		return chrObjStreamlet(() -> spawn().takeWhile(fun));
	}

	public LngObjPair<V>[] toArray() {
		return spawn().toArray();
	}

	public List<LngObjPair<V>> toList() {
		return toList_();
	}

	public LngObjMap<List<V>> toListMap() {
		return toListMap_();
	}

	public LngObjMap<V> toMap() {
		var source = spawn().source();
		var map = new LngObjMap<V>();
		var pair = LngObjPair.of(LngPrim.EMPTYVALUE, (V) null);
		while (source.source2(pair))
			map.put(pair.k, pair.v);
		return map;
	}

	public ListMultimap<Long, V> toMultimap() {
		var map = new ListMultimap<Long, V>();
		groupBy_().concatMapValue(Puller::of).sink(map::put);
		return map;
	}

	public ObjLngMap<V> toObjLngMap() {
		var source = spawn().source();
		var pair = LngObjPair.of(LngPrim.EMPTYVALUE, (V) null);
		var map = new ObjLngMap<V>();
		while (source.source2(pair))
			map.put(pair.v, pair.k);
		return map;
	}

	public Set<LngObjPair<V>> toSet() {
		return spawn().toSet();
	}

	public LngObjPair<V> uniqueResult() {
		var pair = spawn().opt();
		return pair.k != LngPrim.EMPTYVALUE ? pair : fail("no result");
	}

	public Streamlet<V> values() {
		return streamlet(() -> spawn().values());
	}

	private <T> Streamlet<T> concatMap_(LngObj_Obj<V, Streamlet<T>> fun) {
		LngObj_Obj<V, Puller<T>> bf = (k, v) -> fun.apply(k, v).puller();
		return streamlet(() -> Puller.of(spawn().concatMap(bf)));
	}

	private <V1, K1> Streamlet2<K1, V1> concatMap2_(LngObj_Obj<V, Streamlet2<K1, V1>> fun) {
		LngObj_Obj<V, Puller2<K1, V1>> bf = (k, v) -> fun.apply(k, v).puller();
		return streamlet2(() -> Puller2.of(spawn().concatMap2(bf)));
	}

	private <V1> LngObjStreamlet<V1> concatMapLngObj_(LngObj_Obj<V, LngObjStreamlet<V1>> fun) {
		LngObj_Obj<V, LngObjPuller<V1>> bf = (k, v) -> fun.apply(k, v).puller();
		return chrObjStreamlet(() -> LngObjPuller.of(spawn().concatMapLngObj(bf)));
	}

	private LngObjPuller<List<V>> groupBy_() {
		return LngObjPuller.of(toListMap_().source());
	}

	private <T> Streamlet<T> map_(LngObj_Obj<V, T> fun) {
		return streamlet(() -> spawn().map(fun));
	}

	private <K1, V1> Streamlet2<K1, V1> map2_(LngObj_Obj<V, K1> kf, LngObj_Obj<V, V1> vf) {
		return streamlet2(() -> spawn().map2(kf, vf));
	}

	private <V1> LngObjStreamlet<V1> mapLngObj_(LngObj_Lng<V> kf, LngObj_Obj<V, V1> vf) {
		return chrObjStreamlet(() -> spawn().mapLngObj(kf, vf));
	}

	private List<LngObjPair<V>> toList_() {
		return spawn().toList();
	}

	private LngObjMap<List<V>> toListMap_() {
		var source = spawn().source();
		var map = new LngObjMap<List<V>>();
		var pair = LngObjPair.of(LngPrim.EMPTYVALUE, (V) null);
		while (source.source2(pair))
			map.computeIfAbsent(pair.k, k_ -> new ArrayList<>()).add(pair.v);
		return map;
	}

	private LngObjPuller<V> spawn() {
		return in.g();
	}

	private static LngStreamlet chrStreamlet(Source<LngPuller> in) {
		return new LngStreamlet(in);
	}

	private static <V> LngObjStreamlet<V> chrObjStreamlet(Source<LngObjPuller<V>> in) {
		return new LngObjStreamlet<>(in);
	}

	private static <K, V> Streamlet2<K, V> streamlet2(Source<Puller2<K, V>> in) {
		return new Streamlet2<>(in);
	}

	private static <T> Streamlet<T> streamlet(Source<Puller<T>> in) {
		return new Streamlet<>(in);
	}

}
