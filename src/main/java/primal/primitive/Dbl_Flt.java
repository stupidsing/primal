package primal.primitive;

import static primal.statics.Fail.fail;

public interface Dbl_Flt {

	public float apply(double c);

	public default Dbl_Flt rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
