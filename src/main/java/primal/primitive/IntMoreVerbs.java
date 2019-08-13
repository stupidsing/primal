package primal.primitive;

import java.util.List;

import primal.Verbs.Take;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.IntPrim.Obj_Int;
import primal.primitive.adt.Ints.IntsBuilder;
import primal.primitive.adt.map.IntObjMap;
import primal.primitive.adt.map.ObjIntMap;
import primal.primitive.adt.set.IntSet;
import primal.primitive.fp.IntFunUtil;
import primal.primitive.puller.IntObjPuller;
import primal.primitive.puller.IntPuller;
import primal.primitive.streamlet.IntObjStreamlet;
import primal.primitive.streamlet.IntStreamlet;
import primal.puller.Puller;

public class IntMoreVerbs {

	public static class ConcatInt {
		@SafeVarargs
		public static <T> IntStreamlet of(IntStreamlet... streamlets) {
			return new IntStreamlet(() -> {
				var source = Take.from(streamlets);
				return IntPuller.of(IntFunUtil.concat(FunUtil.map(st -> st.puller().source(), source)));
			});
		}

	}

	public static class IntersectInt {
		public static IntSet of(IntSet... sets) {
			return ReadInt.from(sets[0]).filter(c -> {
				var b = true;
				for (var set_ : sets)
					b &= set_.contains(c);
				return b;
			}).toSet();
		}
	}

	public static class LiftInt {
		public static <T> Fun<Puller<T>, IntStreamlet> of(Obj_Int<T> fun0) {
			var fun1 = fun0.rethrow();
			return ts -> {
				var b = new IntsBuilder();
				T t;
				while ((t = ts.pull()) != null)
					b.append(fun1.apply(t));
				return new IntStreamlet(b.toInts()::puller);
			};
		}
	}

	public static class ReadInt {
		public static IntStreamlet for_(int s, int e) {
			return new IntStreamlet(new Source<>() {
				private int m = s;

				public IntPuller g() {
					return IntPuller.of(() -> {
						var c = m++;
						return c < e ? c : IntPrim.EMPTYVALUE;
					});
				}
			});
		}

		public static IntStreamlet from(int... ts) {
			return new IntStreamlet(() -> IntPuller.of(ts));
		}

		public static IntStreamlet from(int[] ts, int start, int end, int inc) {
			return new IntStreamlet(() -> IntPuller.of(ts, start, end, inc));
		}

		public static IntStreamlet from(IntSet set) {
			return new IntStreamlet(() -> IntPuller.of(set.source()));
		}

		public static <V> IntObjStreamlet<V> from2(IntObjMap<V> map) {
			return new IntObjStreamlet<>(() -> IntObjPuller.of(map.source()));
		}

		public static <V> IntObjStreamlet<V> from2(ObjIntMap<V> map) {
			return new IntObjStreamlet<>(() -> IntObjPuller.of(map.source()));
		}

		public static <V> IntObjStreamlet<V> from2(Source<IntObjPuller<V>> puller) {
			return new IntObjStreamlet<>(puller);
		}

		public static <V> IntObjStreamlet<List<V>> from2(ListMultimap<Integer, V> multimap) {
			return new IntObjStreamlet<>(() -> {
				var iter = multimap.map.entrySet().iterator();
				return IntObjPuller.of(pair -> {
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

	public static class UnionInt {
		public static IntSet of(IntSet... sets) {
			var set = new IntSet();
			for (var set_ : sets)
				ReadInt.from(set_).sink(set::add);
			return set;
		}
	}

}
