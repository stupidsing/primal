package primal.primitive.fp;

import primal.adt.Pair;
import primal.primitive.DblPrim.ObjObj_Dbl;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsDbl {

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
		var fun1 = fun0.rethrow();
		return puller -> {
			var pair = Pair.<K, V>of(null, null);
			var source = puller.source();
			var result = (double) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
