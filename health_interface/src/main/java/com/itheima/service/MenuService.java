package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Menu;

import java.util.List;
import java.util.Map;

/**
 * @Author Sven Yang
 * @Date 2020/12/24 20:53
 */

public interface MenuService {
    void add(Menu menu);

    /**
     * 根据id查主菜单
     * @param id
     * @return
     */
    Menu findById(Integer id);

    /**
     * 编辑主菜单
     * @param menu
     */
    void edit(Menu menu);

    void deleteById(Integer id);

    List<Map> findAll();

    /**
     * 删除子菜单
     * @param id
     */
    void deleteChildById(Integer id);

    /**
     * 新增二级菜单
     * @param menu
     */
    void addChild(Menu menu);

    /**
     * 编辑子菜单
     * @param menu
     */
    void editChild(Menu menu);

    List<Menu> findAllMenusByTree();

    List<Integer> findIdsByRoleId(Integer roleId);

    List<Map> findMenuNames();

    /**
     * 按名称查询
     * @param queryString
     * @return
     */
    List<Menu> findByName(String queryString);
}
