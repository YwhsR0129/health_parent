package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色持久层接口
 */
public interface RoleDao {

    /**
     * 根据用户id关联查询角色集合
     * @param userId
     * @return
     */
    public Set<Role> findRolesByUserId(Integer userId);

    /**
     * 获取全部角色列表
     * */
    Page<Role> findAllRolesByCondition(String value);

    void add(Role role);

    void addRelation(Map<String, Object> map);

    int selectRelatedUsers(Integer id);

    void deleteRelation(Integer id);

    void delete(Integer id);

    Role findById(Integer id);

    List<Integer> findPermissionIdsByRoleId(Integer roleId);

    void editRole(Role role);

    List<String> findAllRoleNames();

    List<String> findAllRoleKeywords();

    List<Role> findAll();

    int selectRelatedMenus(Integer id);
}
