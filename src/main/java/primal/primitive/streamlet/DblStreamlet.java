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
import primal.primitive.DblOpt;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.DblComparator;
import primal.primitive.DblPrim.DblObjPair_;
import primal.primitive.DblPrim.DblObjSource;
import primal.primitive.DblPrim.DblObj_Obj;
import primal.primitive.DblPrim.DblPred;
import primal.primitive.DblPrim.DblSink;
import primal.primitive.DblPrim.DblSource;
import primal.primitive.DblPrim.Dbl_Obj;
import primal.primitive.Dbl_Dbl;
import primal.primitive.adt.Doubles;
import primal.primitive.adt.Doubles.DoublesBuilder;
import primal.primitive.adt.map.DblObjMap;
import primal.primitive.adt.map.ObjDblMap;
import primal.primitive.adt.set.DblSet;
import primal.primitive.puller.DblObjPuller;
import primal.primitive.puller.DblPuller;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.streamlet.Streamlet;
import primal.streamlet.Streamlet2;
import primal.streamlet.StreamletDefaults;

public class DblStreamlet implements StreamletDefaults<Double, DblOpt, DblPred, DblPuller, DblSink, DblSource> {

	private Source<DblPuller> in;

	public DblStreamlet(Source<DblPuller> in) {
		this.in = in;
	}

	public <R> R apply(Fun<DblStreamlet, R> fun) {
		return fun.apply(this);
	}

	public double average() {
		return spawn().average();
	}

	public Streamlet<DblPuller> chunk(int n) {
		return streamlet(() -> spawn().chunk(n));
	}

	public DblStreamlet closeAtEnd(Closeable c) {
		return doubleStreamlet(() -> {
			var in = spawn();
			in.closeAtEnd(c);
			return in;
		});
	}

	public <O> Streamlet<O> concatMap(Dbl_Obj<Streamlet<O>> fun) {
		return concatMap_(fun);
	}

	public <K, V> Streamlet2<K, V> concatMap2(Dbl_Obj<Streamlet2<K, V>> fun) {
		return concatMap2_(fun);
	}

	public DblStreamlet cons(double c) {
		return doubleStreamlet(() -> spawn().cons(c));
	}

	public DblStreamlet collect() {
		var doubles = toList_();
		return doubleStreamlet(() -> DblPuller.of(doubles.cs, doubles.start, doubles.end, 1));
	}

	public <U, O> Streamlet<O> cross(Streamlet<U> st1, DblObj_Obj<U, O> fun) {
		return streamlet(() -> spawn().cross(st1.toList(), fun));
	}

	public DblStreamlet distinct() {
		return doubleStreamlet(() -> spawn().distinct());
	}

	public DblStreamlet drop(int n) {
		return doubleStreamlet(() -> spawn().drop(n));
	}

	public DblStreamlet dropWhile(DblPred fun) {
		return doubleStreamlet(() -> spawn().dropWhile(fun));
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == DblStreamlet.class ? Equals.ab(spawn(), ((DblStreamlet) object).spawn()) : false;
	}

	public DblStreamlet filter(DblPred fun) {
		return doubleStreamlet(() -> spawn().filter(fun));
	}

	public double first() {
		return spawn().first();
	}

	public <O> Streamlet<O> flatMap(Dbl_Obj<Iterable<O>> fun) {
		return streamlet(() -> spawn().flatMap(fun));
	}

	public <R> R fold(R init, DblObj_Obj<R, R> fun) {
		return spawn().fold(init, fun);
	}

	public <U, V, W> W forkJoin(Fun<DblStreamlet, U> fork0, Fun<DblStreamlet, V> fork1, Fun2<U, V, W> join) {
		return join.apply(fork0.apply(this), fork1.apply(this));
	}

	public <V> DblObjStreamlet<DoublesBuilder> groupBy() {
		return doubleObjStreamlet(this::groupBy_);
	}

	public <V> DblObjStreamlet<V> groupBy(Fun<DblStreamlet, V> fun) {
		return new DblObjStreamlet<V>(() -> groupBy_().mapValue(list -> fun.apply(doubleStreamlet(()-> list.toDoubles().puller()))));
	}

	@Override
	public int hashCode() {
		return spawn().hashCode();
	}

	public DblObjStreamlet<Integer> index() {
		return doubleObjStreamlet(() -> DblObjPuller.of(new DblObjSource<>() {
			private DblPuller puller = spawn();
			private int i = 0;

			public boolean source2(DblObjPair_<Integer> pair) {
				var c = puller.pull();
				if (c != DblPrim.EMPTYVALUE) {
					pair.update(c, i++);
					return true;
				} else
					return false;
			}
		}));
	}

	@Override
	public Iterator<Double> iterator() {
		return spawn().iterator();
	}

	public <O> Streamlet2<Double, O> join2(Streamlet<O> streamlet) {
		return concatMap2_(t -> streamlet.map2(v -> t, v -> v));
	}

	public double last() {
		return spawn().last();
	}

	public <O> Streamlet<O> map(Dbl_Obj<O> fun) {
		return map_(fun);
	}

	public <K, V> Streamlet2<K, V> map2(Dbl_Obj<K> kf, Dbl_Obj<V> vf) {
		return map2_(kf, vf);
	}

	public DblStreamlet mapDbl(Dbl_Dbl fun) {
		return doubleStreamlet(() -> spawn().mapDbl(fun));
	}

