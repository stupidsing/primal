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
import primal.primitive.DblObj_Dbl;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.DblObjPredicate;
import primal.primitive.DblPrim.DblObjSink;
import primal.primitive.DblPrim.DblObjSource;
import primal.primitive.DblPrim.DblObj_Obj;
import primal.primitive.DblPrim.DblPred;
import primal.primitive.adt.map.DblObjMap;
import primal.primitive.adt.map.ObjDblMap;
import primal.primitive.adt.pair.DblObjPair;
import primal.primitive.fp.DblObjFunUtil;
import primal.primitive.puller.DblObjPuller;
import primal.primitive.puller.DblPuller;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.streamlet.Streamlet;
import primal.streamlet.Streamlet2;
import primal.streamlet.StreamletDefaults;

public class DblObjStreamlet<V> implements StreamletDefaults<DblObjPair<V>, DblObjPair<V>, DblObjPredicate<V>, DblObjPuller<V>, DblObjSink<V>, DblObjSource<V>> {

	private Source<DblObjPuller<V>> in;

	@SafeVarargs
	public static <V> DblObjStreamlet<V> concat(DblObjStreamlet<V>... streamlets) {
		return chrObjStreamlet(() -> {
			var source = Take.from(streamlets);
			return DblObjPuller.of(DblObjFunUtil.concat(FunUtil.map(st -> st.spawn().source(), source)));
		});
	}

	public DblObjStreamlet(Source<DblObjPuller<V>> in) {
		this.in = in;
	}

	public <R> R apply(Fun<DblObjStreamlet<V>, R> fun) {
		return fun.apply(this);
	}

	public Streamlet<DblObjPuller<V>> chunk(int n) {
		return streamlet(() -> spawn().chunk(n));
	}

	public DblObjStreamlet<V> closeAtEnd(Closeable c) {
		return chrObjStreamlet(() -> {
			var in = spawn();
			in.closeAtEnd(c);
			return in;
		});
	}

	public <O> Streamlet<O> concatMap(DblObj_Obj<V, Streamlet<O>> fun) {
		return concatMap_(fun);
	}

	public <K1, V1> Streamlet2<K1, V1> concatMap2(DblObj_Obj<V, Streamlet2<K1, V1>> fun) {
		return concatMap2_(fun);
	}

	public <V1> DblObjStreamlet<V1> concatMapDblObj(DblObj_Obj<V, DblObjStreamlet<V1>> fun) {
		return concatMapDblObj_(fun);
	}

	public <V1> DblObjStreamlet<V1> concatMapValue(Fun<V, Streamlet<V1>> fun) {
		Fun<V, Puller<V1>> f = v -> fun.apply(v).puller();
		return chrObjStreamlet(() -> DblObjPuller.of(spawn().concatMapValue(f)));
	}

	public DblObjStreamlet<V> cons(double key, V value) {
		return chrObjStreamlet(() -> spawn().cons(key, value));
	}

	public DblObjStreamlet<V> distinct() {
		return chrObjStreamlet(() -> spawn().distinct());
	}

	public DblObjStreamlet<V> drop(int n) {
		return chrObjStreamlet(() -> spawn().drop(n));
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == DblObjStreamlet.class ? Equals.ab(spawn(), ((DblObjStreamlet<?>) object).spawn())
				: false;
	}

	public DblObjStreamlet<V> filter(DblObjPredicate<V> fun) {
		return chrObjStreamlet(() -> spawn().filter(fun));
	}

	public DblObjStreamlet<V> filterKey(DblPred fun) {
		return chrObjStreamlet(() -> spawn().filterKey(fun));
	}

	public DblObjStreamlet<V> filterValue(Predicate<V> fun) {
		return chrObjStreamlet(() -> spawn().filterValue(fun));
	}

	public DblObjPair<V> first() {
		return spawn().first();
	}

