package primal.primitive.fp;

import primal.adt.Pair;
import primal.primitive.ChrPrim.ObjObj_Chr;
import primal.primitive.ChrPrim.Obj_Chr;
import primal.puller.Puller;
import primal.puller.Puller2;

public class AsChr {

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
			var pair = Pair.<K, V>of(null, null);
			var source = puller.source();
			var result = (char) 0;
			while (source.source2(pair))
				result += fun1.apply(pair.k, pair.v);
			return result;
		};
	}

}
