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

import primal.Nouns.Buffer;
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
import primal.primitive.FltOpt;
import primal.primitive.FltPrim;
import primal.primitive.FltPrim.FltComparator;
import primal.primitive.FltPrim.FltObj_Obj;
import primal.primitive.FltPrim.FltPred;
import primal.primitive.FltPrim.FltSink;
import primal.primitive.FltPrim.FltSource;
import primal.primitive.FltPrim.Flt_Obj;
import primal.primitive.Flt_Flt;
import primal.primitive.fp.FltFunUtil;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.puller.PullerDefaults;

public class FltPuller implements PullerDefaults<Float, FltOpt, FltPred, FltSink, FltSource> {

	private static float empty = FltPrim.EMPTYVALUE;

	private FltSource source;

	@SafeVarargs
	public static FltPuller concat(FltPuller... outlets) {
		var sources = new ArrayList<FltSource>();
		for (var outlet : outlets)
			sources.add(outlet.source);
		return of(FltFunUtil.concat(Take.from(sources)));
	}

	public static FltPuller empty() {
		return of(FltFunUtil.nullSource());
	}

	@SafeVarargs
	public static FltPuller of(float... ts) {
		return of(ts, 0, ts.length, 1);
	}

	public static FltPuller of(float[] ts, int start, int end, int inc) {
		IntPredicate pred = 0 < inc ? i -> i < end : i -> end < i;

		return of(new FltSource() {
			private int i = start;

			public float g() {
				var c = pred.test(i) ? ts[i] : empty;
				i += inc;
				return c;
			}
		});
	}

	public static FltPuller of(Enumeration<Float> en) {
		return of(Take.from(en));
	}

	public static FltPuller of(Iterable<Float> col) {
		return of(Take.from(col));
	}

	public static FltPuller of(Source<Float> source) {
		return FltPuller.of(() -> {
			var c = source.g();
			return c != null ? c : empty;
		});
	}

	public static FltPuller of(FltSource source) {
		return new FltPuller(source);
	}

	private FltPuller(FltSource source) {
		this.source = source;
	}

	public float average() {
		var count = 0;
		float result = 0, c1;
		while ((c1 = pull()) != empty) {
			result += c1;
			count++;
		}
		return (float) (result / count);
	}

	public Puller<FltPuller> chunk(int n) {
		return Puller.of(FunUtil.map(FltPuller::new, FltFunUtil.chunk(n, source)));
	}

	public FltPuller closeAtEnd(Closeable c) {
		return of(() -> {
			var next = pull();
			if (next == empty)
				Close.quietly(c);
			return next;
		});
	}

	public <R> R collect(Fun<FltPuller, R> fun) {
		return fun.apply(this);
	}

	public <O> Puller<O> concatMap(Flt_Obj<Puller<O>> fun) {
		return Puller.of(FunUtil.concat(FltFunUtil.map(t -> fun.apply(t).source(), source)));
	}

	public <K, V> Puller2<K, V> concatMap2(Flt_Obj<Puller2<K, V>> fun) {
		return Puller2.of(FunUtil2.concat(FltFunUtil.map(t -> fun.apply(t).source(), source)));
	}

	public FltPuller concatMapFlt(Flt_Obj<FltPuller> fun) {
		return of(FltFunUtil.concat(FltFunUtil.map(t -> fun.apply(t).source, source)));
	}

	public FltPuller cons(float c) {
		return of(FltFunUtil.cons(c, source));
	}

	public int count() {
		var i = 0;
		while (pull() != empty)
			i++;
		return i;
	}

