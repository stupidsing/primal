package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblObj_Flt<T> {

	public float apply(double c, T t);

	public default DblObj_Flt<T> rethrow() {
		return (c, t) -> {
			try {
				return apply(c, t);
			} catch (Exception ex) {
				return fail("for " + c + ":" + t + ", ", ex);
			}
		};
	}

}
