package primal.primitive;

import static primal.statics.Fail.fail;

public class IntPrim {

	public static int EMPTYVALUE = Integer.MIN_VALUE;

	public interface Int_Obj<T> {
		public T apply(int c);

		public default Int_Obj<T> rethrow() {
			return i -> {
				try {
					return apply(i);
				} catch (Exception ex) {
					return fail("for " + i, ex);
				}
			};
		}
	}

	public interface IntComparator {
		int compare(int c0, int c1);
	}

	public interface IntObj_Obj<X, Y> {
		public Y apply(int c, X x);

		public default IntObj_Obj<X, Y> rethrow() {
			return (x, y) -> {
				try {
					return apply(x, y);
				} catch (Exception ex) {
					return fail("for " + x + ":" + y, ex);
				}
			};
		}
	}

	public static class IntObjPair_<V> {
		public int k;
		public V v;

		protected IntObjPair_(int k, V v) {
			update(k, v);
		}

		public void update(int k_, V v_) {
			k = k_;
			v = v_;
		}
	}

	public interface IntObjPredicate<T> {
		public boolean test(int c, T t);

		public default IntObjPredicate<T> rethrow() {
			return (c, t) -> {
				try {
					return test(c, t);
				} catch (Exception ex) {
					return fail("for " + c + ":" + t, ex);
				}
			};
		}
	}

	public interface IntObjSink<T> { // extends ObjCharConsumer<T>
		public void sink2(int c, T t);

		public default IntObjSink<T> rethrow() {
			return (c, t) -> {
				try {
					sink2(c, t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface IntObjSource<T> {
		public boolean source2(IntObjPair_<T> pair);
	}

	public interface IntPred {
		public boolean test(int c);

		public default IntPred rethrow() {
			return c -> {
				try {
					return test(c);
				} catch (Exception ex) {
					return fail("for " + c, ex);
				}
			};
		}
	}

	public interface IntSink {
		public void f(int c);

		public default IntSink rethrow() {
			return t -> {
				try {
					f(t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface IntSource {
		public int g();
	}

	public interface Obj_Int<T> {
		public int apply(T t);

		public default Obj_Int<T> rethrow() {
			return t -> {
				try {
					return apply(t);
				} catch (Exception ex) {
					return fail("for " + t, ex);
				}
			};
		}
	}

	public interface ObjObj_Int<X, Y> {
		public int apply(X x, Y y);

		public default ObjObj_Int<X, Y> rethrow() {
			return (x, y) -> {
				try {
					return apply(x, y);
				} catch (Exception ex) {
					return fail("for " + x + ":" + y, ex);
				}
			};
		}
	}

}
