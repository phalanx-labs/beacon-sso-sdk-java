# Beacon SSO SDK Java

面向 Spring Boot 的 Beacon SSO SDK，提供 OAuth2 登录回调、登出、认证状态查询，以及基于 gRPC 的用户与账号能力。

**模块**
- `bamboo-sso-base`: 核心逻辑、配置与 gRPC 访问层
- `bamboo-sso-sdk-springboot`: Spring Boot Starter（Controller/Filter/AutoConfig）

**Maven 坐标**
```xml
<dependency>
    <groupId>com.frontleaves.phalanx.beacon.sso</groupId>
    <artifactId>bamboo-sso-sdk-springboot</artifactId>
    <version>${version}</version>
</dependency>
```

可选仅引入 base 模块：
```xml
<dependency>
    <groupId>com.frontleaves.phalanx.beacon.sso</groupId>
    <artifactId>bamboo-sso-base</artifactId>
    <version>${version}</version>
</dependency>
```

**快速开始**
1. 引入依赖
2. 配置 `application.yml`
```yaml
beacon:
  sso:
    enabled: true
    base-url: https://sso.example.com
    client-id: your-client-id
    client-secret: your-client-secret
    redirect-uri: https://your-app.example.com/oauth/callback
    well-known-uri: https://sso.example.com/.well-known/openid-configuration
    endpoints:
      auth-uri: /oauth/authorize
      token-uri: /oauth/token
      userinfo-uri: /oauth/userinfo
      introspection-uri: /oauth/introspect
      revocation-uri: /oauth/revoke
    grpc:
      enabled: true
      host: sso-grpc.example.com
      port: 9090
      app-access-id: your-app-access-id
      app-secret-key: your-app-secret-key
```

`well-known-uri` 与 `endpoints` 二选一即可；启用 gRPC 需要填写 `grpc` 相关配置。

**HTTP 端点**
- `GET /oauth/login` 登录跳转
- `GET /oauth/callback` 登录回调
- `GET /oauth/logout` 登出注销
- `GET /oauth/status` 认证状态
- `GET /user/userinfo` 获取当前用户信息（需要 gRPC + Authorization）
- `POST /account/register/email` 邮箱注册（需要 gRPC）
- `POST /account/login/password` 密码登录（需要 gRPC）
- `POST /account/password/change` 修改密码（需要 gRPC）

**调用示例**
获取当前用户信息：
```bash
curl -H "Authorization: Bearer <access_token>" \
  http://localhost:8080/user/userinfo
```

**构建与测试**
```bash
mvn -q -DskipTests compile
```

