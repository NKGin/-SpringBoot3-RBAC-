package com.ginwind.springrbac.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ginwind.springrbac.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");

        Long currentUserId = SecurityUtils.getCurrentUserId();

        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createId", Long.class, currentUserId);
        this.strictInsertFill(metaObject, "updateId", Long.class, currentUserId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");

        Long currentUserId = SecurityUtils.getCurrentUserId();

        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateId", Long.class, currentUserId);
    }
}