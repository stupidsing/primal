### How to run this?
mvn eclipse:clean eclipse:eclipse install assembly:single && java -jar target/primal-1.0-jar-with-dependencies.jar

### How to rename this?
find -type f | xargs sed s/primal/newname/g

### Package dependencies

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
primal.adt.Pair
primal.fp.FunUtil
primal.fp.FunUtil2
	:
	:
	:
primal.Main
