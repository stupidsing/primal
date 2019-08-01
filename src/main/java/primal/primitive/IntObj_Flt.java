package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntObj_Flt<T> {

	public float apply(int c, T t);

	public default IntObj_Flt<T> rethrow() {
		return (c, t) -> {
			try {
				return apply(c, t);
			} catch (Exception ex) {
				return fail("for " + c + ":" + t + ", ", ex);
			}
		};
	}

}
