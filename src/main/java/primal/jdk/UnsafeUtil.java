package primal.jdk;

import java.lang.invoke.MethodHandles;

public class UnsafeUtil {

	public <T> Class<? extends T> defineClass(Class<T> interfaceClazz, String className, byte[] bytes) {
		return defineClass(interfaceClazz, className, bytes, null);
	}

	public <T> Class<? extends T> defineClass(Class<T> interfaceClazz, String className, byte[] bytes, Object[] array) {
		try {
			@SuppressWarnings("unchecked")
			var clazz = (Class<? extends T>) MethodHandles.lookup().defineHiddenClass(bytes, true).lookupClass();
			return clazz;
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

}
