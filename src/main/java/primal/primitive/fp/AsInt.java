package primal.primitive.fp;

import primal.adt.Pair;
import primal.primitive.IntPrim.ObjObj_Int;
import primal.primitive.IntPrim.Obj_Int;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsInt {

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
			var pair = Pair.<K, V>of(null, null);
			var source = puller.source();
			var result = (int) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
