# SpringRBAC-Manager

**SpringRBAC-Manager** 是一个轻量级、高扩展性的企业级后台权限管理系统后端。采用标准的 **RBAC (Role-Based Access Control)** 模型，基于 Spring Boot 3 和 Spring Security 6 开发，实现了用户、角色、权限的精细化控制。结合 JWT + Redis 机制，实现了高效且安全的无状态（支持服务端控制）认证鉴权。

## 📖 项目简介

本项目旨在提供一套开箱即用的权限管理脚手架。它将复杂的 Spring Security 配置进行了封装和优化，支持动态权限校验、统一异常处理、数据字段自动填充等企业级开发常用功能。

**核心亮点：**

* **安全认证**：基于 Spring Security + JWT 实现认证，配合 Redis 实现 Token 的双重校验（支持强制登出、Token 过期控制）。
* **权限控制**：支持方法级别的细粒度权限控制 (`@PreAuthorize`)。
* **架构设计**：采用 Maven 多模块架构（Server, Common, Pojo），结构清晰，易于维护。
* **开发体验**：集成 MyBatis-Plus、Lombok、Knife4j（接口文档），极大提升开发效率。

## 🛠 技术栈

| 技术 | 说明 | 版本 |
| --- | --- | --- |
| **Spring Boot** | 核心框架 | 3.5.9 |
| **Spring Security** | 安全认证与授权 | 6.x |
| **MyBatis-Plus** | ORM 框架 | 3.5.7 |
| **MySQL** | 关系型数据库 | 8.0+ |
| **Redis** | 缓存与 Token 存储 | Latest |
| **JWT (JJWT)** | Token 生成与解析 | 0.11.5 |
| **Knife4j** | 接口文档增强 | 4.5.0 |
| **Druid** | 数据库连接池 | 1.2.8 |
| **FastJSON** | JSON 序列化 | 1.2.83 |
| **Lombok** | 简化 Java 代码 | Latest |

## 🧩 模块结构

项目采用 Maven 多模块构建：

```text
SpringRBAC-Manager
├── common           // 公共模块：工具类(JwtUtil, RedisUtil)、通用常量、全局异常、统一返回结果
├── pojo             // 数据模型模块：Entity、DTO、VO
└── server           // 核心业务模块：Controller、Service、Mapper、Security配置、启动类

```

## ✨ 功能特性

### 1. 用户管理 (User Management)

* **基础操作**：用户的增删改查。
* **状态控制**：启用/禁用用户账号。
* **密码管理**：管理员重置密码、用户修改个人密码。
* **角色分配**：为用户分配多个角色。

### 2. 角色管理 (Role Management)

* **基础操作**：角色的创建、修改、删除（带关联校验）。
* **权限分配**：为角色勾选对应的功能权限。

### 3. 权限管理 (Permission Management)

* **接口权限**：基于 URL 路径和请求方法（GET/POST等）定义具体的资源权限。
* **动态加载**：支持后端动态配置权限标识。

### 4. 系统安全

* **登录认证**：JSON 格式登录，BCrypt 密码加密。
* **Token 机制**：
* Access Token 自动生成与校验。
* Redis 存储 Token 白名单，支持服务端主动失效 Token。


* **异常处理**：
* 401 未认证 (JwtAuthenticationEntryPoint)
* 403 权限不足 (JwtAccessDeniedHandler)
* 登录失败处理 (LoginFailureHandler)


* **审计日志**：MyBatis-Plus 自动填充创建人、创建时间、更新人、更新时间。

## 🚀 快速开始

### 环境要求

* **JDK**: 21
* **Maven**: 3.6+
* **MySQL**: 8.0+
* **Redis**: 5.0+

### 1. 数据库配置

创建一个名为 `spring_rbac_manager` 的数据库，并导入相关的建表语句（需根据 Entity 自行生成或使用 flyway，以下为核心表结构示意）：

* `user` (用户表)
* `role` (角色表)
* `permission` (权限表)
* `user_role` (用户角色关联表)
* `role_permission` (角色权限关联表)

### 2. 修改配置文件

打开 `server/src/main/resources/application-dev.yml`，配置你的数据库和 Redis 连接信息：

```yaml
rbac:
  datasource:
    host: localhost
    port: 3306
    database: spring_rbac_manager
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
  jwt:
    admin-secret-key: "your_secret_key" # 修改为复杂的密钥

```

### 3. 启动项目

运行 `server` 模块下的启动类：
`com.ginwind.springrbac.SpringRBACManager`

启动成功后，控制台将输出 Spring Boot 启动日志。

### 4. 接口文档

项目集成了 Knife4j，启动后访问地址查看 API 文档：
`http://localhost:8080/doc.html`

## 🔑 默认账号

* 项目中设置了默认密码常量 `PasswordConstant.DEFAULT_PASSWORD` 为 `123456`。
* 建议初始化数据库时插入一个超级管理员账号，并赋予所有权限。

## ⚙️ 核心配置说明

### Spring Security 自定义过滤器链

项目重写了标准的 Security 过滤器链，以适应前后端分离架构：

1. **JwtLoginFilter**: 拦截 `/login` 请求，校验 JSON 用户名密码，成功后生成 JWT 并存入 Redis。
2. **JwtAuthenticationFilter**: 拦截所有受保护请求，解析 Header 中的 Token，并校验 Redis 中是否存在该 Token。

### 权限控制注解

在 Controller 层使用注解进行权限控制：

```java
@PreAuthorize("hasAuthority('user:add')")
@PostMapping
public Result save(@RequestBody UserDTO userDTO) {
    userService.saveUser(userDTO);
    return Result.success();
}

```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 开源协议

[MIT License](https://www.google.com/search?q=LICENSE)