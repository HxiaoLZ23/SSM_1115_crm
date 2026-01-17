# ⚠️ 重要：首次启动前必读

## 🔴 配置文件缺失提醒

如果你是首次克隆或拉取项目，**必须**先创建配置文件才能启动项目！

---

## ✅ 必需操作（3步）

### 第1步：创建配置文件

复制配置模板：

**方法1：使用命令行**
```bash
cd f1115-main/src/main/resources/
copy db.properties.example db.properties
```

**方法2：在IDEA中**
1. 右键点击 `db.properties.example`
2. 选择 `Copy`
3. 右键点击 `resources` 文件夹
4. 选择 `Paste`
5. 重命名为 `db.properties`

### 第2步：修改配置

编辑 `f1115-main/src/main/resources/db.properties`：

```properties
# 修改数据库密码
jdbc.password=YOUR_MYSQL_PASSWORD

# 修改Redis密码（如果有）
redis.password=YOUR_REDIS_PASSWORD

# 后续开发AI功能时配置
dashscope.apiKey=YOUR_DASHSCOPE_API_KEY
```

### 第3步：验证配置

确认以下信息：
- ✅ MySQL数据库已启动
- ✅ Redis服务已启动
- ✅ 数据库用户名密码正确
- ✅ 数据库 `f1115_db` 已创建

---

## 🚨 常见错误

### 错误1：找不到配置文件
```
Could not resolve placeholder 'jdbc.password'
```

**原因**：没有创建 `db.properties` 文件

**解决**：按照上面的步骤创建配置文件

### 错误2：数据库连接失败
```
Access denied for user 'root'@'localhost'
```

**原因**：数据库密码不正确

**解决**：检查 `db.properties` 中的 `jdbc.password`

### 错误3：Redis连接失败
```
Unable to connect to Redis
```

**原因**：Redis服务未启动

**解决**：启动Redis服务 `redis-server`

---

## 📚 详细文档

- [CONFIG_GUIDE.md](CONFIG_GUIDE.md) - 完整配置指南
- [QUICK_START.md](QUICK_START.md) - 快速启动指南
- [README.md](README.md) - 项目说明

---

## 🔒 安全提示

**永远不要**将包含真实密码的 `db.properties` 提交到Git！

该文件已添加到 `.gitignore`，Git会自动忽略它。

---

**配置完成后，请查看 [QUICK_START.md](QUICK_START.md) 启动项目！🚀**
