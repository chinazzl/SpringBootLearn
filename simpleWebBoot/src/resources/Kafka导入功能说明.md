# Kafka Excel导入功能使用说明

## 功能概述
通过上传Excel模板文件，批量导入Kafka主题权限配置数据。

## API接口

### 1. 导入Excel
**接口地址：** `POST /kafka/import`

**请求参数：**
- `file`：MultipartFile类型，Excel文件（必填）
- `duplicateHandle`：字符串类型，重复数据处理方式（可选，默认为override）
  - `override`：覆盖已有数据
  - `skip`：跳过重复，仅新增

**请求示例（使用curl）：**
```bash
# 覆盖模式导入
curl -X POST "http://localhost:8080/kafka/import?duplicateHandle=override" \
  -F "file=@kafka_template.xlsx"

# 跳过重复模式导入
curl -X POST "http://localhost:8080/kafka/import?duplicateHandle=skip" \
  -F "file=@kafka_template.xlsx"
```

**响应结果：**
```json
{
  "success": true,
  "message": "导入成功，新增/覆盖5条",
  "totalCount": 6,
  "successCount": 5,
  "failCount": 1,
  "errorDetail": "第3行数据校验失败：字段不能为空\n"
}
```

### 2. 获取模板说明
**接口地址：** `GET /kafka/template/info`

**响应示例：**
```
Excel模板列名：魔方名称、主题名称、用户权限、用户名称、设置密码
支持格式：.xlsx, .xls, .csv
重复数据处理参数：
  - override：覆盖已有数据（默认）
  - skip：跳过重复，仅新增
```

## Excel模板格式

### 列名（必须严格匹配）
| 魔方名称 | 主题名称 | 用户权限 | 用户名称 | 设置密码 |
|---------|---------|---------|---------|---------|
| 测试魔方1 | test-topic-1 | read | user001 | pwd123 |
| 测试魔方1 | test-topic-2 | write | user002 | pwd456 |
| 生产魔方 | prod-topic-1 | admin | user003 | pwd789 |

### 字段说明
- **魔方名称**（必填）：必须在MAGIC_CUBE表中已存在
- **主题名称**（必填）：不存在则自动创建
- **用户权限**（必填）：权限类型，如read、write、admin等
- **用户名称**（必填）：不存在则自动创建
- **设置密码**（可选）：用户密码，新用户不填则默认为123456

## 业务逻辑说明

### 1. 魔方处理
- 魔方必须预先在MAGIC_CUBE表中存在
- 如果魔方不存在，该行数据导入失败

### 2. 主题处理
- 如果主题不存在，自动创建新主题
- 如果主题已存在，直接使用现有主题ID

### 3. 用户处理
- 如果用户不存在，自动创建新用户
- 新用户密码使用Excel中的"设置密码"，为空则默认123456
- 如果用户已存在，直接使用现有用户ID

### 4. 权限处理
- 根据"魔方ID + 主题ID + 用户ID"判断权限是否存在
- **覆盖模式（override）**：存在则更新权限类型，不存在则新增
- **跳过模式（skip）**：存在则跳过不处理，不存在则新增

## 支持的文件格式
- `.xlsx`（Excel 2007及以上版本）
- `.xls`（Excel 2003版本）
- `.csv`（逗号分隔值文件）

## 文件大小限制
- 最大支持 10MB

## 错误处理
- 文件格式不正确：返回错误提示
- 字段校验失败：记录行号和错误信息
- 魔方不存在：该行导入失败
- 其他异常：记录详细错误信息

## 导入结果说明
- `totalCount`：总行数
- `successCount`：成功处理的行数（新增+覆盖）
- `failCount`：失败行数
- `errorDetail`：详细错误信息（包含行号）

## 数据库表结构

### MAGIC_CUBE（魔方表）
- ID：主键
- MAGIC_NAME：魔方名称（唯一）

### KAFKA_TOPIC（Kafka主题表）
- ID：主题ID
- TOPIC_NAME：主题名称（唯一）

### KAFKA_MNG_USER（Kafka用户管理表）
- ID：用户ID
- USER_NAME：用户名称（唯一）
- USER_PWD：用户密码

### KAFKA_TOPIC_PERMISSION（Kafka主题权限表）
- ID：主键
- MAGIC_ID：魔方ID（外键）
- TOPIC_ID：主题ID（外键）
- USER_ID：用户ID（外键）
- PERMISSION_TYPE：权限类型
- 联合唯一索引：(MAGIC_ID, TOPIC_ID, USER_ID)

## 使用示例

### Postman测试
1. 选择POST方法
2. 输入URL：`http://localhost:8080/kafka/import?duplicateHandle=override`
3. 选择Body -> form-data
4. 添加key为`file`，类型选择File
5. 选择Excel文件
6. 点击Send发送请求

### 前端HTML示例
```html
<form id="uploadForm" enctype="multipart/form-data">
  <input type="file" name="file" id="fileInput" accept=".xlsx,.xls,.csv" />
  <select name="duplicateHandle">
    <option value="override">覆盖已有数据</option>
    <option value="skip">跳过重复，仅新增</option>
  </select>
  <button type="submit">上传导入</button>
</form>

<script>
document.getElementById('uploadForm').onsubmit = async (e) => {
  e.preventDefault();
  const formData = new FormData(e.target);
  const response = await fetch('/kafka/import?' + new URLSearchParams({
    duplicateHandle: formData.get('duplicateHandle')
  }), {
    method: 'POST',
    body: formData
  });
  const result = await response.json();
  alert(result.message);
};
</script>
```
