package primal.primitive;

import static primal.statics.Fail.fail;

public interface Flt_Int {

	public int apply(float c);

	public default Flt_Int rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
