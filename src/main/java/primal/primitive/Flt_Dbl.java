package primal.primitive;

import static primal.statics.Fail.fail;

public interface Flt_Dbl {

	public double apply(float c);

	public default Flt_Dbl rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
