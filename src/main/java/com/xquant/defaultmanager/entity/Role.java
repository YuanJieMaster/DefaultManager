package com.xquant.defaultmanager.entity;

public enum Role {
    RISK_CONTROL,      // 风控专员：负责提交“违约认定申请”和“违约重生申请”
    RISK_REVIEWER, //  风控审核员：负责审核“违约认定申请”和“违约重生申请”
    ADMIN      // 管理员：负责“违约风险原因维护”、用户管理、系统配置以及查看全系统范围的“违约统计”
}
