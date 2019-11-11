package primal.primitive.fp;

import primal.adt.Pair;
import primal.fp.Funs.Sink;
import primal.primitive.LngPrim.ObjObj_Lng;
import primal.primitive.LngPrim.Obj_Lng;
import primal.primitive.LngVerbs.CopyLng;
import primal.primitive.Int_Lng;
import primal.primitive.adt.Longs;
import primal.primitive.adt.Longs.LongsBuilder;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsLng {

	public static long[] array(int length, Int_Lng f) {
		var cs = new long[length];
		for (var i = 0; i < length; i++)
			cs[i] = f.apply(i);
		return cs;
	}

	public static Longs build(Sink<LongsBuilder> sink) {
		var sb = new LongsBuilder();
		sink.f(sb);
		return sb.toLongs();
	}

	public static Longs concat(Longs... array) {
		var length = 0;
		for (var longs : array)
			length += longs.size();
		var cs1 = new long[length];
		var i = 0;
		for (var longs : array) {
			var size_ = longs.size();
			CopyLng.array(longs.cs, longs.start, cs1, i, size_);
			i += size_;
		}
		return Longs.of(cs1);
	}

	public static long[] concat(long[]... array) {
		var length = 0;
		for (var fs : array)
			length += fs.length;
		var fs1 = new long[length];
		var i = 0;
		for (var fs : array) {
			var length_ = fs.length;
			CopyLng.array(fs, 0, fs1, i, length_);
			i += length_;
		}
		return fs1;
	}

	public static Longs of(Puller<Longs> puller) {
		return build(cb -> puller.forEach(cb::append));
	}

	public static <T> Obj_Lng<Puller<T>> sum(Obj_Lng<T> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var source = puller.source();
			T t;
			var result = (long) 0;
			while ((t = source.g()) != null)
				result += fun1.apply(t);
			return result;
		};
	}

	public static <K, V> Obj_Lng<Puller2<K, V>> sum(ObjObj_Lng<K, V> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var pair = Pair.<K, V> of(null, null);
			var source = puller.source();
			var result = (long) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
