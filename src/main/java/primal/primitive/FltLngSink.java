package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltLngSink {

	public void sink2(float c, long f);

	public default FltLngSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
