package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblObj_Chr<T> {

	public char apply(double c, T t);

	public default DblObj_Chr<T> rethrow() {
		return (c, t) -> {
			try {
				return apply(c, t);
			} catch (Exception ex) {
				return fail("for " + c + ":" + t + ", ", ex);
			}
		};
	}

}
