package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.TreeNode;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Map;

/**
 * 系统菜单_权限
 * */
public interface PermissionService {

    /**
     * 查询分页权限
     * */
    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

    /**
     * 新增权限
     * */
    Boolean add(Permission permission);

    /**
     * 编辑
     * */
    void edit(Permission permission);

    /**
     * 查询权限
     * */
    Permission findById(Integer id);

    /**
     * 删除
     * */
    Boolean deleteById(Integer id);

    /**
     * 关联角色
     * */
    List<Role> findRoleById(Integer id);

    List<Permission> findAll();

    List<TreeNode> findByDescendants();

    /**
     * 获取全部权限的名称和关键字封装到result对象中
     * @return
     * */
    List<Map> findPermissionNames();

}

