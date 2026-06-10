package com.stock.detect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("detect_alert")
public class DetectAlert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tsCode;
    private String name;
    private String tradeDate;
    private String alertType;
    private String alertMsg;
    private LocalDateTime createdAt;
}
