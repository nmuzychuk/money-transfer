# Money Transfer

## Overview
Transfers money between accounts. Requires redis.

## Usage
```
mvn package
java $JAVA_OPTS -cp 'target/classes:target/dependency/*' com.nmuzychuk.App

curl -X POST -d 'name=Bob&balance=100' http://localhost:4567/accounts
curl -X POST -d 'name=Greg&balance=100' http://localhost:4567/accounts

curl http://localhost:4567/accounts

curl -X POST -d 'sender=1&receiver=2&amount=60' http://localhost:4567/transfers
curl -X POST -d 'sender=1&receiver=2&amount=60' http://localhost:4567/transfers

curl http://localhost:4567/transfers
```

## Test
Run JUnit tests
```
mvn test
```
