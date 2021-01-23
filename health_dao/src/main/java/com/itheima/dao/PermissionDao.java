package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Set;

/**
 * 权限持久层接口
 */
public interface PermissionDao {

    /**
     * 根据角色id关联查询权限集合
     * @param roleId
     * @return
     */
    public Set<Permission> findPermissionsByRoleId(Integer roleId);


    /**
     * 查询权限组
     * */
    Page<Permission> findPage(String queryString);

    /**
     * 新增
     * */
    void add(Permission permission);

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
    void deleteById(Integer id);



    /**
     * 关联角色
     * */
    List<Role> findRoleById(Integer id);

    /**
     * 查询权限组名称
     * */
    Permission getByName(String name);

    List<Permission> findAll();

    //List<String> findPermissionNames();

    List<String> findAllPermissionNames();

    List<String> findAllPermissionKeywords();
}
