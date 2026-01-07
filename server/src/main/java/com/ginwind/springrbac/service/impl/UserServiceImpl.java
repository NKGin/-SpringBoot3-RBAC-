package com.ginwind.springrbac.service.impl;



import com.ginwind.springrbac.dto.LoginDTO;
import com.ginwind.springrbac.entity.User;
import com.ginwind.springrbac.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User login(LoginDTO dto) {

        // 5.否则，直接返回user对象数据
        return new User();
    }
}
