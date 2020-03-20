package primal.os;

import java.lang.reflect.Modifier;

public class Env {

	public static String HOME;
	public static String USER;

	static {
		for (var f : Env.class.getDeclaredFields())
			if (Modifier.isStatic(f.getModifiers())) {
				String value = System.getenv(f.getName());
				if (value != null && !value.isEmpty())
					try {
						f.set(null, value);
					} catch (Exception e) {
						e.printStackTrace();
						throw new IllegalStateException(e);
					}
			}
	}

}
