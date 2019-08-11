package primal.streamlet;

import static primal.statics.Fail.fail;

import java.io.Closeable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.Verbs.Take;
import primal.adt.Fixie_.FixieFun3;
import primal.adt.Pair;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.FunUtil2;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.fp.Funs2.Fun2;
import primal.fp.Funs2.Sink2;
import primal.fp.Funs2.Source2;
import primal.puller.Puller;
import primal.puller.Puller2;

public class Streamlet2<K, V> implements StreamletDefaults<Pair<K, V>, Pair<K, V>, BiPredicate<K, V>, Puller2<K, V>, Sink2<K, V>, Source2<K, V>> {

	private Source<Puller2<K, V>> in;

	@SafeVarargs
	public static <K, V> Streamlet2<K, V> concat(Streamlet2<K, V>... streamlets) {
		return streamlet2(() -> {
			var source = Take.from(streamlets);
			return Puller2.of(FunUtil2.concat(FunUtil.map(st -> st.spawn().source(), source)));
		});
	}

	public Streamlet2(Source<Puller2<K, V>> in) {
		this.in = in;
	}

	public <T> T apply(Fun<Streamlet2<K, V>, T> fun) {
		return fun.apply(this);
	}

	public Streamlet<Puller2<K, V>> chunk(int n) {
		return streamlet(() -> spawn().chunk(n));
	}

	public Streamlet2<K, V> closeAtEnd(Closeable c) {
		return streamlet2(() -> {
			var in = spawn();
			in.closeAtEnd(c);
			return in;
		});
	}

	public Streamlet2<K, V> collect() {
		return streamlet2(toList_());
	}

	public <O> Streamlet<O> concatMap(Fun2<K, V, Streamlet<O>> fun) {
		return concatMap_(fun);
	}

	public <K1, V1> Streamlet2<K1, V1> concatMap2(Fun2<K, V, Streamlet2<K1, V1>> fun) {
		return concatMap2_(fun);
	}

	public <V1> Streamlet2<K, V1> concatMapValue(Fun<V, Streamlet<V1>> fun) {
		Fun<V, Puller<V1>> f = v -> fun.apply(v).puller();
		return streamlet2(() -> Puller2.of(spawn().concatMapValue(f)));
	}

	public Streamlet2<K, V> cons(K key, V value) {
		return cons_(key, value);
	}

	public Streamlet2<K, V> cons(Pair<K, V> pair) {
		return pair.map(this::cons_);
	}

	public Streamlet2<K, V> distinct() {
		return streamlet2(() -> spawn().distinct());
	}

	public Streamlet2<K, V> drop(int n) {
		return streamlet2(() -> spawn().drop(n));
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == Streamlet2.class ? Equals.ab(spawn(), ((Streamlet2<?, ?>) object).spawn()) : false;
	}

	public Streamlet2<K, V> filter(BiPredicate<K, V> fun) {
		return streamlet2(() -> spawn().filter(fun));
	}

	public Streamlet2<K, V> filterKey(Predicate<K> fun) {
		return streamlet2(() -> spawn().filterKey(fun));
	}

	public Streamlet2<K, V> filterValue(Predicate<V> fun) {
		return streamlet2(() -> spawn().filterValue(fun));
	}

	public Pair<K, V> first() {
		return spawn().first();
	}

	public <O> Streamlet<O> flatMap(Fun2<K, V, Iterable<O>> fun) {
		return streamlet(() -> spawn().flatMap(fun));
	}

	public <R> R fold(R init, FixieFun3<R, K, V, R> fun) {
		return spawn().fold(init, fun);
	}

	public Streamlet2<K, List<V>> groupBy() {
		return streamlet2(() -> spawn().groupBy());
	}

	public <V1> Streamlet2<K, V1> groupBy(Fun<Streamlet<V>, V1> fun) {
		return streamlet2(() -> spawn().groupBy().mapValue(list -> fun.apply(new Streamlet<>(() -> Puller.of(list)))));
	}

	@Override
	public int hashCode() {
		return spawn().hashCode();
	}

	@Override
	public Iterator<Pair<K, V>> iterator() {
		return spawn().iterator();
	}

	public Streamlet<K> keys() {
		return streamlet(() -> spawn().keys());
	}

	public Pair<K, V> last() {
		return spawn().last();
	}

	public <O> Streamlet<O> map(Fun2<K, V, O> fun) {
		return map_(fun);
	}

	public <V1> Streamlet2<K, V1> map2(Fun2<K, V, V1> vf) {
		return map2_((k, v) -> k, vf);
	}

	public <K1, V1> Streamlet2<K1, V1> map2(Fun2<K, V, K1> kf, Fun2<K, V, V1> vf) {
		return map2_(kf, vf);
	}

