package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltFltSink {

	public void sink2(float c, float f);

	public default FltFltSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
