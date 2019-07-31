package primal.primitive;

import static primal.statics.Fail.fail;

public interface Int_Int {

	public int apply(int c);

	public default Int_Int rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
