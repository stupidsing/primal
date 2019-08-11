package primal.primitive;

import java.util.List;

import primal.Verbs.Take;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.primitive.adt.DblMutable;
import primal.primitive.adt.Doubles.DoublesBuilder;
import primal.primitive.adt.map.DblObjMap;
import primal.primitive.adt.map.ObjDblMap;
import primal.primitive.adt.set.DblSet;
import primal.primitive.fp.DblFunUtil;
import primal.primitive.puller.DblObjPuller;
import primal.primitive.puller.DblPuller;
import primal.primitive.streamlet.DblObjStreamlet;
import primal.primitive.streamlet.DblStreamlet;
import primal.puller.Puller;

public class DblMoreVerbs {

	public static class ConcatDbl {
		@SafeVarargs
		public static <T> DblStreamlet of(DblStreamlet... streamlets) {
			return new DblStreamlet(() -> {
				var source = Take.from(streamlets);
				return DblPuller.of(DblFunUtil.concat(FunUtil.map(st -> st.puller().source(), source)));
			});
		}

	}

	public static class IntersectDbl {
		public static DblSet of(DblSet... sets) {
			return ReadDbl.from(sets[0]).filter(c -> {
				var b = true;
				for (var set_ : sets)
					b &= set_.contains(c);
				return b;
			}).toSet();
		}
	}

	public static class LiftDbl {
		public static <T> Fun<Puller<T>, DblStreamlet> of(Obj_Dbl<T> fun0) {
			var fun1 = fun0.rethrow();
			return ts -> {
				var b = new DoublesBuilder();
				T t;
				while ((t = ts.pull()) != null)
					b.append(fun1.apply(t));
				return new DblStreamlet(b.toDoubles()::puller);
			};
		}
	}

	public static class ReadDbl {
		public static DblStreamlet for_(double s, double e) {
			return new DblStreamlet(() -> {
				var m = DblMutable.of(s);
				return DblPuller.of(() -> {
					var c = m.increment();
					return c < e ? c : DblPrim.EMPTYVALUE;
				});
			});
		}

		public static DblStreamlet from(double... ts) {
			return new DblStreamlet(() -> DblPuller.of(ts));
		}

		public static DblStreamlet from(double[] ts, int start, int end, int inc) {
			return new DblStreamlet(() -> DblPuller.of(ts, start, end, inc));
		}

		public static DblStreamlet from(DblSet set) {
			return new DblStreamlet(() -> DblPuller.of(set.source()));
		}

		public static <V> DblObjStreamlet<V> from2(DblObjMap<V> map) {
			return new DblObjStreamlet<>(() -> DblObjPuller.of(map.source()));
		}

		public static <V> DblObjStreamlet<V> from2(ObjDblMap<V> map) {
			return new DblObjStreamlet<>(() -> DblObjPuller.of(map.source()));
		}

		public static <V> DblObjStreamlet<V> from2(Source<DblObjPuller<V>> puller) {
			return new DblObjStreamlet<>(puller);
		}

		public static <V> DblObjStreamlet<List<V>> from2(ListMultimap<Double, V> multimap) {
			return new DblObjStreamlet<>(() -> {
				var iter = multimap.map.entrySet().iterator();
				return DblObjPuller.of(pair -> {
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

	public static class UnionDbl {
		public static DblSet of(DblSet... sets) {
			var set = new DblSet();
			for (var set_ : sets)
				ReadDbl.from(set_).sink(set::add);
			return set;
		}
	}

}
