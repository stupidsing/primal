package primal.primitive.fp;

import static primal.statics.Fail.fail;

import java.util.Collections;
import java.util.Iterator;

import primal.NullableSyncQueue;
import primal.Verbs.Start;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Sink;
import primal.fp.Funs.Source;
import primal.fp.Funs2.Source2;
import primal.os.Log_;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.LngObjSource;
import primal.primitive.LngPrim.LngPred;
import primal.primitive.LngPrim.LngSink;
import primal.primitive.LngPrim.LngSource;
import primal.primitive.LngPrim.Lng_Obj;
import primal.primitive.Lng_Lng;
import primal.primitive.adt.pair.LngObjPair;
import primal.statics.Fail.InterruptedRuntimeException;

public class LngFunUtil {

	private static long empty = LngPrim.EMPTYVALUE;

	public static Source<LngSource> chunk(int n, LngSource source) {
		return new Source<>() {
			private long c = source.g();
			private boolean isAvail = c != LngPrim.EMPTYVALUE;
			private int i;
			private LngSource source_ = () -> {
				if ((isAvail = isAvail && (c = source.g()) != LngPrim.EMPTYVALUE) && ++i < n)
					return c;
				else {
					i = 0;
					return LngPrim.EMPTYVALUE;
				}
			};

			public LngSource g() {
				return isAvail ? cons(c, source_) : null;
			}
		};
	}

	public static LngSource concat(Source<LngSource> source) {
		return new LngSource() {
			private LngSource source0 = nullSource();

			public long g() {
				var c = LngPrim.EMPTYVALUE;
				while (source0 != null && (c = source0.g()) == LngPrim.EMPTYVALUE)
					source0 = source.g();
				return c;
			}
		};
	}

	public static LngSource cons(long c, LngSource source) {
		return new LngSource() {
			private boolean isFirst = true;

			public long g() {
				if (!isFirst)
					return source.g();
				else {
					isFirst = false;
					return c;
				}
			}
		};
	}

	public static LngSource drop(int n, LngSource source) {
		var isAvailable = true;
		while (0 < n && (isAvailable &= source.g() != empty))
			n--;
		return isAvailable ? source : nullSource();
	}

	public static LngSource dropWhile(LngPred fun, LngSource source) {
		return new LngSource() {
			private boolean b = true;

			public long g() {
				long t;
				while ((t = source.g()) != empty && (b &= fun.test(t)))
					;
				return t;
			}
		};
	}

	public static LngSource filter(LngPred fun0, LngSource source) {
		var fun1 = fun0.rethrow();
		return () -> {
			var c = LngPrim.EMPTYVALUE;
			while ((c = source.g()) != LngPrim.EMPTYVALUE && !fun1.test(c))
				;
			return c;
		};
	}

	public static LngSource flatten(Source<Iterable<Long>> source) {
		return new LngSource() {
			private Iterator<Long> iter = Collections.emptyIterator();

			public long g() {
				Iterable<Long> iterable;
				while (!iter.hasNext())
					if ((iterable = source.g()) != null)
						iter = iterable.iterator();
					else
						return LngPrim.EMPTYVALUE;
				return iter.next();
			}
		};
	}

	public static <R> R fold(Fun<LngObjPair<R>, R> fun0, R init, LngSource source) {
		var fun1 = fun0.rethrow();
		long c;
		while ((c = source.g()) != LngPrim.EMPTYVALUE)
			init = fun1.apply(LngObjPair.of(c, init));
		return init;
	}

	public static boolean isAll(LngPred pred0, LngSource source) {
		var pred1 = pred0.rethrow();
		long c;
		while ((c = source.g()) != LngPrim.EMPTYVALUE)
			if (!pred1.test(c))
				return false;
		return true;
	}

	public static boolean isAny(LngPred pred0, LngSource source) {
		var pred1 = pred0.rethrow();
		long c;
		while ((c = source.g()) != LngPrim.EMPTYVALUE)
			if (pred1.test(c))
				return true;
		return false;
	}

