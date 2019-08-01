package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrObj_Chr<T> {

	public char apply(char c, T t);

	public default ChrObj_Chr<T> rethrow() {
		return (c, t) -> {
			try {
				return apply(c, t);
			} catch (Exception ex) {
				return fail("for " + c + ":" + t + ", ", ex);
			}
		};
	}

}
