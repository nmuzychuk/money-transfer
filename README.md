# Money Transfer
[![Java CI with Maven](https://github.com/nmuzychuk/money-transfer/actions/workflows/maven.yml/badge.svg)](https://github.com/nmuzychuk/money-transfer/actions/workflows/maven.yml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6997b36b968b41cb964b3db3e4f75305)](https://www.codacy.com/manual/nmuzychuk/money-transfer)

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

## License
This project is released under the [MIT License](LICENSE.txt)
