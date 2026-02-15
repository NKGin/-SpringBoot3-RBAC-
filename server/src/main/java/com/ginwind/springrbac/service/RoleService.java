package com.ginwind.springrbac.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginwind.springrbac.dto.RoleDTO;
import com.ginwind.springrbac.dto.RolePageDTO;
import com.ginwind.springrbac.entity.Role;
import com.ginwind.springrbac.vo.RoleVO;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色
     */
    Page<Role> pageQuery(RolePageDTO dto);

    /**
     * 保存角色
     */
    void saveRole(RoleDTO roleDTO);

    /**
     * 更新角色
     */
    void updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     */
    void deleteRole(Integer id);

    /**
     * 获取角色详情
     */
    RoleVO getRoleDetail(Integer id);
}
