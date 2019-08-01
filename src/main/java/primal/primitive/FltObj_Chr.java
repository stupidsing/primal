package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltObj_Chr<T> {

	public char apply(float c, T t);

	public default FltObj_Chr<T> rethrow() {
		return (c, t) -> {
			try {
				return apply(c, t);
			} catch (Exception ex) {
				return fail("for " + c + ":" + t + ", ", ex);
			}
		};
	}

}
