# Java 功能开发 Prompt 模板

复制以下内容到 Cursor 对话，**替换尖括号部分**后发送。  
规范已由 `.cursor/rules/` 自动加载，本模板补充任务上下文。

---

## 模板

```
请按团队规范实现以下功能（Building ManOS）。

【开发者】<你的姓名，用于 @author>
【模块】<如：BuildingDao / PurchaseService / MenuController>
【功能】<一句话描述>

【要求】
- 分层：cli → service → dao，不跨层
- MySQL + PreparedStatement
- 完整 JavaDoc：@author @since @param @return
- 表结构参考 sql/schema.sql

【范围】
仅修改/新增必要文件，不要重构无关代码。

若需新建多个类，按 model → dao → service → cli 顺序实现。
```

---

## 示例：实现楼盘 DAO

```
请按团队规范实现以下功能（Building ManOS）。

【开发者】张三
【模块】BuildingDao + Building（若 model 尚无）
【功能】楼盘表的 insert、findById、findAll、deleteById

【要求】
- 分层：仅 dao + model，不写 service/cli
- MySQL + PreparedStatement
- 完整 JavaDoc：@author @since @param @return
- 表结构参考 sql/schema.sql

【范围】
仅修改/新增必要文件。
```

---

## 示例：实现购买流程

```
请按团队规范实现以下功能（Building ManOS）。

【开发者】李四
【模块】PurchaseService + PercentageDiscount
【功能】选择在售房屋，应用折扣策略，更新 house 状态并写入 sale_record

【要求】
- 使用 DiscountStrategy 策略模式
- 防止重复购买（status 校验）
- 完整 JavaDoc

【范围】
不修改 cli，仅 service + discount；dao 方法可新增但需说明。
```
