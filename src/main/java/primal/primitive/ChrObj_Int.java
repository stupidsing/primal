package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrObj_Int<T> {

	public int apply(char c, T t);

	public default ChrObj_Int<T> rethrow() {
		return (c, t) -> {
			try {
				return apply(c, t);
			} catch (Exception ex) {
				return fail("for " + c + ":" + t + ", ", ex);
			}
		};
	}

}
