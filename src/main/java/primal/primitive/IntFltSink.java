package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntFltSink {

	public void sink2(int c, float f);

	public default IntFltSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
