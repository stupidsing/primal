package primal.jdk;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import primal.Main;
import primal.Verbs.New;

public class UnsafeUtilTest {

	@Test
	public void test() throws Exception {
		var className = "primal.Main";
		var bytes = Files.readAllBytes(Paths.get("target/classes/" + className.replace(".", "/") + ".class"));
		Class<? extends AutoCloseable> clazz = new UnsafeUtil().defineClass(Main.class, className, bytes);
		New.clazz(clazz).close();
	}

}
