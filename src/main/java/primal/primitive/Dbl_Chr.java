package primal.primitive;

import static primal.statics.Fail.fail;

public interface Dbl_Chr {

	public char apply(double c);

	public default Dbl_Chr rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
