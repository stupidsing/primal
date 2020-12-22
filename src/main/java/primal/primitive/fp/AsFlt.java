package primal.primitive.fp;

import primal.adt.Pair;
import primal.primitive.FltPrim.ObjObj_Flt;
import primal.primitive.FltPrim.Obj_Flt;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsFlt {

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
			var pair = Pair.<K, V>of(null, null);
			var source = puller.source();
			var result = (float) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
