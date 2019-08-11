package primal.primitive;

import java.util.List;

import primal.Verbs.Take;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.LngPrim.Obj_Lng;
import primal.primitive.adt.LngMutable;
import primal.primitive.adt.Longs.LongsBuilder;
import primal.primitive.adt.map.LngObjMap;
import primal.primitive.adt.map.ObjLngMap;
import primal.primitive.adt.set.LngSet;
import primal.primitive.fp.LngFunUtil;
import primal.primitive.puller.LngObjPuller;
import primal.primitive.puller.LngPuller;
import primal.primitive.streamlet.LngObjStreamlet;
import primal.primitive.streamlet.LngStreamlet;
import primal.puller.Puller;

public class LngMoreVerbs {

	public static class ConcatLng {
		@SafeVarargs
		public static <T> LngStreamlet of(LngStreamlet... streamlets) {
			return new LngStreamlet(() -> {
				var source = Take.from(streamlets);
				return LngPuller.of(LngFunUtil.concat(FunUtil.map(st -> st.puller().source(), source)));
			});
		}

	}

	public static class IntersectLng {
		public static LngSet of(LngSet... sets) {
			return ReadLng.from(sets[0]).filter(c -> {
				var b = true;
				for (var set_ : sets)
					b &= set_.contains(c);
				return b;
			}).toSet();
		}
	}

	public static class LiftLng {
		public static <T> Fun<Puller<T>, LngStreamlet> of(Obj_Lng<T> fun0) {
			var fun1 = fun0.rethrow();
			return ts -> {
				var b = new LongsBuilder();
				T t;
				while ((t = ts.pull()) != null)
					b.append(fun1.apply(t));
				return new LngStreamlet(b.toLongs()::puller);
			};
		}
	}

	public static class ReadLng {
		public static LngStreamlet for_(long s, long e) {
			return new LngStreamlet(() -> {
				var m = LngMutable.of(s);
				return LngPuller.of(() -> {
					var c = m.increment();
					return c < e ? c : LngPrim.EMPTYVALUE;
				});
			});
		}

		public static LngStreamlet from(long... ts) {
			return new LngStreamlet(() -> LngPuller.of(ts));
		}

		public static LngStreamlet from(long[] ts, int start, int end, int inc) {
			return new LngStreamlet(() -> LngPuller.of(ts, start, end, inc));
		}

		public static LngStreamlet from(LngSet set) {
			return new LngStreamlet(() -> LngPuller.of(set.source()));
		}

		public static <V> LngObjStreamlet<V> from2(LngObjMap<V> map) {
			return new LngObjStreamlet<>(() -> LngObjPuller.of(map.source()));
		}

		public static <V> LngObjStreamlet<V> from2(ObjLngMap<V> map) {
			return new LngObjStreamlet<>(() -> LngObjPuller.of(map.source()));
		}

		public static <V> LngObjStreamlet<V> from2(Source<LngObjPuller<V>> puller) {
			return new LngObjStreamlet<>(puller);
		}

		public static <V> LngObjStreamlet<List<V>> from2(ListMultimap<Long, V> multimap) {
			return new LngObjStreamlet<>(() -> {
				var iter = multimap.map.entrySet().iterator();
				return LngObjPuller.of(pair -> {
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

	public static class UnionLng {
		public static LngSet of(LngSet... sets) {
			var set = new LngSet();
			for (var set_ : sets)
				ReadLng.from(set_).sink(set::add);
			return set;
		}
	}

}
