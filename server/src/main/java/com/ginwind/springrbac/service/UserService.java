package com.ginwind.springrbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginwind.springrbac.dto.PasswordEditDTO;
import com.ginwind.springrbac.dto.UserDTO;
import com.ginwind.springrbac.dto.UserPageDTO;
import com.ginwind.springrbac.entity.User;
import com.ginwind.springrbac.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户
     */
    Page<UserVO> pageQuery(UserPageDTO userPageDTO);

    /**
     * 保存用户
     */
    void saveUser(UserDTO userDTO);

    /**
     * 更新用户
     */
    void updateUser(UserDTO userDTO);

    /**
     * 批量删除用户
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新用户状态
     */
    void updateStatus(Long id, String status);

    /**
     * 重置用户密码
     */
    void resetPassword(Long id);

    /**
     * 修改密码
     */
    void editPassword(PasswordEditDTO passwordEditDTO);

    /**
     * 获取用户详情
     */
    UserVO getUserDetail(Long id);
}
