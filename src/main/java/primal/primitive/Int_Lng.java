package primal.primitive;

import static primal.statics.Fail.fail;

public interface Int_Lng {

	public long apply(int c);

	public default Int_Lng rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
