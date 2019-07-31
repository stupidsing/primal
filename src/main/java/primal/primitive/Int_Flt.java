package primal.primitive;

import static primal.statics.Fail.fail;

public interface Int_Flt {

	public float apply(int c);

	public default Int_Flt rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
