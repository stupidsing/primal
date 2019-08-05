package primal.primitive.puller;

import static java.lang.Math.max;
import static primal.statics.Fail.fail;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;

import primal.NullableSyncQueue;
import primal.Verbs.Close;
import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.Verbs.Take;
import primal.adt.Mutable;
import primal.adt.Pair;
import primal.fp.FunUtil;
import primal.fp.FunUtil2;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.DblOpt;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.DblComparator;
import primal.primitive.DblPrim.DblObj_Obj;
import primal.primitive.DblPrim.DblPred;
import primal.primitive.DblPrim.DblSink;
import primal.primitive.DblPrim.DblSource;
import primal.primitive.DblPrim.Dbl_Obj;
import primal.primitive.Dbl_Dbl;
import primal.primitive.fp.DblFunUtil;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.puller.PullerDefaults;

public class DblPuller implements PullerDefaults<Double, DblOpt, DblPred, DblSink, DblSource> {

	private static double empty = DblPrim.EMPTYVALUE;

	private DblSource source;

	@SafeVarargs
	public static DblPuller concat(DblPuller... outlets) {
		var sources = new ArrayList<DblSource>();
		for (var outlet : outlets)
			sources.add(outlet.source);
		return of(DblFunUtil.concat(Take.from(sources)));
	}

	public static DblPuller empty() {
		return of(DblFunUtil.nullSource());
	}

	@SafeVarargs
	public static DblPuller of(double... ts) {
		return of(ts, 0, ts.length, 1);
	}

	public static DblPuller of(double[] ts, int start, int end, int inc) {
		IntPredicate pred = 0 < inc ? i -> i < end : i -> end < i;

		return of(new DblSource() {
			private int i = start;

			public double g() {
				var c = pred.test(i) ? ts[i] : empty;
				i += inc;
				return c;
			}
		});
	}

	public static DblPuller of(Enumeration<Double> en) {
		return of(Take.from(en));
	}

	public static DblPuller of(Iterable<Double> col) {
		return of(Take.from(col));
	}

	public static DblPuller of(Source<Double> source) {
		return DblPuller.of(() -> {
			var c = source.g();
			return c != null ? c : empty;
		});
	}

	public static DblPuller of(DblSource source) {
		return new DblPuller(source);
	}

	private DblPuller(DblSource source) {
		this.source = source;
	}

	public double average() {
		var count = 0;
		double result = 0, c1;
		while ((c1 = pull()) != empty) {
			result += c1;
			count++;
		}
		return (double) (result / count);
	}

	public Puller<DblPuller> chunk(int n) {
		return Puller.of(FunUtil.map(DblPuller::new, DblFunUtil.chunk(n, source)));
	}

	public DblPuller closeAtEnd(Closeable c) {
		return of(() -> {
			var next = pull();
			if (next == empty)
				Close.quietly(c);
			return next;
		});
	}

	public <R> R collect(Fun<DblPuller, R> fun) {
		return fun.apply(this);
	}

	public <O> Puller<O> concatMap(Dbl_Obj<Puller<O>> fun) {
		return Puller.of(FunUtil.concat(DblFunUtil.map(t -> fun.apply(t).source(), source)));
	}

	public <K, V> Puller2<K, V> concatMap2(Dbl_Obj<Puller2<K, V>> fun) {
		return Puller2.of(FunUtil2.concat(DblFunUtil.map(t -> fun.apply(t).source(), source)));
	}

	public DblPuller concatMapDbl(Dbl_Obj<DblPuller> fun) {
		return of(DblFunUtil.concat(DblFunUtil.map(t -> fun.apply(t).source, source)));
	}

	public DblPuller cons(double c) {
		return of(DblFunUtil.cons(c, source));
	}

	public int count() {
		var i = 0;
		while (pull() != empty)
			i++;
		return i;
	}

	public <U, O> Puller<O> cross(List<U> list, DblObj_Obj<U, O> fun) {
		return Puller.of(new Source<>() {
			private double c;
			private int index = list.size();

			public O g() {
				if (index == list.size()) {
					index = 0;
					c = pull();
				}
				return fun.apply(c, list.get(index++));
			}
		});
	}

	public DblPuller distinct() {
		var set = new HashSet<>();
		return of(() -> {
			double c;
			while ((c = pull()) != empty && !set.add(c))
				;
			return c;
		});
	}

