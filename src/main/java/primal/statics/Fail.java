package primal.statics;

public class Fail {

	public static class InterruptedRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1l;

		public InterruptedRuntimeException(InterruptedException ex) {
			super(ex);
		}
	}

	public static <T> T fail() {
		return fail(null, null);
	}

	public static <T> T fail(String m) {
		return fail(m, null);
	}

	public static <T> T fail(Throwable th) {
		return fail(null, th);
	}

	public static <T> T fail(String m, Throwable th) {
		return t(m, th);
	}

	public static boolean failBool(String m) {
		return t(m, null) != null;
	}

	private static <T> T t(String m, Throwable th) {
		if (th instanceof InterruptedException ex)
			throw new InterruptedRuntimeException(ex);
		else if (th instanceof RuntimeException ex && m == null)
			throw ex;
		else
			throw new RuntimeException((th != null ? th.getMessage() + "\n" : "") + m, th);
	}

}
