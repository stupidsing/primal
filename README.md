### How to run this?
src/main/sh/replace.sh && mvn eclipse:clean eclipse:eclipse install assembly:single && java -jar target/primal-1.0-jar-with-dependencies.jar

### How to rename this?
find -type f | xargs sed s/primal/newname/g

### Package dependencies

```
primal.statics.*
primal.Nouns
primal.io.*
primal.Verbs
primal.os.*
primal.adt.Fixie_
primal.adt.Fixie
primal.adt.FixieArray
primal.adt.Mutable
primal.NullableSyncQueue
primal.fp.Funs
primal.fp.Funs2
primal.adt.Opt
primal.adt.Pair
primal.fp.FunUtil
primal.fp.FunUtil2
	:
	:
	:
primal.primitive.Chr_Dbl
primal.primitive.Chr_DblFlt
primal.primitive.ChrVerbs
primal.primitive.ChrPrim
primal.primitive.ChrOpt
primal.primitive.adt.pair.ChrDblPair
primal.primitive.adt.pair.ChrObjPair
primal.primitive.adt.set.ChrSet
primal.primitive.fp.ChrFunUtil
primal.primitive.fp.ChrObjFunUtil
primal.primitive.puller.ChrPuller
primal.primitive.adt.Chars
primal.streamlet.Streamlet2
primal.primitive.streamlet.ChrObjStreamlet
primal.streamlet.Streamlet
primal.primitive.streamlet.ChrStreamlet
	:
	:
	:
primal.primitive.ChrMoreVerbs
primal.MoreVerbs
	:
	:
	:
primal.persistent.*
primal.parser.*
primal.Main
```
