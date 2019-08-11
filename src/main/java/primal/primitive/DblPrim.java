package primal.primitive;

import static primal.statics.Fail.fail;

public class DblPrim {

	public static double EMPTYVALUE = Double.MIN_VALUE;

	public interface Dbl_Obj<T> {
		public T apply(double c);

		public default Dbl_Obj<T> rethrow() {
			return i -> {
				try {
					return apply(i);
				} catch (Exception ex) {
					return fail("for " + i, ex);
				}
			};
		}
	}

	public interface DblComparator {
		int compare(double c0, double c1);
	}

	public interface DblObj_Obj<X, Y> {
		public Y apply(double c, X x);

		public default DblObj_Obj<X, Y> rethrow() {
			return (x, y) -> {
				try {
					return apply(x, y);
				} catch (Exception ex) {
					return fail("for " + x + ":" + y, ex);
				}
			};
		}
	}

	public static class DblObjPair_<V> {
		public double k;
		public V v;

		protected DblObjPair_(double k, V v) {
			update(k, v);
		}

		public void update(double k_, V v_) {
			k = k_;
			v = v_;
		}
	}

	public interface DblObjPredicate<T> {
		public boolean test(double c, T t);

		public default DblObjPredicate<T> rethrow() {
			return (c, t) -> {
				try {
					return test(c, t);
				} catch (Exception ex) {
					return fail("for " + c + ":" + t, ex);
				}
			};
		}
	}

	public interface DblObjSink<T> { // extends ObjCharConsumer<T>
		public void sink2(double c, T t);

		public default DblObjSink<T> rethrow() {
			return (c, t) -> {
				try {
					sink2(c, t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface DblObjSource<T> {
		public boolean source2(DblObjPair_<T> pair);
	}

	public interface DblPred {
		public boolean test(double c);

		public default DblPred rethrow() {
			return c -> {
				try {
					return test(c);
				} catch (Exception ex) {
					return fail("for " + c, ex);
				}
			};
		}
	}

	public interface DblSink {
		public void f(double c);

		public default DblSink rethrow() {
			return t -> {
				try {
					f(t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface DblSource {
		public double g();
	}

	public interface Obj_Dbl<T> {
		public double apply(T t);

		public default Obj_Dbl<T> rethrow() {
			return t -> {
				try {
					return apply(t);
				} catch (Exception ex) {
					return fail("for " + t, ex);
				}
			};
		}
	}

	public interface ObjObj_Dbl<X, Y> {
		public double apply(X x, Y y);

		public default ObjObj_Dbl<X, Y> rethrow() {
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
