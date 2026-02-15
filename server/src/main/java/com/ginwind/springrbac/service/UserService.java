package com.ginwind.springrbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginwind.springrbac.dto.PasswordEditDTO;
import com.ginwind.springrbac.dto.UserDTO;
import com.ginwind.springrbac.dto.UserPageDTO;
import com.ginwind.springrbac.entity.User;
import com.ginwind.springrbac.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {
    // 分页查询
    Page<UserVO> pageQuery(UserPageDTO userPageDTO);
    // 新增用户
    void saveUser(UserDTO userDTO);
    // 修改用户
    void updateUser(UserDTO userDTO);
    // 删除用户
    void deleteBatch(List<Long> ids);
    // 启用/禁用
    void updateStatus(Long id, String status);
    // 重置密码
    void resetPassword(Long id);
    // 修改密码（个人）
    void editPassword(PasswordEditDTO passwordEditDTO);
    // 获取详情
    UserVO getUserDetail(Long id);
}