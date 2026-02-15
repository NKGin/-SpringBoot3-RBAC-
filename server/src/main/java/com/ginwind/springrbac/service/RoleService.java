package com.ginwind.springrbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginwind.springrbac.dto.RoleDTO;
import com.ginwind.springrbac.dto.RolePageDTO;
import com.ginwind.springrbac.entity.Role;
import com.ginwind.springrbac.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {
    Page<Role> pageQuery(RolePageDTO dto);
    void saveRole(RoleDTO roleDTO);
    void updateRole(RoleDTO roleDTO);
    void deleteRole(Integer id);
    RoleVO getRoleDetail(Integer id);
}