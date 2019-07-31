package primal.primitive;

import static primal.statics.Fail.fail;

public interface Flt_Chr {

	public char apply(float c);

	public default Flt_Chr rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
