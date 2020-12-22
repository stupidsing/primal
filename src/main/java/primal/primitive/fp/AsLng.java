package primal.primitive.fp;

import primal.adt.Pair;
import primal.primitive.LngPrim.ObjObj_Lng;
import primal.primitive.LngPrim.Obj_Lng;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsLng {

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
			var pair = Pair.<K, V>of(null, null);
			var source = puller.source();
			var result = (long) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
