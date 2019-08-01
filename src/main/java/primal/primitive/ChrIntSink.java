package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrIntSink {

	public void sink2(char c, int f);

	public default ChrIntSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
