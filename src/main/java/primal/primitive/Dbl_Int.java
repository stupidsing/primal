package primal.primitive;

import static primal.statics.Fail.fail;

public interface Dbl_Int {

	public int apply(double c);

	public default Dbl_Int rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
