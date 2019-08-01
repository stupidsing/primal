package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblLng_Obj<T> {

	public T apply(double c, long f);

	public default DblLng_Obj<T> rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f + ", ", ex);
			}
		};
	}

}
