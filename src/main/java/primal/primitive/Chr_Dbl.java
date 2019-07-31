package primal.primitive;

import static primal.statics.Fail.fail;

public interface Chr_Dbl {

	public double apply(char c);

	public default Chr_Dbl rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