	public DblPuller drop(int n) {
		var isAvailable = true;
		while (0 < n && (isAvailable &= pull() != empty))
			n--;
		return isAvailable ? this : empty();
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == DblPuller.class) {
			var source1 = ((DblPuller) object).source;
			double o0, o1;
			while (Equals.ab(o0 = source.g(), o1 = source1.g()))
				if (o0 == empty && o1 == empty)
					return true;
			return false;
		} else
			return false;
	}

	public DblPuller filter(DblPred fun) {
		return of(DblFunUtil.filter(fun, source));
	}

	public double first() {
		return pull();
	}

	public <O> Puller<O> flatMap(Dbl_Obj<Iterable<O>> fun) {
		return Puller.of(FunUtil.flatten(DblFunUtil.map(fun, source)));
	}

	public <R> R fold(R init, DblObj_Obj<R, R> fun) {
		double c;
		while ((c = pull()) != empty)
			init = fun.apply(c, init);
		return init;
	}

	@Override
	public int hashCode() {
		var h = 7;
		double c;
		while ((c = source.g()) != empty)
			h = h * 31 + Objects.hashCode(c);
		return h;
	}

	public boolean isAll(DblPred pred) {
		return DblFunUtil.isAll(pred, source);
	}

	public boolean isAny(DblPred pred) {
		return DblFunUtil.isAny(pred, source);
	}

	@Override
	public Iterator<Double> iterator() {
		return DblFunUtil.iterator(source);
	}

	public double last() {
		double c, c1 = empty;
		while ((c = pull()) != empty)
			c1 = c;
		return c1;
	}

	public <O> Puller<O> map(Dbl_Obj<O> fun) {
		return Puller.of(DblFunUtil.map(fun, source));
	}

	public <K, V> Puller2<K, V> map2(Dbl_Obj<K> kf0, Dbl_Obj<V> vf0) {
		return Puller2.of(DblFunUtil.map2(kf0, vf0, source));
	}

	public DblPuller mapDbl(Dbl_Dbl fun0) {
		return of(DblFunUtil.mapDbl(fun0, source));
	}

	public <V> DblObjPuller<V> mapDblObj(Dbl_Obj<V> fun0) {
		return DblObjPuller.of(DblFunUtil.mapDblObj(fun0, source));
	}

	public double min(DblComparator comparator) {
		var c = minOrEmpty(comparator);
		if (c != empty)
			return c;
		else
			return fail("no result");
	}

	public double minOrEmpty(DblComparator comparator) {
		double c = pull(), c1;
		if (c != empty) {
			while ((c1 = pull()) != empty)
				if (0 < comparator.compare(c, c1))
					c = c1;
			return c;
		} else
			return empty;
	}

	public DblPuller nonBlock(double c0) {
		var queue = new NullableSyncQueue<Double>();

		new Thread(() -> {
			double c;
			do
				queue.offerQuietly(c = source.g());
			while (c != empty);
		}).start();

		return new DblPuller(() -> {
			var mutable = Mutable.<Double> nil();
			var c = queue.poll(mutable) ? mutable.value() : c0;
			return c;
		});
	}

	public DblOpt opt() {
		var c = pull();
		if (c != empty)
			return pull() == empty ? DblOpt.of(c) : fail("more than one result");
		else
			return DblOpt.none();
	}

	public Pair<DblPuller, DblPuller> partition(DblPred pred) {
		return Pair.of(filter(pred), filter(c -> !pred.test(c)));
	}

	public double pull() {
		return source.g();
	}

	public DblPuller reverse() {
		var list = toList();
		return DblPuller.of(list.cs, list.size - 1, -1, -1);
	}

	public void sink(DblSink sink0) {
		var sink1 = sink0.rethrow();
		double c;
		while ((c = pull()) != empty)
			sink1.f(c);
	}

	public DblPuller skip(int n) {
		var end = false;
		for (var i = 0; !end && i < n; i++)
			end = pull() == empty;
		return !end ? of(source) : empty();
	}

	public DblPuller snoc(double c) {
		return of(DblFunUtil.snoc(c, source));
	}

	public DblSource source() {
		return source;
	}

	public DblPuller sort() {
		var array = toArray();
		Arrays.sort(array);
		return DblPuller.of(array);
	}

	public Puller<DblPuller> split(DblPred fun) {
		return Puller.of(FunUtil.map(DblPuller::new, DblFunUtil.split(fun, source)));
	}

	public double sum() {
		double result = 0, c1;
		while ((c1 = pull()) != empty)
			result += c1;
		return result;
	}

	public DblPuller take(int n) {
		return of(new DblSource() {
			private int count = n;

			public double g() {
				return 0 < count-- ? pull() : null;
			}
		});
	}

	public double[] toArray() {
		var list = toList();
		return Arrays.copyOf(list.cs, list.size);
	}

	public Builder toList() {
		var list = new Builder();
		double c;
		while ((c = pull()) != empty)
			list.append(c);
		return list;
	}

	public <K, V> Map<K, V> toMap(Dbl_Obj<K> kf0, Dbl_Obj<V> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		var map = new HashMap<K, V>();
		double c;
		while ((c = pull()) != empty) {
			var key = kf1.apply(c);
			if (map.put(key, vf1.apply(c)) != null)
				fail("duplicate key " + key);
		}
		return map;
	}

	public <U, R> Puller<R> zip(Puller<U> outlet1, DblObj_Obj<U, R> fun) {
		return Puller.of(() -> {
			var t = pull();
			var u = outlet1.pull();
			return t != empty && u != null ? fun.apply(t, u) : null;
		});
	}

	public static class Builder {
		public double[] cs = emptyArray;
		public int size;

		private Builder append(double c) {
			extendBuffer(size + 1);
			cs[size++] = c;
			return this;
		}

		private void extendBuffer(int capacity1) {
			var capacity0 = cs.length;

			if (capacity0 < capacity1) {
				int capacity = max(capacity0, 4);
				while (capacity < capacity1)
					capacity = capacity < 4096 ? capacity << 1 : capacity * 3 / 2;

				cs = Arrays.copyOf(cs, capacity);
			}
		}
	}

	private static double[] emptyArray = new double[0];

}
