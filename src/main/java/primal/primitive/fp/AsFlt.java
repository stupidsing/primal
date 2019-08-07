package primal.primitive.fp;

import primal.adt.Pair;
import primal.fp.Funs.Sink;
import primal.primitive.FltPrim.ObjObj_Flt;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.FltVerbs.CopyFlt;
import primal.primitive.adt.Floats;
import primal.primitive.adt.Floats.FloatsBuilder;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsFlt {

	public static Floats build(Sink<FloatsBuilder> sink) {
		var sb = new FloatsBuilder();
		sink.f(sb);
		return sb.toFloats();
	}

	public static Floats concat(Floats... array) {
		var length = 0;
		for (var floats : array)
			length += floats.size();
		var cs1 = new float[length];
		var i = 0;
		for (var floats : array) {
			var size_ = floats.size();
			CopyFlt.array(floats.cs, floats.start, cs1, i, size_);
			i += size_;
		}
		return Floats.of(cs1);
	}

	public static float[] concat(float[]... array) {
		var length = 0;
		for (var fs : array)
			length += fs.length;
		var fs1 = new float[length];
		var i = 0;
		for (var fs : array) {
			var length_ = fs.length;
			CopyFlt.array(fs, 0, fs1, i, length_);
			i += length_;
		}
		return fs1;
	}

	public static Floats of(Puller<Floats> puller) {
		return build(cb -> puller.forEach(cb::append));
	}

	public static <T> Obj_Flt<Puller<T>> sum(Obj_Flt<T> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var source = puller.source();
			T t;
			var result = (float) 0;
			while ((t = source.g()) != null)
				result += fun1.apply(t);
			return result;
		};
	}

	public static <K, V> Obj_Flt<Puller2<K, V>> sum(ObjObj_Flt<K, V> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var pair = Pair.<K, V> of(null, null);
			var source = puller.source();
			var result = (float) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
