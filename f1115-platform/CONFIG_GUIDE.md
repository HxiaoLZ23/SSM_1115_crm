# F1115 配置指南

## 📝 配置文件说明

本项目使用配置文件模板来保护敏感信息（如数据库密码、API密钥等）不被提交到Git仓库。

---

## 🔧 首次配置步骤

### 1. 复制配置模板

将 `db.properties.example` 复制为 `db.properties`：

**在项目目录中执行**：
```bash
cd f1115-main/src/main/resources/
cp db.properties.example db.properties
```

**或者在IDEA中**：
1. 右键点击 `db.properties.example`
2. 选择 `Copy`
3. 右键点击 `resources` 文件夹
4. 选择 `Paste`
5. 重命名为 `db.properties`

### 2. 修改配置文件

编辑 `db.properties`，填入你的真实配置：

```properties
# MySQL数据库配置（修改为你的实际配置）
jdbc.username=root
jdbc.password=YOUR_MYSQL_PASSWORD

# Redis配置（修改为你的实际配置）
redis.host=localhost
redis.port=6379
redis.password=YOUR_REDIS_PASSWORD

# 通义千问API配置（后续开发AI功能时配置）
dashscope.apiKey=YOUR_DASHSCOPE_API_KEY
```

---

## 🔒 安全说明

### 已添加到 .gitignore 的文件

以下文件不会被提交到Git：
- `**/db.properties` - 包含真实配置的文件
- `**/db.properties.prod` - 生产环境配置
- `**/db.properties.local` - 本地开发配置

### 会被提交到Git的文件

- `db.properties.example` - 配置模板（不包含敏感信息）

---

## 📋 配置项说明

### MySQL数据库配置

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| jdbc.driverClassName | 数据库驱动 | com.mysql.cj.jdbc.Driver |
| jdbc.url | 数据库连接URL | jdbc:mysql://localhost:3306/f1115_db?... |
| jdbc.username | 数据库用户名 | root |
| jdbc.password | 数据库密码 | YOUR_PASSWORD |

### Redis配置

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| redis.host | Redis服务器地址 | localhost |
| redis.port | Redis端口 | 6379 |
| redis.password | Redis密码 | 留空或填写密码 |
| redis.database | Redis数据库编号 | 0 |

### 通义千问API配置

| 配置项 | 说明 | 获取方式 |
|--------|------|---------|
| dashscope.apiKey | API密钥 | https://dashscope.aliyun.com/ |
| dashscope.model | 模型类型 | qwen-plus |

---

## 🚀 不同环境的配置

### 开发环境（本地）

创建 `db.properties`（已在.gitignore中）：
```properties
jdbc.password=your_local_password
redis.password=
dashscope.apiKey=YOUR_DEV_API_KEY
```

### 测试环境

创建 `db.properties.test`：
```properties
jdbc.url=jdbc:mysql://test-server:3306/f1115_db?...
jdbc.password=test_password
redis.host=test-redis-server
```

### 生产环境

创建 `db.properties.prod`（已在.gitignore中）：
```properties
jdbc.url=jdbc:mysql://prod-server:3306/f1115_db?...
jdbc.password=prod_password
redis.host=prod-redis-server
redis.password=prod_redis_password
dashscope.apiKey=PROD_API_KEY
```

---

## ⚠️ 注意事项

### 1. 不要提交敏感信息

**永远不要**将包含真实密码、API密钥的配置文件提交到Git！

### 2. 检查.gitignore

在提交前，确认 `.gitignore` 中已包含：
```
**/db.properties
**/db.properties.prod
**/db.properties.local
```

### 3. 团队协作

团队成员首次拉取代码后：
1. 复制 `db.properties.example` 为 `db.properties`
2. 填入自己的本地配置
3. 不要提交 `db.properties` 到Git

### 4. 配置变更

如果需要添加新的配置项：
1. 在 `db.properties.example` 中添加配置项（使用占位符）
2. 在 `db.properties` 中添加真实配置
3. 只提交 `db.properties.example` 的变更

---

## 🔍 验证配置

### 检查文件是否被忽略

```bash
# 查看哪些文件会被提交
git status

# db.properties 不应该出现在列表中
```

### 检查配置是否正确

启动项目后，查看日志：
- 数据库连接成功：`{dataSource-1} inited`
- Redis连接成功：无错误日志

---

## 📚 相关文档

- [QUICK_START.md](QUICK_START.md) - 快速启动指南
- [README.md](README.md) - 项目说明
- [.gitignore](.gitignore) - Git忽略规则

---

**配置完成后，即可启动项目！🚀**