	public <K, V> DblObjStreamlet<V> mapDblObj(Dbl_Obj<V> fun0) {
		return doubleObjStreamlet(() -> spawn().mapDblObj(fun0));
	}

	public double max() {
		return spawn().min((c0, c1) -> Double.compare(c1, c0));
	}

	public double min() {
		return spawn().min((c0, c1) -> Double.compare(c0, c1));
	}

	public double min(DblComparator comparator) {
		return spawn().min(comparator);
	}

	public double minOrEmpty(DblComparator comparator) {
		return spawn().minOrEmpty(comparator);
	}

	public Pair<DblStreamlet, DblStreamlet> partition(DblPred pred) {
		return Pair.of(filter(pred), filter(t -> !pred.test(t)));
	}

	public DblPuller puller() {
		return spawn();
	}

	public DblStreamlet reverse() {
		return doubleStreamlet(() -> spawn().reverse());
	}

	public DblStreamlet skip(int n) {
		return doubleStreamlet(() -> spawn().skip(n));
	}

	public DblStreamlet snoc(double c) {
		return doubleStreamlet(() -> spawn().snoc(c));
	}

	public DblStreamlet sort() {
		return doubleStreamlet(() -> spawn().sort());
	}

	public double sum() {
		return spawn().sum();
	}

	public DblStreamlet take(int n) {
		return doubleStreamlet(() -> spawn().take(n));
	}

	public DblStreamlet takeWhile(DblPred fun) {
		return doubleStreamlet(() -> spawn().takeWhile(fun));
	}

	public double[] toArray() {
		return spawn().toArray();
	}

	public Doubles toList() {
		return toList_();
	}

	public <K> DblObjMap<DoublesBuilder> toListMap() {
		return toListMap_();
	}

	public <K> DblObjMap<DoublesBuilder> toListMap(Dbl_Dbl valueFun) {
		return toListMap_(valueFun);
	}

	public <K> ObjDblMap<K> toMap(Dbl_Obj<K> kf0) {
		var puller = spawn();
		var kf1 = kf0.rethrow();
		var map = new ObjDblMap<K>();
		double c;
		while ((c = puller.pull()) != DblPrim.EMPTYVALUE)
			map.put(kf1.apply(c), c);
		return map;
	}

	public <K, V> Map<K, V> toMap(Dbl_Obj<K> keyFun, Dbl_Obj<V> valueFun) {
		return spawn().toMap(keyFun, valueFun);
	}

	public DblSet toSet() {
		var puller = spawn();
		var set = new DblSet();
		double c;
		while ((c = puller.pull()) != DblPrim.EMPTYVALUE)
			set.add(c);
		return set;
	}

	public <K, V> Map<K, Set<V>> toSetMap(Dbl_Obj<K> keyFun, Dbl_Obj<V> valueFun) {
		return spawn().map2(keyFun, valueFun).groupBy().mapValue(values -> streamlet(values).toSet()).toMap();
	}

	public double uniqueResult() {
		return spawn().opt().g();
	}

	public <U, V> Streamlet<V> zip(Iterable<U> list1, DblObj_Obj<U, V> fun) {
		return streamlet(() -> spawn().zip(Puller.of(list1), fun));
	}

	private <O> Streamlet<O> concatMap_(Dbl_Obj<Streamlet<O>> fun) {
		return streamlet(() -> spawn().concatMap(t -> fun.apply(t).puller()));
	}

	private <K, V> Streamlet2<K, V> concatMap2_(Dbl_Obj<Streamlet2<K, V>> fun) {
		return streamlet2(() -> spawn().concatMap2(t -> fun.apply(t).puller()));
	}

	private <V> DblObjPuller<DoublesBuilder> groupBy_() {
		return DblObjPuller.of(toListMap_().source());
	}

	private <O> Streamlet<O> map_(Dbl_Obj<O> fun) {
		return streamlet(() -> spawn().map(fun));
	}

	private <K, V> Streamlet2<K, V> map2_(Dbl_Obj<K> kf, Dbl_Obj<V> vf) {
		return streamlet2(() -> spawn().map2(kf, vf));
	}

	private Doubles toList_() {
		var list = spawn().toList();
		return Doubles.of(list.cs, 0, list.size);
	}

	private DblObjMap<DoublesBuilder> toListMap_() {
		return toListMap_(value -> value);
	}

	private DblObjMap<DoublesBuilder> toListMap_(Dbl_Dbl valueFun) {
		var puller = spawn();
		var map = new DblObjMap<DoublesBuilder>();
		double c;
		while ((c = puller.pull()) != DblPrim.EMPTYVALUE)
			map.computeIfAbsent(c, k_ -> new DoublesBuilder()).append(valueFun.apply(c));
		return map;
	}

	private DblPuller spawn() {
		return in.g();
	}

	private static <T> Streamlet<T> streamlet(Iterable<T> col) {
		return streamlet(() -> Puller.of(col));
	}

	private static DblStreamlet doubleStreamlet(Source<DblPuller> in) {
		return new DblStreamlet(in);
	}

	private static <V> DblObjStreamlet<V> doubleObjStreamlet(Source<DblObjPuller<V>> in) {
		return new DblObjStreamlet<>(in);
	}

	private static <K, V> Streamlet2<K, V> streamlet2(Source<Puller2<K, V>> in) {
		return new Streamlet2<>(in);
	}

	private static <T> Streamlet<T> streamlet(Source<Puller<T>> in) {
		return new Streamlet<>(in);
	}

}