	public <U, O> Puller<O> cross(List<U> list, FltObj_Obj<U, O> fun) {
		return Puller.of(new Source<>() {
			private float c;
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

	public FltPuller distinct() {
		var set = new HashSet<>();
		return of(() -> {
			float c;
			while ((c = pull()) != empty && !set.add(c))
				;
			return c;
		});
	}

	public FltPuller drop(int n) {
		var isAvailable = true;
		while (0 < n && (isAvailable &= pull() != empty))
			n--;
		return isAvailable ? this : empty();
	}

	public FltPuller dropWhile(FltPred fun) {
		return of(new FltSource() {
			private boolean b = false;

			public float g() {
				float t;
				return (t = pull()) != empty && (b |= !fun.test(t)) ? t : empty;
			}
		});
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == FltPuller.class) {
			var source1 = ((FltPuller) object).source;
			float o0, o1;
			while (Equals.ab(o0 = source.g(), o1 = source1.g()))
				if (o0 == empty && o1 == empty)
					return true;
			return false;
		} else
			return false;
	}

	public FltPuller filter(FltPred fun) {
		return of(FltFunUtil.filter(fun, source));
	}

	public float first() {
		return pull();
	}

	public <O> Puller<O> flatMap(Flt_Obj<Iterable<O>> fun) {
		return Puller.of(FunUtil.flatten(FltFunUtil.map(fun, source)));
	}

	public <R> R fold(R init, FltObj_Obj<R, R> fun) {
		float c;
		while ((c = pull()) != empty)
			init = fun.apply(c, init);
		return init;
	}

	@Override
	public int hashCode() {
		var h = 7;
		float c;
		while ((c = source.g()) != empty)
			h = h * 31 + Objects.hashCode(c);
		return h;
	}

	public boolean isAll(FltPred pred) {
		return FltFunUtil.isAll(pred, source);
	}

	public boolean isAny(FltPred pred) {
		return FltFunUtil.isAny(pred, source);
	}

	@Override
	public Iterator<Float> iterator() {
		return FltFunUtil.iterator(source);
	}

	public float last() {
		float c, c1 = empty;
		while ((c = pull()) != empty)
			c1 = c;
		return c1;
	}

	public <O> Puller<O> map(Flt_Obj<O> fun) {
		return Puller.of(FltFunUtil.map(fun, source));
	}

	public <K, V> Puller2<K, V> map2(Flt_Obj<K> kf0, Flt_Obj<V> vf0) {
		return Puller2.of(FltFunUtil.map2(kf0, vf0, source));
	}

	public FltPuller mapFlt(Flt_Flt fun0) {
		return of(FltFunUtil.mapFlt(fun0, source));
	}

	public <V> FltObjPuller<V> mapFltObj(Flt_Obj<V> fun0) {
		return FltObjPuller.of(FltFunUtil.mapFltObj(fun0, source));
	}

	public float min(FltComparator comparator) {
		var c = minOrEmpty(comparator);
		if (c != empty)
			return c;
		else
			return fail("no result");
	}

	public float minOrEmpty(FltComparator comparator) {
		float c = pull(), c1;
		if (c != empty) {
			while ((c1 = pull()) != empty)
				if (0 < comparator.compare(c, c1))
					c = c1;
			return c;
		} else
			return empty;
	}

	public FltPuller nonBlock(float c0) {
		var queue = new NullableSyncQueue<Float>();

		new Thread(() -> {
			float c;
			do
				queue.offerQuietly(c = source.g());
			while (c != empty);
		}).start();

		return new FltPuller(() -> {
			var mutable = Mutable.<Float> nil();
			var c = queue.poll(mutable) ? mutable.value() : c0;
			return c;
		});
	}

	public FltOpt opt() {
		var c = pull();
		if (c != empty)
			return pull() == empty ? FltOpt.of(c) : fail("more than one result");
		else
			return FltOpt.none();
	}

	public Pair<FltPuller, FltPuller> partition(FltPred pred) {
		return Pair.of(filter(pred), filter(c -> !pred.test(c)));
	}

	public float pull() {
		return source.g();
	}

	public FltPuller reverse() {
		var list = toList();
		return FltPuller.of(list.cs, list.size - 1, -1, -1);
	}

	public void sink(FltSink sink0) {
		var sink1 = sink0.rethrow();
		float c;
		while ((c = pull()) != empty)
			sink1.f(c);
	}

	public FltPuller skip(int n) {
		var end = false;
		for (var i = 0; !end && i < n; i++)
			end = pull() == empty;
		return !end ? of(source) : empty();
	}

	public FltPuller snoc(float c) {
		return of(FltFunUtil.snoc(c, source));
	}

	public FltSource source() {
		return source;
	}

	public FltPuller sort() {
		var array = toArray();
		Arrays.sort(array);
		return FltPuller.of(array);
	}

	public Puller<FltPuller> split(FltPred fun) {
		return Puller.of(FunUtil.map(FltPuller::new, FltFunUtil.split(fun, source)));
	}

	public float sum() {
		float result = 0, c1;
		while ((c1 = pull()) != empty)
			result += c1;
		return result;
	}

	public FltPuller take(int n) {
		return of(new FltSource() {
			private int count = n;

			public float g() {
				return 0 < count-- ? pull() : null;
			}
		});
	}

	public FltPuller takeWhile(FltPred fun) {
		return of(new FltSource() {
			private boolean b = true;

			public float g() {
				float t;
				return (t = pull()) != empty && (b &= fun.test(t)) ? t : empty;
			}
		});
	}

	public float[] toArray() {
		var list = toList();
		return Arrays.copyOf(list.cs, list.size);
	}

	public Builder toList() {
		var list = new Builder();
		float c;
		while ((c = pull()) != empty)
			list.append(c);
		return list;
	}

	public <K, V> Map<K, V> toMap(Flt_Obj<K> kf0, Flt_Obj<V> vf0) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		var map = new HashMap<K, V>();
		float c;
		while ((c = pull()) != empty) {
			var key = kf1.apply(c);
			if (map.put(key, vf1.apply(c)) != null)
				fail("duplicate key " + key);
		}
		return map;
	}

	public <U, R> Puller<R> zip(Puller<U> outlet1, FltObj_Obj<U, R> fun) {
		return Puller.of(() -> {
			var t = pull();
			var u = outlet1.pull();
			return t != empty && u != null ? fun.apply(t, u) : null;
		});
	}

	public static class Builder {
		public float[] cs = emptyArray;
		public int size;

		private Builder append(float c) {
			extendBuffer(size + 1);
			cs[size++] = c;
			return this;
		}

		private void extendBuffer(int capacity1) {
			var capacity0 = cs.length;

			if (capacity0 < capacity1) {
				int capacity = max(capacity0, 4);
				while (capacity < capacity1)
					capacity = capacity < Buffer.size ? capacity << 1 : capacity * 3 / 2;

				cs = Arrays.copyOf(cs, capacity);
			}
		}
	}

	private static float[] emptyArray = new float[0];

}