	public <O> Streamlet<O> flatMap(DblObj_Obj<V, Iterable<O>> fun) {
		return streamlet(() -> spawn().flatMap(fun));
	}

	public DblObjStreamlet<List<V>> groupBy() {
		return chrObjStreamlet(this::groupBy_);
	}

	public <V1> DblObjStreamlet<V1> groupBy(Fun<Streamlet<V>, V1> fun) {
		return chrObjStreamlet(() -> groupBy_().mapValue(list -> fun.apply(new Streamlet<>(() -> Puller.of(list)))));
	}

	@Override
	public int hashCode() {
		return spawn().hashCode();
	}

	@Override
	public Iterator<DblObjPair<V>> iterator() {
		return spawn().iterator();
	}

	public DblStreamlet keys() {
		return chrStreamlet(() -> spawn().keys());
	}

	public DblObjPair<V> last() {
		return spawn().last();
	}

	public <O> Streamlet<O> map(DblObj_Obj<V, O> fun) {
		return map_(fun);
	}

	public <V1> Streamlet2<Double, V1> map2(DblObj_Obj<V, V1> vf) {
		return map2_((k, v) -> k, vf);
	}

	public <K1, V1> Streamlet2<K1, V1> map2(DblObj_Obj<V, K1> kf, DblObj_Obj<V, V1> vf) {
		return map2_(kf, vf);
	}

	public <V1> DblObjStreamlet<V1> mapDblObj(DblObj_Dbl<V> kf, DblObj_Obj<V, V1> vf) {
		return mapDblObj_(kf, vf);
	}

	public <V1> DblObjStreamlet<V1> mapValue(Fun<V, V1> fun) {
		return chrObjStreamlet(() -> spawn().mapValue(fun));
	}

	public DblObjPair<V> min(Comparator<DblObjPair<V>> comparator) {
		return spawn().min(comparator);
	}

	public DblObjPair<V> minOrNull(Comparator<DblObjPair<V>> comparator) {
		return spawn().minOrNull(comparator);
	}

	public Streamlet<DblObjPair<V>> pairs() {
		return streamlet(() -> spawn().pairs());
	}

	public Pair<DblObjStreamlet<V>, DblObjStreamlet<V>> partition(DblObjPredicate<V> pred) {
		return Pair.of(filter(pred), filter((k, v) -> !pred.test(k, v)));
	}

	public DblObjPuller<V> puller() {
		return spawn();
	}

	public DblObjStreamlet<V> reverse() {
		return chrObjStreamlet(() -> spawn().reverse());
	}

	public DblObjStreamlet<V> skip(int n) {
		return chrObjStreamlet(() -> spawn().skip(n));
	}

	public DblObjStreamlet<V> snoc(double key, V value) {
		return chrObjStreamlet(() -> spawn().snoc(key, value));
	}

	public DblObjStreamlet<V> sort(Comparator<DblObjPair<V>> comparator) {
		return chrObjStreamlet(() -> spawn().sort(comparator));
	}

	public <O extends Comparable<? super O>> DblObjStreamlet<V> sortBy(DblObj_Obj<V, O> fun) {
		return chrObjStreamlet(() -> spawn().sortBy(fun));
	}

	public DblObjStreamlet<V> sortByKey(Comparator<Double> comparator) {
		return chrObjStreamlet(() -> spawn().sortByKey(comparator));
	}

	public DblObjStreamlet<V> sortByValue(Comparator<V> comparator) {
		return chrObjStreamlet(() -> spawn().sortByValue(comparator));
	}

	public DblObjStreamlet<V> take(int n) {
		return chrObjStreamlet(() -> spawn().take(n));
	}

	public DblObjPair<V>[] toArray() {
		return spawn().toArray();
	}

	public List<DblObjPair<V>> toList() {
		return toList_();
	}

	public DblObjMap<List<V>> toListMap() {
		return toListMap_();
	}

