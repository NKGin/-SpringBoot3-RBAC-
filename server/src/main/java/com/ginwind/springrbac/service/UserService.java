package com.ginwind.springrbac.service;

import com.ginwind.springrbac.dto.LoginDTO;
import com.ginwind.springrbac.entity.User;



public interface UserService {
    /**
     * 微信登录
     * @param dto
     * @return
     */
    User login(LoginDTO dto);
}
