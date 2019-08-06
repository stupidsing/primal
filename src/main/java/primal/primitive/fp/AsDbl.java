package primal.primitive.fp;

import primal.adt.Pair;
import primal.fp.Funs.Sink;
import primal.primitive.DblPrim.ObjObj_Dbl;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.primitive.DblVerbs.CopyDbl;
import primal.primitive.adt.Doubles;
import primal.primitive.adt.Doubles.DoublesBuilder;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsDbl {

	public static Doubles build(Sink<DoublesBuilder> sink) {
		var sb = new DoublesBuilder();
		sink.f(sb);
		return sb.toDoubles();
	}

	public static Doubles concat(Doubles... array) {
		var length = 0;
		for (var doubles : array)
			length += doubles.size();
		var cs1 = new double[length];
		var i = 0;
		for (var doubles : array) {
			var size_ = doubles.size();
			CopyDbl.array(doubles.cs, doubles.start, cs1, i, size_);
			i += size_;
		}
		return Doubles.of(cs1);
	}

	public static double[] concat(double[]... array) {
		var length = 0;
		for (var fs : array)
			length += fs.length;
		var fs1 = new double[length];
		var i = 0;
		for (var fs : array) {
			var length_ = fs.length;
			CopyDbl.array(fs, 0, fs1, i, length_);
			i += length_;
		}
		return fs1;
	}

	public static Doubles of(Puller<Doubles> puller) {
		return build(cb -> puller.forEach(cb::append));
	}

	public static <T> Obj_Dbl<Puller<T>> sum(Obj_Dbl<T> fun0) {
		var fun1 = fun0.rethrow();
		return puller -> {
			var source = puller.source();
			T t;
			var result = (double) 0;
			while ((t = source.g()) != null)
				result += fun1.apply(t);
			return result;
		};
	}

	public static <K, V> Obj_Dbl<Puller2<K, V>> sum(ObjObj_Dbl<K, V> fun0) {
		ObjObj_Dbl<K, V> fun1 = fun0.rethrow();
		return puller -> {
			var pair = Pair.<K, V> of(null, null);
			var source = puller.source();
			var result = (double) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
