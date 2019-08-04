package primal.io;

import static primal.statics.Fail.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import primal.Nouns.Utf8;
import primal.statics.Rethrow.FunIo;

/**
 * Extends input stream to provide additional functionality.
 *
 * @author ywsing
 */
public class ReadStream extends InputStream {

	private InputStream is;

	public static ReadStream of(InputStream is) {
		return new ReadStream(is);
	}

	public ReadStream(InputStream is) {
		this.is = is;
	}

	public String readString() {
		return new String(readBytes(), Utf8.charset);
	}

	public byte[] readBytes() {
		return doRead(InputStream::readAllBytes);
	}

	public <T> T doBufferedReader(FunIo<BufferedReader, T> fun) {
		return doReader(r -> {
			try (var br = new BufferedReader(r)) {
				return fun.apply(br);
			}
		});
	}

	public <T> T doReader(FunIo<InputStreamReader, T> fun) {
		return doRead(is -> {
			try (var w = new InputStreamReader(is, Utf8.charset)) {
				return fun.apply(w);
			}
		});
	}

	public <T> T doRead(FunIo<ReadStream, T> fun) {
		try (var is = this) {
			return fun.apply(is);
		} catch (IOException ex) {
			return fail(ex);
		}
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public void mark(int readLimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return is.read(bytes);
	}

	@Override
	public int read(byte[] bs, int offset, int length) throws IOException {
		return is.read(bs, offset, length);
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long skip(long n) {
		throw new UnsupportedOperationException();
	}

}
