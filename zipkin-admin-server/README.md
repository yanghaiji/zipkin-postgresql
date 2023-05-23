## zipkin-admin-server

### 修改配置文件

- 登录相关

将这里的配置修改成自己的数据库连接

```
  datasource:
    driverClassName: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/zipkin?rewriteBatchedStatements=true
    username: postgres
    password: postgres
```

- zipkin 持久化配置

将这里的配置修改成自己的数据库连接

```
zipkin:
  storage:
    StorageComponent: postgresql
    type: postgresql
    postgresql:
      host: ${PG_HOST:localhost}
      port: ${PG_TCP_PORT:5432}
      username: ${PG_USER:postgres}
      password: ${PG_PASS:postgres}
      db: ${PG_DB:zipkin}
      max-active: ${PG_MAX_CONNECTIONS:10}
      use-ssl: ${PG_USE_SSL:false}
```
