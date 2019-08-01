package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltChr_Obj<T> {

	public T apply(float c, char f);

	public default FltChr_Obj<T> rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f + ", ", ex);
			}
		};
	}

}
