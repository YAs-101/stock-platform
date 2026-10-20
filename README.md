# StockPulse — 股票检测统计平台 

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue)](https://spring.io/projects/spring-cloud)
[![JDK](https://img.shields.io/badge/JDK-17-orange)](https://adoptium.net/)
[![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D)](https://vuejs.org/)

基于 **Spring Cloud 微服务架构**的 A 股数据检测统计平台。提供股票搜索、K 线查询、异常检测预警、多维度统计排行及 ECharts 可视化仪表盘。


## 技术栈

| 层次 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2.5 / Spring Cloud 2023.0.1 / Spring Cloud Alibaba 2023.0.1.0 |
| 注册中心 | Nacos 2.4.1 |
| 网关 | Spring Cloud Gateway + LoadBalancer |
| 持久层 | MyBatis-Plus 3.5.9 / MySQL 8.0 |
| RPC | OpenFeign |
| 安全 | BCrypt / JJWT |
| 前端 | Vue 3 (CDN) / ECharts 5 / 原生 CSS |
| 构建 | Maven 3.9 / JDK 17 |
| 数据采集 | Python / baostock |

## 项目结构

```
stock-platform
├── common/             公共模块 — Result 封装 / JwtUtils / FeignClient 接口
├── stock-gateway/      网关服务 :9999 — 统一路由 / CORS / 负载均衡
├── stock-user/         用户服务 :8081 — 注册 / 登录 / JWT 签发
├── stock-data/         数据服务 :8082 — 股票搜索 / K线 / 基本面 / 资金流向
├── stock-detect/       检测服务 :8083 — 5条异常检测规则 / 预警记录
├── stock-stat/         统计服务 :8084 — 涨幅排行 / 成交量排行 / 行业统计
├── frontend/           前端仪表盘 — 4 视图单页应用（大盘/搜索/排行/预警）
├── scripts/            数据采集脚本 — baostock 历史行情采集
└── sql/                数据库初始化 SQL
```

## 快速启动

```bash
# 1. 启动 Nacos（需 JDK 8/11）
cd /path/to/nacos/bin
startup.cmd -m standalone      # Windows
sh startup.sh -m standalone    # macOS / Linux

# 2. 创建数据库并导入表结构
mysql -u root -p < sql/init.sql

# 3. 采集数据（可选，或使用已有数据文件）
pip install baostock pymysql
python scripts/collect_baostock.py

# 4. IDEA 打开项目 → Maven Reload → 依次启动
#    stock-user → stock-data → stock-detect → stock-stat → stock-gateway

# 5. 打开前端
#    浏览器访问 http://localhost:8080
#    或直接打开 frontend/index.html
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Nacos | 8848 | 注册中心 + 配置中心 |
| stock-user | 8081 | 用户注册/登录 |
| stock-data | 8082 | 数据查询 |
| stock-detect | 8083 | 异常检测 |
| stock-stat | 8084 | 统计分析 |
| stock-gateway | 9999 | API 网关 |
| Frontend | 8080 | Python HTTP Server |

## API 路由（通过 Gateway）

| 路由前缀 | 目标服务 |
|----------|----------|
| `/api/user/**` | stock-user |
| `/api/stock/**` | stock-data |
| `/api/detect/**` | stock-detect |
| `/api/stat/**` | stock-stat |

## 数据库表

| 表名 | 说明 | 状态 |
|------|------|-----------|
| stock_basic | 股票基本信息 | 5807 条 |
| daily_quote | 日线行情 | 291,814 条 |
| daily_basic | 每日基本面 | 待采集 |
| money_flow | 资金流向 | 待采集 |
| detect_alert | 预警记录 | 运行时生成 |
| sys_user | 用户信息 | 运行时注册 |

## 完成度

- [x] 6 模块微服务架构
- [x] Nacos 服务注册发现
- [x] Gateway 统一路由 + CORS
- [x] 用户注册/登录（BCrypt + JWT）
- [x] 股票模糊搜索 + K 线查询
- [x] 5 条检测规则（涨停/跌停/放量/连涨/连跌）
- [x] 涨幅/跌幅/成交量/资金流向排行
- [x] ECharts 可视化仪表盘（4 视图）
- [ ] JWT 网关鉴权过滤器
- [ ] Swagger 接口文档
- [ ] Redis 缓存
- [ ] 实时行情数据接入
- [ ] Docker 容器化部署

## License

MIT
