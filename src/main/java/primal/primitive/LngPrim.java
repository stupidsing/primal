package primal.primitive;

import static primal.statics.Fail.fail;

import primal.primitive.adt.pair.LngObjPair;

public class LngPrim {

	public static long EMPTYVALUE = Long.MIN_VALUE;

	public interface Lng_Obj<T> {
		public T apply(long c);

		public default Lng_Obj<T> rethrow() {
			return i -> {
				try {
					return apply(i);
				} catch (Exception ex) {
					return fail("for " + i, ex);
				}
			};
		}
	}

	public interface LngComparator {
		int compare(long c0, long c1);
	}

	public interface LngObj_Obj<X, Y> {
		public Y apply(long c, X x);

		public default LngObj_Obj<X, Y> rethrow() {
			return (x, y) -> {
				try {
					return apply(x, y);
				} catch (Exception ex) {
					return fail("for " + x + ":" + y, ex);
				}
			};
		}
	}

	public interface LngObjPredicate<T> {
		public boolean test(long c, T t);

		public default LngObjPredicate<T> rethrow() {
			return (c, t) -> {
				try {
					return test(c, t);
				} catch (Exception ex) {
					return fail("for " + c + ":" + t, ex);
				}
			};
		}
	}

	public interface LngObjSink<T> { // extends ObjCharConsumer<T>
		public void sink2(long c, T t);

		public default LngObjSink<T> rethrow() {
			return (c, t) -> {
				try {
					sink2(c, t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface LngObjSource<T> {
		public boolean source2(LngObjPair<T> pair);
	}

	public interface LngTest {
		public boolean test(long c);

		public default LngTest rethrow() {
			return c -> {
				try {
					return test(c);
				} catch (Exception ex) {
					return fail("for " + c, ex);
				}
			};
		}
	}

	public interface LngSink {
		public void f(long c);

		public default LngSink rethrow() {
			return t -> {
				try {
					f(t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface LngSource {
		public long g();
	}

	public interface Obj_Lng<T> {
		public long apply(T t);

		public default Obj_Lng<T> rethrow() {
			return t -> {
				try {
					return apply(t);
				} catch (Exception ex) {
					return fail("for " + t, ex);
				}
			};
		}
	}

	public interface ObjObj_Lng<X, Y> {
		public long apply(X x, Y y);

		public default ObjObj_Lng<X, Y> rethrow() {
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
