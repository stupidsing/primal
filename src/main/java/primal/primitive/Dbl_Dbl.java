package primal.primitive;

import static primal.statics.Fail.fail;

public interface Dbl_Dbl {

	public double apply(double c);

	public default Dbl_Dbl rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
