package com.ginwind.springrbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ginwind.springrbac.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}