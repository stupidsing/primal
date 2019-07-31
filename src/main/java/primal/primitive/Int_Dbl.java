package primal.primitive;

import static primal.statics.Fail.fail;

public interface Int_Dbl {

	public double apply(int c);

	public default Int_Dbl rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
