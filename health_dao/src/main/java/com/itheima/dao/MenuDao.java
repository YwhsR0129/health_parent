package com.itheima.dao;

import com.itheima.pojo.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface MenuDao {
    LinkedHashSet<Menu> findRelatedMenus(int role_id);

    /**
     * 新增主菜单
     * @param menu
     */
    void add(Menu menu);

    /**
     * 查所有 主菜单
     * @return
     */
    List<Map> findAll();

    /**
     * 查询当前主菜单的子菜单
     * @param menu
     * @return
     */
    List<Menu> findChildrenByMenu(Map menu);

    /**
     * 根据主菜单id查关联子菜单
     * @param id
     * @return
     */
    List<Menu> findChildrenByMenuId(@Param("id") Integer id);

    /**
     * 删除主菜单
     * @param id
     */
    void deleteById(@Param("id") Integer id);

    /**
     * 删除子菜单
     * @param id
     */
    void deleteChildById(@Param("id") Integer id);

    /**
     * 新增二级菜单
     * @param childMenu
     */
    void addChild(Menu childMenu);

    /**
     * 根据id查主菜单索引
     * @param childMenu
     * @return
     */
    String findPathById(Menu childMenu);

    /**
     * 根据id查主菜单
     * @param id
     * @return
     */
    Menu findById(@Param("id") Integer id);

    /**
     * 编辑主菜单
     * @param menu
     */
    void edit(Menu menu);

    List<Menu> findParentMenus();

    List<Integer> findByRoleId(Integer roleId);

    void deleteRelationByRoleId(int roleId);

    void addRoleRelation(@Param(value = "param1") int roleId, @Param(value = "param2") Integer menuId);

    /**
     * 校验一级菜单重复
     * @param menu
     * @return
     */
    List<Menu> checkMenu(Menu menu);

    /**
     * 校验二级菜单重复
     * @param menu
     * @return
     */
    List<Menu> checkChildMenu(Menu menu);

    List<Menu> findMenusByRoleId(Integer id);

    /**
     * 清除菜单-角色关联信息
     * @param id
     */
    void deleteMenuRole(@Param("id") Integer id);

    /**
     * 插入关系表
     * @param menuRoleMap
     */
    void addMenuRole(Map menuRoleMap);

    List<String> findMenuNames();

    /**
     * 按名称查询 模糊查询
     * @param queryString
     * @return
     */
    List<Menu> findByName(@Param("queryString") String queryString);

    /**
     * 按名称查询 和 父菜单id 模糊查询子菜单
     * @param queryString
     * @param parentMenuId
     */
    List<Menu> findChildByName(@Param("queryString") String queryString, @Param("parentMenuId") Integer parentMenuId);

    /**
     * 查所有主菜单
     * @return
     */
    List<Menu> findMenuAll();
}
