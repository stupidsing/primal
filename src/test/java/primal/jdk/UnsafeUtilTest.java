package primal.jdk;

import org.junit.Test;

import primal.Main;
import primal.Verbs.New;
import primal.Verbs.ReadFile;

public class UnsafeUtilTest {

	@Test
	public void test() throws Exception {
		var className = "primal.Main";
		var bytes = ReadFile.from("target/classes/" + className.replace(".", "/") + ".class").readBytes();
		Class<? extends AutoCloseable> clazz = new UnsafeUtil().defineClass(Main.class, className, bytes);
		New.clazz(clazz).close();
	}

}
