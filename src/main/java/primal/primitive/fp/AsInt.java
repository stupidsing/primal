package primal.primitive.fp;

import primal.adt.Pair;
import primal.fp.Funs.Sink;
import primal.primitive.IntPrim.ObjObj_Int;
import primal.primitive.IntPrim.Obj_Int;
import primal.primitive.IntVerbs.CopyInt;
import primal.primitive.adt.Ints;
import primal.primitive.adt.Ints.IntsBuilder;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsInt {

	public static Ints build(Sink<IntsBuilder> sink) {
		var sb = new IntsBuilder();
		sink.f(sb);
		return sb.toInts();
	}

	public static Ints concat(Ints... array) {
		var length = 0;
		for (var ints : array)
			length += ints.size();
		var cs1 = new int[length];
		var i = 0;
		for (var ints : array) {
			var size_ = ints.size();
			CopyInt.array(ints.cs, ints.start, cs1, i, size_);
			i += size_;
		}
		return Ints.of(cs1);
	}

	public static int[] concat(int[]... array) {
		var length = 0;
		for (var fs : array)
			length += fs.length;
		var fs1 = new int[length];
		var i = 0;
		for (var fs : array) {
			var length_ = fs.length;
			CopyInt.array(fs, 0, fs1, i, length_);
			i += length_;
		}
		return fs1;
	}

	public static Ints of(Puller<Ints> puller) {
		return build(cb -> puller.forEach(cb::append));
	}

	public static <T> Obj_Int<Puller<T>> sum(Obj_Int<T> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var source = puller.source();
			T t;
			var result = (int) 0;
			while ((t = source.g()) != null)
				result += fun1.apply(t);
			return result;
		};
	}

	public static <K, V> Obj_Int<Puller2<K, V>> sum(ObjObj_Int<K, V> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var pair = Pair.<K, V> of(null, null);
			var source = puller.source();
			var result = (int) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
