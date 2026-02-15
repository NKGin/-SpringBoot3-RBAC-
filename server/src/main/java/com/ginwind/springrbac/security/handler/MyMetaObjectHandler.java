package com.ginwind.springrbac.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ginwind.springrbac.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 公共字段自动填充处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的填充策略
     * 对应 FieldFill.INSERT
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");

        // 从 SecurityContextHolder 中获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充创建人 ID
        this.strictInsertFill(metaObject, "createId", Long.class, currentUserId);

        // 填充更新人 ID
        this.strictInsertFill(metaObject, "updateId", Long.class, currentUserId);
    }

    /**
     * 更新时的填充策略
     * 对应 FieldFill.INSERT_UPDATE 或 FieldFill.UPDATE
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");

        // 从 SecurityContextHolder 中获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 填充更新人ID（建议从当前登录上下文获取）
        this.strictInsertFill(metaObject, "updateId", Long.class, currentUserId);
    }
}