package com.ginwind.springrbac.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginwind.springrbac.entity.*;
import com.ginwind.springrbac.mapper.*;
import com.ginwind.springrbac.security.domain.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username){
        if (username.isEmpty()){
            throw new InternalAuthenticationServiceException("用户名为空");
        }

        // 1查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 2查询用户角色
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.eq("user_id", user.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 3查询角色对应的权限
        QueryWrapper<RolePermission> rolePermWrapper = new QueryWrapper<>();
        rolePermWrapper.in("role_id", roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermWrapper);
        List<Integer> permIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        // 4查询权限名
        List<String> permissions = permissionMapper.selectBatchIds(permIds)
                .stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toList());
        log.info(permissions.toString());

        // 5封装 LoginUser
        return LoginUser.builder()
                .password(user.getPassword())
                .username(user.getUsername())
                .status(user.getStatus())
                .permissions(permissions).build();
    }
}