	public static Iterator<Long> iterator(LngSource source) {
		return new Iterator<>() {
			private long next = LngPrim.EMPTYVALUE;

			public boolean hasNext() {
				if (next == LngPrim.EMPTYVALUE)
					next = source.g();
				return next != LngPrim.EMPTYVALUE;
			}

			public Long next() {
				var next0 = next;
				next = LngPrim.EMPTYVALUE;
				return next0;
			}

		};
	}

	public static Iterable<Long> iter(LngSource source) {
		return () -> iterator(source);
	}

	public static <T1> Source<T1> map(Lng_Obj<T1> fun0, LngSource source) {
		var fun1 = fun0.rethrow();
		return () -> {
			var c0 = source.g();
			return c0 != LngPrim.EMPTYVALUE ? fun1.apply(c0) : null;
		};
	}

	public static <K, V> Source2<K, V> map2(Lng_Obj<K> kf0, Lng_Obj<V> vf0, LngSource source) {
		var kf1 = kf0.rethrow();
		var vf1 = vf0.rethrow();
		return pair -> {
			var c = source.g();
			var b = c != LngPrim.EMPTYVALUE;
			if (b)
				pair.update(kf1.apply(c), vf1.apply(c));
			return b;
		};
	}

	public static LngSource mapLng(Lng_Lng fun0, LngSource source) {
		var fun1 = fun0.rethrow();
		return () -> {
			var c = source.g();
			return c != LngPrim.EMPTYVALUE ? fun1.apply(c) : LngPrim.EMPTYVALUE;
		};
	}

	public static <V> LngObjSource<V> mapLngObj(Lng_Obj<V> fun0, LngSource source) {
		var fun1 = fun0.rethrow();
		return pair -> {
			var c = source.g();
			if (c != LngPrim.EMPTYVALUE) {
				pair.update(c, fun1.apply(c));
				return true;
			} else
				return false;

		};
	}

	public static LngSink nullSink() {
		return i -> {
		};
	}

	public static LngSource nullSource() {
		return () -> LngPrim.EMPTYVALUE;
	}

	public static LngSource snoc(long c, LngSource source) {
		return new LngSource() {
			private boolean isAppended = false;

			public long g() {
				if (!isAppended) {
					var c_ = source.g();
					if (c_ != LngPrim.EMPTYVALUE)
						return c_;
					else {
						isAppended = true;
						return c;
					}
				} else
					return LngPrim.EMPTYVALUE;
			}
		};
	}

	/**
	 * Problematic split: all data must be read, i.e. the children lists must not be
	 * skipped.
	 */
	public static Source<LngSource> split(LngPred fun0, LngSource source) {
		var fun1 = fun0.rethrow();
		return new Source<>() {
			private long c = source.g();
			private boolean isAvail = c != LngPrim.EMPTYVALUE;
			private LngSource source_ = () -> (isAvail = isAvail && (c = source.g()) != LngPrim.EMPTYVALUE)
					&& !fun1.test(c) ? c : null;

			public LngSource g() {
				return isAvail ? cons(c, source_) : null;
			}
		};
	}

	/**
	 * Sucks data from a sink and make it into a source.
	 */
	public static LngSource suck(Sink<LngSink> fun) {
		var queue = new NullableSyncQueue<Long>();
		LngSink enqueue = c -> enqueue(queue, c);

		var thread = Start.thread(() -> {
			try {
				fun.f(enqueue);
			} finally {
				enqueue(queue, LngPrim.EMPTYVALUE);
			}
		});

		return () -> {
			try {
				return queue.take();
			} catch (InterruptedException | InterruptedRuntimeException ex) {
				thread.interrupt();
				return fail(ex);
			}
		};
	}

	public static LngSource take(int n, LngSource source) {
		return new LngSource() {
			private int count = n;

			public long g() {
				return 0 < count-- ? source.g() : null;
			}
		};
	}

	public static LngSource takeWhile(LngPred fun, LngSource source) {
		return new LngSource() {
			private boolean b = true;

			public long g() {
				long t;
				return (t = source.g()) != empty && (b &= fun.test(t)) ? t : empty;
			}
		};
	}

	private static void enqueue(NullableSyncQueue<Long> queue, long c) {
		try {
			queue.offer(c);
		} catch (InterruptedException ex) {
			Log_.error(ex);
		}
	}

}