	public DblObjMap<V> toMap() {
		var source = spawn().source();
		var map = new DblObjMap<V>();
		var pair = DblObjPair.of(DblPrim.EMPTYVALUE, (V) null);
		while (source.source2(pair))
			map.put(pair.k, pair.v);
		return map;
	}

	public ListMultimap<Double, V> toMultimap() {
		var map = new ListMultimap<Double, V>();
		groupBy_().concatMapValue(Puller::of).sink(map::put);
		return map;
	}

	public ObjDblMap<V> toObjDblMap() {
		var source = spawn().source();
		var pair = DblObjPair.of(DblPrim.EMPTYVALUE, (V) null);
		var map = new ObjDblMap<V>();
		while (source.source2(pair))
			map.put(pair.v, pair.k);
		return map;
	}

	public Set<DblObjPair<V>> toSet() {
		return spawn().toSet();
	}

	public DblObjPair<V> uniqueResult() {
		var pair = spawn().opt();
		return pair.k != DblPrim.EMPTYVALUE ? pair : fail("no result");
	}

	public Streamlet<V> values() {
		return streamlet(() -> spawn().values());
	}

	private <T> Streamlet<T> concatMap_(DblObj_Obj<V, Streamlet<T>> fun) {
		DblObj_Obj<V, Puller<T>> bf = (k, v) -> fun.apply(k, v).puller();
		return streamlet(() -> Puller.of(spawn().concatMap(bf)));
	}

	private <V1, K1> Streamlet2<K1, V1> concatMap2_(DblObj_Obj<V, Streamlet2<K1, V1>> fun) {
		DblObj_Obj<V, Puller2<K1, V1>> bf = (k, v) -> fun.apply(k, v).puller();
		return streamlet2(() -> Puller2.of(spawn().concatMap2(bf)));
	}

	private <V1> DblObjStreamlet<V1> concatMapDblObj_(DblObj_Obj<V, DblObjStreamlet<V1>> fun) {
		DblObj_Obj<V, DblObjPuller<V1>> bf = (k, v) -> fun.apply(k, v).puller();
		return chrObjStreamlet(() -> DblObjPuller.of(spawn().concatMapDblObj(bf)));
	}

	private DblObjPuller<List<V>> groupBy_() {
		return DblObjPuller.of(toListMap_().source());
	}

	private <T> Streamlet<T> map_(DblObj_Obj<V, T> fun) {
		return streamlet(() -> spawn().map(fun));
	}

	private <K1, V1> Streamlet2<K1, V1> map2_(DblObj_Obj<V, K1> kf, DblObj_Obj<V, V1> vf) {
		return streamlet2(() -> spawn().map2(kf, vf));
	}

	private <V1> DblObjStreamlet<V1> mapDblObj_(DblObj_Dbl<V> kf, DblObj_Obj<V, V1> vf) {
		return chrObjStreamlet(() -> spawn().mapDblObj(kf, vf));
	}

	private List<DblObjPair<V>> toList_() {
		return spawn().toList();
	}

	private DblObjMap<List<V>> toListMap_() {
		var source = spawn().source();
		var map = new DblObjMap<List<V>>();
		var pair = DblObjPair.of(DblPrim.EMPTYVALUE, (V) null);
		while (source.source2(pair))
			map.computeIfAbsent(pair.k, k_ -> new ArrayList<>()).add(pair.v);
		return map;
	}

	private DblObjPuller<V> spawn() {
		return in.g();
	}

	private static DblStreamlet chrStreamlet(Source<DblPuller> in) {
		return new DblStreamlet(in);
	}

	private static <V> DblObjStreamlet<V> chrObjStreamlet(Source<DblObjPuller<V>> in) {
		return new DblObjStreamlet<>(in);
	}

	private static <K, V> Streamlet2<K, V> streamlet2(Source<Puller2<K, V>> in) {
		return new Streamlet2<>(in);
	}

	private static <T> Streamlet<T> streamlet(Source<Puller<T>> in) {
		return new Streamlet<>(in);
	}

}
