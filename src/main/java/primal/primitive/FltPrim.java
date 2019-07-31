package primal.primitive;

import static primal.statics.Fail.fail;

import primal.primitive.adt.pair.FltObjPair;

public class FltPrim {

	public static float EMPTYVALUE = Float.MIN_VALUE;

	public interface Flt_Obj<T> {
		public T apply(float c);

		public default Flt_Obj<T> rethrow() {
			return i -> {
				try {
					return apply(i);
				} catch (Exception ex) {
					return fail("for " + i, ex);
				}
			};
		}
	}

	public interface FltComparator {
		int compare(float c0, float c1);
	}

	public interface FltObj_Obj<X, Y> {
		public Y apply(float c, X x);

		public default FltObj_Obj<X, Y> rethrow() {
			return (x, y) -> {
				try {
					return apply(x, y);
				} catch (Exception ex) {
					return fail("for " + x + ":" + y, ex);
				}
			};
		}
	}

	public interface FltObjPredicate<T> {
		public boolean test(float c, T t);

		public default FltObjPredicate<T> rethrow() {
			return (c, t) -> {
				try {
					return test(c, t);
				} catch (Exception ex) {
					return fail("for " + c + ":" + t, ex);
				}
			};
		}
	}

	public interface FltObjSink<T> { // extends ObjCharConsumer<T>
		public void sink2(float c, T t);

		public default FltObjSink<T> rethrow() {
			return (c, t) -> {
				try {
					sink2(c, t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface FltObjSource<T> {
		public boolean source2(FltObjPair<T> pair);
	}

	public interface FltTest {
		public boolean test(float c);

		public default FltTest rethrow() {
			return c -> {
				try {
					return test(c);
				} catch (Exception ex) {
					return fail("for " + c, ex);
				}
			};
		}
	}

	public interface FltSink {
		public void f(float c);

		public default FltSink rethrow() {
			return t -> {
				try {
					f(t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface FltSource {
		public float g();
	}

	public interface Obj_Flt<T> {
		public float apply(T t);

		public default Obj_Flt<T> rethrow() {
			return t -> {
				try {
					return apply(t);
				} catch (Exception ex) {
					return fail("for " + t, ex);
				}
			};
		}
	}

	public interface ObjObj_Flt<X, Y> {
		public float apply(X x, Y y);

		public default ObjObj_Flt<X, Y> rethrow() {
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
