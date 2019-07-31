package primal.primitive;

import static primal.statics.Fail.fail;

public interface Dbl_Lng {

	public long apply(double c);

	public default Dbl_Lng rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
