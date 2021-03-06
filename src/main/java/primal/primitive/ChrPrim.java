package primal.primitive;

import static primal.statics.Fail.fail;

public class ChrPrim {

	public static char EMPTYVALUE = Character.MIN_VALUE;

	public interface Chr_Obj<T> {
		public T apply(char c);

		public default Chr_Obj<T> rethrow() {
			return i -> {
				try {
					return apply(i);
				} catch (Exception ex) {
					return fail("for " + i, ex);
				}
			};
		}
	}

	public interface ChrComparator {
		int compare(char c0, char c1);
	}

	public interface ChrObj_Obj<X, Y> {
		public Y apply(char c, X x);

		public default ChrObj_Obj<X, Y> rethrow() {
			return (x, y) -> {
				try {
					return apply(x, y);
				} catch (Exception ex) {
					return fail("for " + x + ":" + y, ex);
				}
			};
		}
	}

	public static class ChrObjPair_<V> {
		public char k;
		public V v;

		protected ChrObjPair_(char k, V v) {
			update(k, v);
		}

		public void update(char k_, V v_) {
			k = k_;
			v = v_;
		}
	}

	public interface ChrObjPredicate<T> {
		public boolean test(char c, T t);

		public default ChrObjPredicate<T> rethrow() {
			return (c, t) -> {
				try {
					return test(c, t);
				} catch (Exception ex) {
					return fail("for " + c + ":" + t, ex);
				}
			};
		}
	}

	public interface ChrObjSink<T> { // extends ObjCharConsumer<T>
		public void sink2(char c, T t);

		public default ChrObjSink<T> rethrow() {
			return (c, t) -> {
				try {
					sink2(c, t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface ChrObjSource<T> {
		public boolean source2(ChrObjPair_<T> pair);
	}

	public interface ChrPred {
		public boolean test(char c);

		public default ChrPred rethrow() {
			return c -> {
				try {
					return test(c);
				} catch (Exception ex) {
					return fail("for " + c, ex);
				}
			};
		}
	}

	public interface ChrSink {
		public void f(char c);

		public default ChrSink rethrow() {
			return t -> {
				try {
					f(t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface ChrSource {
		public char g();
	}

	public interface Obj_Chr<T> {
		public char apply(T t);

		public default Obj_Chr<T> rethrow() {
			return t -> {
				try {
					return apply(t);
				} catch (Exception ex) {
					return fail("for " + t, ex);
				}
			};
		}
	}

	public interface ObjObj_Chr<X, Y> {
		public char apply(X x, Y y);

		public default ObjObj_Chr<X, Y> rethrow() {
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
