package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngChr_Obj<T> {

	public T apply(long c, char f);

	public default LngChr_Obj<T> rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f + ", ", ex);
			}
		};
	}

}
