package primal.primitive.fp;

import static primal.statics.Fail.fail;

import java.util.Iterator;
import java.util.function.Predicate;

import primal.NullableSyncQueue;
import primal.Verbs.Start;
import primal.adt.Pair;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Sink;
import primal.fp.Funs.Source;
import primal.fp.Funs2.Source2;
import primal.os.Log_;
import primal.primitive.FltObj_Flt;
import primal.primitive.FltPrim.FltObjPair_;
import primal.primitive.FltPrim.FltObjPredicate;
import primal.primitive.FltPrim.FltObjSource;
import primal.primitive.FltPrim.FltObj_Obj;
import primal.primitive.FltPrim.FltPred;
import primal.primitive.adt.pair.FltObjPair;
import primal.statics.Fail.InterruptedRuntimeException;
import primal.statics.Rethrow;

public class FltObjFunUtil {

	public static <V> Source<FltObjSource<V>> chunk(int n, FltObjSource<V> source) {
		return new Source<>() {
			private FltObjPair<V> pair;
			private boolean isAvail;
			private int i;
			private FltObjSource<V> source_ = pair1 -> {
				var b = (isAvail = isAvail && source.source2(pair)) && ++i < n;
				if (b)
					pair1.update(pair.k, pair.v);
				else
					i = 0;
				return b;
			};

			{
				isAvail = source.source2(pair);
			}

			public FltObjSource<V> g() {
				return isAvail ? cons(pair.k, pair.v, source_) : null;
			}
		};
	}

	public static <V> FltObjSource<V> concat(Source<FltObjSource<V>> source) {
		return new FltObjSource<>() {
			private FltObjSource<V> source2 = nullSource();

			public boolean source2(FltObjPair_<V> pair) {
				var b = false;
				while (source2 != null && !(b = source2.source2(pair)))
					source2 = source.g();
				return b;
			}
		};
	}

	public static <V> FltObjSource<V> cons(float key, V value, FltObjSource<V> source2) {
		return new FltObjSource<>() {
			private boolean isFirst = true;

			public boolean source2(FltObjPair_<V> pair) {
				if (!isFirst)
					return source2.source2(pair);
				else {
					isFirst = false;
					pair.update(key, value);
					return true;
				}
			}
		};
	}

	public static <V> FltObjSource<V> filter(FltObjPredicate<V> fun0, FltObjSource<V> source2) {
		var fun1 = fun0.rethrow();
		return pair -> {
			boolean b;
			while ((b = source2.source2(pair)) && !fun1.test(pair.k, pair.v))
				;
			return b;
		};
	}

	public static <V> FltObjSource<V> filterKey(FltPred fun0, FltObjSource<V> source2) {
		var fun1 = fun0.rethrow();
		return pair -> {
			boolean b;
			while ((b = source2.source2(pair)) && !fun1.test(pair.k))
				;
			return b;
		};
	}

	public static <V> FltObjSource<V> filterValue(Predicate<V> fun0, FltObjSource<V> source2) {
		var fun1 = Rethrow.predicate(fun0);
		return pair -> {
			boolean b;
			while ((b = source2.source2(pair)) && !fun1.test(pair.v))
				;
			return b;
		};
	}

	public static <K, V, R> R fold(Fun<Pair<R, FltObjPair<V>>, R> fun0, R init, FltObjSource<V> source2) {
		var fun1 = fun0.rethrow();
		FltObjPair<V> pair = FltObjPair.of((float) 0, null);
		while (source2.source2(pair))
			init = fun1.apply(Pair.of(init, pair));
		return init;
	}

	public static <V> boolean isAll(FltObjPredicate<V> pred0, FltObjSource<V> source2) {
		var pred1 = pred0.rethrow();
		FltObjPair<V> pair = FltObjPair.of((float) 0, null);
		while (source2.source2(pair))
			if (!pred1.test(pair.k, pair.v))
				return false;
		return true;
	}

	public static <V> boolean isAny(FltObjPredicate<V> pred0, FltObjSource<V> source2) {
		var pred1 = pred0.rethrow();
		FltObjPair<V> pair = FltObjPair.of((float) 0, null);
		while (source2.source2(pair))
			if (pred1.test(pair.k, pair.v))
				return true;
		return false;
	}

