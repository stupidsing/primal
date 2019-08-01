package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrDblSink {

	public void sink2(char c, double f);

	public default ChrDblSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
