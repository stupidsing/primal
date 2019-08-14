package primal;

import primal.Verbs.Get;

public class Adjectives {

	public static class Current {
		public static Class<?> clazz() {
			try {
				return Class.forName(Get.stackTrace(3).getClassName());
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static String method() {
			return Get.stackTrace(3).getMethodName();
		}

		public static String package_() {
			var cls = Get.stackTrace(3).getClassName();
			var pos = cls.lastIndexOf(".");
			return cls.substring(0, pos);
		}
	}

}
