package primal.streamlet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import primal.MoreVerbs.Read;
import primal.puller.Puller;

public class StreamletTest {

	@Test
	public void testChunk() {
		var objects = new Object[29];

		for (var i = 0; i < objects.length; i++)
			objects[i] = new Object();

		var chunks = Puller.of(objects).chunk(5);
		assertEquals(5, chunks.pull().toList().size());
		assertEquals(5, chunks.pull().toList().size());
		assertEquals(5, chunks.pull().toList().size());
		assertEquals(5, chunks.pull().toList().size());
		assertEquals(5, chunks.pull().toList().size());
		assertEquals(4, chunks.pull().toList().size());
	}

	@Test
	public void testWhile() {
		assertEquals(List.of(0, 1, 2), Read.each(0, 1, 2, 3, 4, 5).takeWhile(i -> i != 3).toList());
		assertEquals(List.of(3, 4, 5), Read.each(0, 1, 2, 3, 4, 5).dropWhile(i -> i != 3).toList());
	}

}
