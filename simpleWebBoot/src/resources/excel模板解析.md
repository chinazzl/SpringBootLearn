我给你提供一个excel模板，我现在要做的是一个SpringBoot项目，通过上传模板excel并解析处理一些逻辑的功能，请按照要求给我提供完整扩展性强、易读性好、健壮性强的代码。并且在 simpleWebBoot项目中生成代码。
使用技术栈：
Spring Boot、Mybatis、Hutool工具类进行处理excel
模板字段如下：
魔方名称、主题名称、用户权限、用户名称、设置密码。
表结构：

| 表名称        | 英文名                   | 字段                                                                                                          |
| ---------- | --------------------- | ----------------------------------------------------------------------------------------------------------- |
| KAKFA主题表   | KAFKA_TOPIC           | ID（主题ID）、TOPIC_NAME（主题名称）、CREATETIME、UPDATETIME、VERSION                                                     |
| KAFKA主题权限表 | KAFKA_TOPIC_PERMISSON | ID、MAGIC_ID（魔方id，通过魔方名称获取）、TOPIC_ID（主题ID）、USER_ID（用户ID）、PERMISSION_TYPE（用户权限）、CREATETIME、UPDATETIME、VERSION |
| KAFKA用户管理表 | KAFKA_MNG_USER        | ID（用户ID）、USER_NAME（用户名称）、USER_PWD（用户密码）、CREATETIME、UPDATETIME、VERSION                                       |

功能逻辑要求：
1. 魔方ID获取：通过魔方名称到MAGIC_CUBE表中进行获取。
2. 主题名称处理：如果KAFKA主题表中没有输入的主题名称，则进行添加，并获取ID，有则不进行添加直接查询获取ID。
3. 用户名称处理：如果用户名称存在则直接获取用户ID，如果不存在则进行新增，后获取ID。
4. 如果魔方名称、用户名称、主题名称存在，则将excel进行更新，否则进行新增。
5. 如果前端传入“重复数据处理”参数为“覆盖已有参数”，则按照新增/更新处理，如果传入“跳过重复，仅新增”参数，则将没有改动的数据进行忽略。
代码要求：
1. 上传支持.xlsx、.xls、.csv格式
2. 导入成功后，展示新增/覆盖n条，如果导入失败则提示新增/覆盖0条。
