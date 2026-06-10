"""
用 baostock 采集沪深300历史日线（更稳定）
运行：python -u collect_baostock.py
"""
import baostock as bs
import pymysql
import time
import sys
from datetime import datetime, timedelta

# ========== 配置 ==========
MYSQL_HOST = "localhost"
MYSQL_PORT = 3306
MYSQL_USER = "root"
MYSQL_PASS = "123456"
MYSQL_DB   = "stock_db"
START_DATE = (datetime.now() - timedelta(days=90)).strftime("%Y-%m-%d")
END_DATE   = datetime.now().strftime("%Y-%m-%d")
# =========================

def get_conn():
    return pymysql.connect(host=MYSQL_HOST, port=MYSQL_PORT,
                           user=MYSQL_USER, password=MYSQL_PASS,
                           database=MYSQL_DB, charset="utf8mb4")

def main():
    conn = get_conn()
    c = conn.cursor()

    # 1. 获取沪深300成分股
    print("[1/4] 获取沪深300成分股...")
    import akshare as ak
    import requests
    hs300 = ak.index_stock_cons(symbol="000300")
    codes = hs300["品种代码"].tolist()
    names = hs300["品种名称"].tolist()
    print(f"  共 {len(codes)} 只成分股")

    # 2. 同步 stock_basic
    print("[2/4] 同步股票基础信息...")
    for code, name in zip(codes, names):
        ts_code = code + ("SH" if code.startswith("6") else "SZ")
        symbol = code[3:] if len(code) > 3 else code
        c.execute("""INSERT IGNORE INTO stock_basic (ts_code, symbol, name, list_status)
                     VALUES (%s, %s, %s, 'L')""", (ts_code, symbol, name))
    conn.commit()
    print("  OK")

    # 3. baostock 登录
    print("[3/4] 登录 baostock ...")
    lg = bs.login()
    print(f"  {lg.error_msg}")

    # 4. 逐只拉取
    print(f"\n[4/4] 采集日线 {START_DATE}~{END_DATE} ...")
    print(f"  预计耗时约 {len(codes)//2//60 + 1} 分钟\n")

    sql = """REPLACE INTO daily_quote
        (ts_code,trade_date,open,high,low,close,pre_close,`change`,pct_chg,vol,amount)
        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"""

    success = 0
    fail = 0

    for i, code in enumerate(codes):
        try:
            # baostock 格式: sh.600000 或 sz.000001
            market = "sh" if code.startswith("6") else "sz"
            bs_code = f"{market}.{code}"

            rs = bs.query_history_k_data_plus(
                bs_code,
                "date,open,high,low,close,preclose,volume,amount,pctChg",
                start_date=START_DATE, end_date=END_DATE,
                frequency="d", adjustflag="2"  # 2=前复权
            )

            if rs.error_code != '0':
                fail += 1
                if fail <= 5:
                    print(f"  !! {code} 查询失败: {rs.error_msg}")
                time.sleep(0.3)
                continue

            data_list = []
            while (rs.error_code == '0') & rs.next():
                data_list.append(rs.get_row_data())

            if not data_list:
                fail += 1
                time.sleep(0.3)
                continue

            for row in data_list:
                ts_code = code + ("SH" if code.startswith("6") else "SZ")
                c.execute(sql, (
                    ts_code, row[0].replace("-", ""),
                    float(row[1]), float(row[2]),
                    float(row[3]), float(row[4]),
                    float(row[5]) if row[5] else 0,  # pre_close
                    float(row[4]) - float(row[5]) if row[5] else 0,  # change
                    float(row[8]) if row[8] else 0,  # pct_chg
                    int(row[6]) if row[6] else 0,    # vol
                    float(row[7]) if row[7] else 0,  # amount
                ))
            success += 1

        except Exception as e:
            fail += 1
            if fail <= 5:
                print(f"  !! {code} 失败: {e}")

        if (i + 1) % 50 == 0:
            conn.commit()
            print(f"  进度: {i+1}/{len(codes)}, 成功 {success}, 失败 {fail}")
        time.sleep(0.3)

    conn.commit()
    c.close()
    conn.close()

    bs.logout()
    print(f"\n{'='*40}")
    print(f"  完成！成功 {success} 只, 失败 {fail} 只")
    print(f"  数据已写入 daily_quote 表")

if __name__ == "__main__":
    main()
