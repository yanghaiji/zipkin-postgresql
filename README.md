
## Zipkin PostgresSQL

[Zipkin PostgresSQL 中文介绍](README_zh.md)

> Due to the fact that Zipkin does not support PostgreSQL, but PostgreSQL was chosen as the data storage technology for the project, we searched online for a long time and finally found that [https://github.com/tian-junwei/zipkin-storage-postgresql ](https://github.com/tian-junwei/zipkin-storage-postgresql )could be used to implement it and we conducted secondary development and integration on it.

## Project Structure

```
├───doc
│ └───sql zikin                 initialization sql
├───zipkin-admin-server         zipkin server, default login user: yanghaiji, password: 654321
├───zipkin-example              Integration Example
│ ├───zipkin-example-gateway    API Gateway
│ └───zipkin-example-system     Example Service
└───zipkin-storage-postgresql   Zipkin Persistence Driver
```


### zipkin-storage-postgresql

Due to licensing issues with packaging, everyone can execute the following command.

```shell script
mvn clean  com.mycila:license-maven-plugin:format install -DskipTests  -Denforcer.skip=true 
```


### zipkin-server

[登录: http://localhost:9411/](http://localhost:9411/)

> username: yanghaiji
>
> password: 654321



Access test path.[http://localhost:9412/system/api/test/user](http://localhost:9412/system/api/test/user)

- zipkin portal

![login](doc/img/login.png)


- traces

![login](doc/img/20230524095710.png)


- dependency

![login](doc/img/20230524095817.png)

