-- StockPulse 股票检测统计平台 · 数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS stock_db DEFAULT CHARACTER SET utf8mb4;
USE stock_db;

-- 股票基本信息
CREATE TABLE IF NOT EXISTS stock_basic (
    ts_code      VARCHAR(20) PRIMARY KEY,
    symbol       VARCHAR(10),
    name         VARCHAR(30),
    area         VARCHAR(20),
    industry     VARCHAR(50),
    list_date    VARCHAR(10),
    market       VARCHAR(10),
    list_status  CHAR(1) DEFAULT 'L'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 日线行情
CREATE TABLE IF NOT EXISTS daily_quote (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    ts_code      VARCHAR(20),
    trade_date   VARCHAR(10),
    open         DECIMAL(10,2),
    high         DECIMAL(10,2),
    low          DECIMAL(10,2),
    close        DECIMAL(10,2),
    pre_close    DECIMAL(10,2),
    change       DECIMAL(10,2),
    pct_chg      DECIMAL(8,4),
    vol          BIGINT,
    amount       DECIMAL(20,2),
    INDEX idx_ts_code (ts_code),
    INDEX idx_trade_date (trade_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 每日基本面指标
CREATE TABLE IF NOT EXISTS daily_basic (
    ts_code      VARCHAR(20),
    trade_date   VARCHAR(10),
    close        DECIMAL(10,2),
    pe_ttm       DECIMAL(12,4),
    pb           DECIMAL(10,4),
    total_mv     DECIMAL(20,2),
    circ_mv      DECIMAL(20,2),
    INDEX idx_ts_code (ts_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资金流向
CREATE TABLE IF NOT EXISTS money_flow (
    ts_code      VARCHAR(20),
    trade_date   VARCHAR(10),
    buy_lg       DECIMAL(20,2),
    sell_lg      DECIMAL(20,2),
    buy_elg      DECIMAL(20,2),
    sell_elg     DECIMAL(20,2),
    net_mf       DECIMAL(20,2),
    INDEX idx_ts_code (ts_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 指数日线
CREATE TABLE IF NOT EXISTS index_daily (
    ts_code      VARCHAR(20),
    trade_date   VARCHAR(10),
    close        DECIMAL(10,2),
    pct_chg      DECIMAL(8,4),
    INDEX idx_ts_code (ts_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统用户
CREATE TABLE IF NOT EXISTS sys_user (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50) NOT NULL UNIQUE,
    password     VARCHAR(200) NOT NULL,
    nick_name    VARCHAR(50),
    email        VARCHAR(100),
    phone        VARCHAR(20),
    status       INT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户关注股票
CREATE TABLE IF NOT EXISTS user_stock (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    ts_code      VARCHAR(20) NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 检测预警记录
CREATE TABLE IF NOT EXISTS detect_alert (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    ts_code      VARCHAR(20),
    name         VARCHAR(30),
    trade_date   VARCHAR(10),
    alert_type   VARCHAR(20),
    alert_msg    VARCHAR(200),
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
