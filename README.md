### How to run this?
mvn eclipse:clean eclipse:eclipse install assembly:single && java -jar target/primal-1.0-jar-with-dependencies.jar

### How to rename this?
find -type f | xargs sed s/primal/newname/g

