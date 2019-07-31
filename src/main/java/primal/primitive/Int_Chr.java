package primal.primitive;

import static primal.statics.Fail.fail;

public interface Int_Chr {

	public char apply(int c);

	public default Int_Chr rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
