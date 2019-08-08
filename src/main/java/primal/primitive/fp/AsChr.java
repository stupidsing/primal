package primal.primitive.fp;

import primal.adt.Pair;
import primal.fp.Funs.Sink;
import primal.primitive.ChrPrim.ObjObj_Chr;
import primal.primitive.ChrPrim.Obj_Chr;
import primal.primitive.ChrVerbs.CopyChr;
import primal.primitive.adt.Chars;
import primal.primitive.adt.Chars.CharsBuilder;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsChr {

	public static Chars build(Sink<CharsBuilder> sink) {
		var sb = new CharsBuilder();
		sink.f(sb);
		return sb.toChars();
	}

	public static Chars concat(Chars... array) {
		var length = 0;
		for (var chars : array)
			length += chars.size();
		var cs1 = new char[length];
		var i = 0;
		for (var chars : array) {
			var size_ = chars.size();
			CopyChr.array(chars.cs, chars.start, cs1, i, size_);
			i += size_;
		}
		return Chars.of(cs1);
	}

	public static char[] concat(char[]... array) {
		var length = 0;
		for (var fs : array)
			length += fs.length;
		var fs1 = new char[length];
		var i = 0;
		for (var fs : array) {
			var length_ = fs.length;
			CopyChr.array(fs, 0, fs1, i, length_);
			i += length_;
		}
		return fs1;
	}

	public static Chars of(Puller<Chars> puller) {
		return build(cb -> puller.forEach(cb::append));
	}

	public static <T> Obj_Chr<Puller<T>> sum(Obj_Chr<T> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var source = puller.source();
			T t;
			var result = (char) 0;
			while ((t = source.g()) != null)
				result += fun1.apply(t);
			return result;
		};
	}

	public static <K, V> Obj_Chr<Puller2<K, V>> sum(ObjObj_Chr<K, V> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var pair = Pair.<K, V> of(null, null);
			var source = puller.source();
			var result = (char) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
