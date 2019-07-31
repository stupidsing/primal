package primal.fp;

import static primal.statics.Fail.fail;

import java.util.function.BiFunction;

import primal.adt.Pair;

public class Funs2 {

	public interface BinOp<T> extends BiFun<T, T> {
	}

	public interface FoldOp<I, O> extends Fun2<I, O, O> {
	}

	public interface BiFun<I, O> extends Fun2<I, I, O> {
	}

	public interface Source2<K, V> {
		public boolean source2(Pair<K, V> pair);
	}

	public interface Sink2<K, V> {
		public void sink2(K key, V value);

		public default Sink2<K, V> rethrow() {
			return (k, v) -> {
				try {
					sink2(k, v);
				} catch (Exception ex) {
					fail("for " + k + ", " + v, ex);
				}
			};
		}
	}

	public interface Fun2<X, Y, Z> extends BiFunction<X, Y, Z> {
		public default Fun2<X, Y, Z> rethrow() {
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