	public static <V> Iterator<FltObjPair<V>> iterator(FltObjSource<V> source2) {
		return new Iterator<>() {
			private FltObjPair<V> next = null;

			public boolean hasNext() {
				if (next == null) {
					FltObjPair<V> next1 = FltObjPair.of((float) 0, null);
					if (source2.source2(next1))
						next = next1;
				}
				return next != null;
			}

			public FltObjPair<V> next() {
				var next0 = next;
				next = null;
				return next0;
			}
		};
	}

	public static <V> Iterable<FltObjPair<V>> iter(FltObjSource<V> source2) {
		return () -> iterator(source2);
	}

	public static <V, T> Source<T> map(FltObj_Obj<V, T> fun0, FltObjSource<V> source2) {
		var fun1 = fun0.rethrow();
		FltObjPair<V> pair = FltObjPair.of((float) 0, null);
		return () -> source2.source2(pair) ? fun1.apply(pair.k, pair.v) : null;
	}

	public static <V, K1, V1, T> Source2<K1, V1> map2(FltObj_Obj<V, K1> kf0, FltObj_Obj<V, V1> vf0, FltObjSource<V> source2) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		FltObjPair<V> pair1 = FltObjPair.of((float) 0, null);
		return pair -> {
			var b = source2.source2(pair1);
			if (b)
				pair.update(kf1.apply(pair1.k, pair1.v), vf1.apply(pair1.k, pair1.v));
			return b;
		};
	}

	public static <V, V1, T> FltObjSource<V1> mapFltObj(FltObj_Flt<V> kf0, FltObj_Obj<V, V1> vf0, FltObjSource<V> source2) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		FltObjPair<V> pair1 = FltObjPair.of((float) 0, null);
		return pair -> {
			var b = source2.source2(pair1);
			if (b)
				pair.update(kf1.apply(pair1.k, pair1.v), vf1.apply(pair1.k, pair1.v));
			return b;
		};
	}

	public static <I> Sink<I> nullSink() {
		return i -> {
		};
	}

	public static <V> FltObjSource<V> nullSource() {
		return pair -> false;
	}

	/**
	 * Problematic split: all data must be read, i.e. the children lists must not be
	 * skipped.
	 */
	public static <V> Source<FltObjSource<V>> split(FltObjPredicate<V> fun0, FltObjSource<V> source2) {
		var fun1 = fun0.rethrow();
		return new Source<>() {
			private FltObjPair<V> pair = FltObjPair.of((float) 0, null);
			private boolean isAvailable;
			private FltObjSource<V> source2_ = pair_ -> (isAvailable &= source2.source2(pair_)) && !fun1.test(pair.k, pair.v);

			{
				isAvailable = source2.source2(pair);
			}

			public FltObjSource<V> g() {
				return isAvailable ? cons(pair.k, pair.v, source2_) : null;
			}
		};
	}

	public static <V> FltObjSource<V> snoc(float key, V value, FltObjSource<V> source) {
		return new FltObjSource<>() {
			private boolean isAppended = false;

			public boolean source2(FltObjPair_<V> pair) {
				var b = !isAppended;
				if (b && !source.source2(pair)) {
					pair.update(key, value);
					isAppended = true;
				}
				return b;
			}
		};
	}

	/**
	 * Sucks data from a sink and make it into a source.
	 */
	public static <V> FltObjSource<V> suck(Sink<Sink<FltObjPair<V>>> fun) {
		var queue = new NullableSyncQueue<FltObjPair<V>>();
		Sink<FltObjPair<V>> enqueue = pair -> enqueue(queue, pair);

		var thread = Start.thread(() -> {
			try {
				fun.f(enqueue);
			} finally {
				enqueue(queue, null);
			}
		});

		return pair -> {
			try {
				var p = queue.take();
				var b = p != null;
				if (b)
					pair.update(p.k, p.v);
				return b;
			} catch (InterruptedException | InterruptedRuntimeException ex) {
				thread.interrupt();
				return fail();
			}
		};
	}

	private static <T> void enqueue(NullableSyncQueue<T> queue, T t) {
		try {
			queue.offer(t);
		} catch (InterruptedException ex) {
			Log_.error(ex);
		}
	}

}
