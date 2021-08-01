package primal.os;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Log_ {

	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

	public static void infof(String format, Object... args) {
		info(String.format(format, args)); // [I]
	}

	public static void info(String message) {
		out.log("🧐", message); // [I]
	}

	public static void warn(String message) {
		out.log("😰", message); // [W]
	}

	public static void error(Throwable th) {
		error("", th);
	}

	public static void error(String message, Throwable th) {
		out.logException("😱", message, th); // [E]
	}

	public static void fatal(Throwable th) {
		fatal("", th);
	}

	public static void fatal(String message, Throwable th) {
		out.logException("💀", message, th); // [F]
	}

	private static Out out = new Out() {
		private DateTimeFormatter yyyymmdd = dtf;

		public void logException(String type, String message, Throwable th) {
			try (var sw = new StringWriter(); var pw = new PrintWriter(sw);) {
				th.printStackTrace(pw);
				log(type, (!message.isEmpty() ? message + ": " : "") + sw);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public void log(String type, String message) {
			System.err.println(current() + " " + type + " " + prefix.get() + message);
		}

		private String current() {
			return yyyymmdd.format(LocalDateTime.now());
		}
	};

	private interface Out {
		public void log(String type, String message);

		public void logException(String type, String message, Throwable th);
	}

	private static ThreadLocal<String> prefix = ThreadLocal.withInitial(() -> "");

}