	public <V1> Streamlet2<K, V1> mapValue(Fun<V, V1> fun) {
		return streamlet2(() -> spawn().mapValue(fun));
	}

	public Pair<K, V> min(Comparator<Pair<K, V>> comparator) {
		return spawn().min(comparator);
	}

	public Pair<K, V> minOrNull(Comparator<Pair<K, V>> comparator) {
		return spawn().minOrNull(comparator);
	}

	public Streamlet<Pair<K, V>> pairs() {
		return streamlet(() -> spawn().pairs());
	}

	public Pair<Streamlet2<K, V>, Streamlet2<K, V>> partition(BiPredicate<K, V> pred) {
		return Pair.of(filter(pred), filter(pred.negate()));
	}

	public Puller2<K, V> puller() {
		return spawn();
	}

	public Streamlet2<K, V> reverse() {
		return streamlet2(() -> spawn().reverse());
	}

	public Streamlet2<K, V> skip(int n) {
		return streamlet2(() -> spawn().skip(n));
	}

	public Streamlet2<K, V> snoc(K key, V value) {
		return streamlet2(() -> spawn().snoc(key, value));
	}

	public Streamlet2<K, V> sort(Comparator<Pair<K, V>> comparator) {
		return streamlet2(() -> spawn().sort(comparator));
	}

	public <O extends Comparable<? super O>> Streamlet2<K, V> sortBy(Fun2<K, V, O> fun) {
		return streamlet2(() -> spawn().sortBy(fun));
	}

	public Streamlet2<K, V> sortByKey(Comparator<K> comparator) {
		return streamlet2(() -> spawn().sortByKey(comparator));
	}

	public Streamlet2<K, V> sortByValue(Comparator<V> comparator) {
		return streamlet2(() -> spawn().sortByValue(comparator));
	}

	public Streamlet2<K, V> take(int n) {
		return streamlet2(() -> spawn().take(n));
	}

	public Pair<K, V>[] toArray() {
		return spawn().toArray();
	}

	public List<Pair<K, V>> toList() {
		return toList_();
	}

	public Map<K, List<V>> toListMap() {
		return spawn().toListMap();
	}

	public Map<K, V> toMap() {
		return spawn().toMap();
	}

	public ListMultimap<K, V> toMultimap() {
		var map = new ListMultimap<K, V>();
		spawn().groupBy().concatMapValue(Puller::of).sink(map::put);
		return map;
	}

	public Set<Pair<K, V>> toSet() {
		return spawn().toSet();
	}

	public Map<K, Set<V>> toSetMap() {
		return spawn().groupBy().mapValue(values -> streamlet(values).toSet()).toMap();
	}

	public Pair<K, V> uniqueResult() {
		var pair = spawn().opt();
		return pair.k != null ? pair : fail("no result");
	}

	public Streamlet<V> values() {
		return streamlet(() -> spawn().values());
	}

	private <T> Streamlet<T> concatMap_(Fun2<K, V, Streamlet<T>> fun) {
		Fun2<K, V, Puller<T>> bf = (k, v) -> fun.apply(k, v).puller();
		return streamlet(() -> Puller.of(spawn().concatMap(bf)));
	}

	private <K1, V1> Streamlet2<K1, V1> concatMap2_(Fun2<K, V, Streamlet2<K1, V1>> fun) {
		Fun2<K, V, Puller2<K1, V1>> bf = (k, v) -> fun.apply(k, v).puller();
		return streamlet2(() -> Puller2.of(spawn().concatMap2(bf)));
	}

	private Streamlet2<K, V> cons_(K key, V value) {
		return streamlet2(() -> spawn().cons(key, value));
	}

	private <T> Streamlet<T> map_(Fun2<K, V, T> fun) {
		return streamlet(() -> spawn().map(fun));
	}

	private <K1, V1> Streamlet2<K1, V1> map2_(Fun2<K, V, K1> kf, Fun2<K, V, V1> vf) {
		return streamlet2(() -> spawn().map2(kf, vf));
	}

	private List<Pair<K, V>> toList_() {
		return spawn().toList();
	}

	private Puller2<K, V> spawn() {
		return in.g();
	}

	private static <T> Streamlet<T> streamlet(Iterable<T> col) {
		return streamlet(() -> Puller.of(col));
	}

	private static <K, V> Streamlet2<K, V> streamlet2(Iterable<Pair<K, V>> col) {
		return streamlet2(() -> Puller2.of(col));
	}

	private static <K, V> Streamlet2<K, V> streamlet2(Source<Puller2<K, V>> in) {
		return new Streamlet2<>(in);
	}

	private static <T> Streamlet<T> streamlet(Source<Puller<T>> in) {
		return new Streamlet<>(in);
	}

}
