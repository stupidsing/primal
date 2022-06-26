package primal.primitive;

import java.util.List;

import primal.Verbs.Take;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Source;
import primal.primitive.ChrPrim.Obj_Chr;
import primal.primitive.adt.Chars.CharsBuilder;
import primal.primitive.adt.map.ChrObjMap;
import primal.primitive.adt.map.ObjChrMap;
import primal.primitive.adt.set.ChrSet;
import primal.primitive.fp.ChrFunUtil;
import primal.puller.Puller;
import primal.puller.primitive.ChrObjPuller;
import primal.puller.primitive.ChrPuller;
import primal.streamlet.primitive.ChrObjStreamlet;
import primal.streamlet.primitive.ChrStreamlet;

public class ChrMoreVerbs {

	public static class ConcatChr extends primal.primitive.ChrVerbs.ConcatChr {
		@SafeVarargs
		public static <T> ChrStreamlet of(ChrStreamlet... streamlets) {
			return new ChrStreamlet(() -> {
				var source = Take.from(streamlets);
				return ChrPuller.of(ChrFunUtil.concat(FunUtil.map(st -> st.puller().source(), source)));
			});
		}

	}

	public static class IntersectChr {
		public static ChrSet of(ChrSet... sets) {
			return ReadChr.from(sets[0]).filter(c -> {
				var b = true;
				for (var set_ : sets)
					b &= set_.contains(c);
				return b;
			}).toSet();
		}
	}

	public static class LiftChr {
		public static <T> Fun<Puller<T>, ChrStreamlet> of(Obj_Chr<T> fun0) {
			var fun1 = fun0.rethrow();
			return ts -> {
				var b = new CharsBuilder();
				T t;
				while ((t = ts.pull()) != null)
					b.append(fun1.apply(t));
				return new ChrStreamlet(b.toChars()::puller);
			};
		}
	}

	public static class ReadChr {
		public static ChrStreamlet for_(char s, char e) {
			return new ChrStreamlet(new Source<>() {
				private char m = s;

				public ChrPuller g() {
					return ChrPuller.of(() -> {
						var c = m++;
						return c < e ? c : ChrPrim.EMPTYVALUE;
					});
				}
			});
		}

		public static ChrStreamlet from(char... ts) {
			return new ChrStreamlet(() -> ChrPuller.of(ts));
		}

		public static ChrStreamlet from(char[] ts, int start, int end, int inc) {
			return new ChrStreamlet(() -> ChrPuller.of(ts, start, end, inc));
		}

		public static ChrStreamlet from(ChrSet set) {
			return new ChrStreamlet(() -> ChrPuller.of(set.source()));
		}

		public static <V> ChrObjStreamlet<V> from2(ChrObjMap<V> map) {
			return new ChrObjStreamlet<>(() -> ChrObjPuller.of(map.source()));
		}

		public static <V> ChrObjStreamlet<V> from2(ObjChrMap<V> map) {
			return new ChrObjStreamlet<>(() -> ChrObjPuller.of(map.source()));
		}

		public static <V> ChrObjStreamlet<V> from2(Source<ChrObjPuller<V>> puller) {
			return new ChrObjStreamlet<>(puller);
		}

		public static <V> ChrObjStreamlet<List<V>> from2(ListMultimap<Character, V> multimap) {
			return new ChrObjStreamlet<>(() -> {
				var iter = multimap.map.entrySet().iterator();
				return ChrObjPuller.of(pair -> {
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

	public static class UnionChr {
		public static ChrSet of(ChrSet... sets) {
			var set = new ChrSet();
			for (var set_ : sets)
				ReadChr.from(set_).sink(set::add);
			return set;
		}
	}

}
