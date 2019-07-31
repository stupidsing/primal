package primal.primitive;

import static primal.statics.Fail.fail;

public interface Flt_Lng {

	public long apply(float c);

	public default Flt_Lng rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
