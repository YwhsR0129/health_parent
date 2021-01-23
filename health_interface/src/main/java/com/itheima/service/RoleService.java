package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleService {
    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

    Set<Permission> findPermissionByRoleId(Integer id);

    void add(Role role, String[] permissionIds, Integer[] menuIds);

    void delete(Integer id);

    Role findById(Integer id);

    List<Integer> findPermissionIdsByRoleId(Integer roleId);

    void editRole(Role role, String[] permissionIds, Integer[] menuIds);

    List<Map> findAllRoleNames();

    List<Role> findAll();
}
