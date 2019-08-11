package primal.primitive;

import java.util.List;

import primal.Verbs.Take;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.FltPrim.Obj_Flt;
import primal.primitive.adt.Floats.FloatsBuilder;
import primal.primitive.adt.FltMutable;
import primal.primitive.adt.map.FltObjMap;
import primal.primitive.adt.map.ObjFltMap;
import primal.primitive.adt.set.FltSet;
import primal.primitive.fp.FltFunUtil;
import primal.primitive.puller.FltObjPuller;
import primal.primitive.puller.FltPuller;
import primal.primitive.streamlet.FltObjStreamlet;
import primal.primitive.streamlet.FltStreamlet;
import primal.puller.Puller;

public class FltMoreVerbs {

	public static class ConcatFlt {
		@SafeVarargs
		public static <T> FltStreamlet of(FltStreamlet... streamlets) {
			return new FltStreamlet(() -> {
				var source = Take.from(streamlets);
				return FltPuller.of(FltFunUtil.concat(FunUtil.map(st -> st.puller().source(), source)));
			});
		}

	}

	public static class IntersectFlt {
		public static FltSet of(FltSet... sets) {
			return ReadFlt.from(sets[0]).filter(c -> {
				var b = true;
				for (var set_ : sets)
					b &= set_.contains(c);
				return b;
			}).toSet();
		}
	}

	public static class LiftFlt {
		public static <T> Fun<Puller<T>, FltStreamlet> of(Obj_Flt<T> fun0) {
			var fun1 = fun0.rethrow();
			return ts -> {
				var b = new FloatsBuilder();
				T t;
				while ((t = ts.pull()) != null)
					b.append(fun1.apply(t));
				return new FltStreamlet(b.toFloats()::puller);
			};
		}
	}

	public static class ReadFlt {
		public static FltStreamlet for_(float s, float e) {
			return new FltStreamlet(() -> {
				var m = FltMutable.of(s);
				return FltPuller.of(() -> {
					var c = m.increment();
					return c < e ? c : FltPrim.EMPTYVALUE;
				});
			});
		}

		public static FltStreamlet from(float... ts) {
			return new FltStreamlet(() -> FltPuller.of(ts));
		}

		public static FltStreamlet from(float[] ts, int start, int end, int inc) {
			return new FltStreamlet(() -> FltPuller.of(ts, start, end, inc));
		}

		public static FltStreamlet from(FltSet set) {
			return new FltStreamlet(() -> FltPuller.of(set.source()));
		}

		public static <V> FltObjStreamlet<V> from2(FltObjMap<V> map) {
			return new FltObjStreamlet<>(() -> FltObjPuller.of(map.source()));
		}

		public static <V> FltObjStreamlet<V> from2(ObjFltMap<V> map) {
			return new FltObjStreamlet<>(() -> FltObjPuller.of(map.source()));
		}

		public static <V> FltObjStreamlet<V> from2(Source<FltObjPuller<V>> puller) {
			return new FltObjStreamlet<>(puller);
		}

		public static <V> FltObjStreamlet<List<V>> from2(ListMultimap<Float, V> multimap) {
			return new FltObjStreamlet<>(() -> {
				var iter = multimap.map.entrySet().iterator();
				return FltObjPuller.of(pair -> {
					var b = iter.hasNext();
					if (b) {
						var e = iter.next();
						pair.update(e.getKey(), e.getValue());
					}
					return b;
				});
			});
		}
	}

	public static class UnionFlt {
		public static FltSet of(FltSet... sets) {
			var set = new FltSet();
			for (var set_ : sets)
				ReadFlt.from(set_).sink(set::add);
			return set;
		}
	}

}
