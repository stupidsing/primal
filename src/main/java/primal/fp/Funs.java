package primal.fp;

import static primal.statics.Fail.fail;

import java.util.function.Function;

public class Funs {

	public interface Iterate<T> extends Fun<T, T> {
	}

	public interface Sink<I> {
		public void f(I i);

		public default Sink<I> rethrow() {
			return t -> {
				try {
					f(t);
				} catch (Exception ex) {
					fail("for " + t, ex);
				}
			};
		}
	}

	public interface Source<O> {
		public O g();
	}

	public interface Fun<I, O> extends Function<I, O> {
		public default Fun<I, O> rethrow() {
			return i -> {
				try {
					return apply(i);
				} catch (Exception ex) {
					return fail("for " + i, ex);
				}
			};
		}
	}

}
