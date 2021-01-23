package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.MenuDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.Menu;
import com.itheima.service.MemberService;
import com.itheima.service.MenuService;
import com.itheima.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单接口实现类
 *
 * @Author Sven Yang
 * @Date 2020/12/24 20:58
 */
@Service(interfaceClass = MenuService.class)
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private JedisPool jedisPool;
    /**
     * 新增主菜单
     *
     * @param menu
     */
    @Override
    public void add(Menu menu) {
        //校验重复
        List<Menu> menuList = menuDao.checkMenu(menu);
        if(menuList.size() > 0){
            throw new RuntimeException(MessageConstant.MENU_EXIST);
        }
        menuDao.add(menu);
        //插入关系表 menu role
        Map menuRoleMap = new HashMap();
        menuRoleMap.put("menu_id", menu.getId());
        //管理员id 默认添加
        menuRoleMap.put("role_id", 1);
        menuDao.addMenuRole(menuRoleMap);
    }

    /**
     * 根据id查主菜单
     * @param id
     * @return
     */
    @Override
    public Menu findById(Integer id) {
        return menuDao.findById(id);
    }

    /**
     * 编辑主菜单 关联修改子菜单path
     * @param menu
     */
    @Override
    public void edit(Menu menu) {
        //校验重复 根据name path查数据库
        List<Menu> menuList = menuDao.checkMenu(menu);
        //集合元素 0 表示编辑后菜单不重复 直接修改
        //集合元素 1 根据name path判定是否是原项目 是则可以修改
        //集合元素 2 表示编辑后项目重复 直接flase
        //设置一个扳机 表示可以修改的情况
        boolean flag = (menuList.size() == 0) ||
                ((menuList.size() == 1)
                        && (menuList.get(0).getId().equals(menu.getId())));
        if(flag){
            //根据id查主菜单原path
            Menu parentMenu = menuDao.findById(menu.getId());
            //主菜单原path
            String parentMenuPath = parentMenu.getPath();
            //修改主菜单信息
            menuDao.edit(menu);
            //如果菜单path改变 还需要修改所有子菜单path
            if(!parentMenuPath.equals(menu.getPath())){
                //获取所有关联子菜单
                List<Menu> childrenMenuList = menuDao.findChildrenByMenuId(menu.getId());
                for (Menu childrenMenu : childrenMenuList) {
                    //子菜单原path
                    String childPath = childrenMenu.getPath().split("-")[1];
                    //新path
                    String newChildPath = "/" + menu.getPath() + "-" + childPath;
                    childrenMenu.setPath(newChildPath);
                    //修改子菜单path
                    menuDao.edit(childrenMenu);
                }
            }
        }else{
            throw new RuntimeException(MessageConstant.MENU_EXIST_EDIT);
        }
    }

    /**
     * 删除主菜单
     *
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        //根据主菜单id查关联子菜单
        List<Menu> childrenList = menuDao.findChildrenByMenuId(id);
        if (childrenList.size() > 0) {
            throw new RuntimeException(MessageConstant.EXIST_CHILDREN);
        }
        //清除关联信息
        menuDao.deleteMenuRole(id);
        menuDao.deleteById(id);
    }

    @Override
    public List<Menu> findAllMenusByTree() {
        List<Menu> parentMenus = menuDao.findParentMenus();
        for (Menu parentMenu : parentMenus) {
            parentMenu.getChildren().addAll(menuDao.findChildrenByMenuId(parentMenu.getId()));
        }
        return parentMenus;
    }

    /**
     * 查所有
     * @return
     */
    @Override
    public List<Map> findAll() {
        List<Map> menuList = menuDao.findAll();
        for (Map map : menuList) {
            List<Menu> list = menuDao.findChildrenByMenu(map);
            map.put("children", list);
        }
        return menuList;
    }

    /**
     * 删除子菜单
     * @param id
     */
    @Override
    public void deleteChildById(Integer id) {
        //清除关联信息
        menuDao.deleteMenuRole(id);
        menuDao.deleteChildById(id);
    }

    /**
     * 新增二级菜单
     * @param childMenu
     */
    @Override
    public void addChild(Menu childMenu) {
        //查重复
        List<Menu> childMenuList = menuDao.checkChildMenu(childMenu);
        if(childMenuList.size() > 0){
            throw new RuntimeException(MessageConstant.MENU_EXIST);
        }
        //查主菜单索引
        String parentPath = menuDao.findPathById(childMenu);
        //拼接子菜单索引
        childMenu.setPath("/"+parentPath+"-"+childMenu.getPath());
        menuDao.addChild(childMenu);
        //插入关系表 menu role
        Map menuRoleMap = new HashMap();
        menuRoleMap.put("menu_id", childMenu.getId());
        //管理员id 默认添加
        menuRoleMap.put("role_id", 1);
        menuDao.addMenuRole(menuRoleMap);
    }

    /**
     * 编辑子菜单
     * @param childMenu
     */
    @Override
    public void editChild(Menu childMenu) {
        //校验重复 根据name path查数据库
        List<Menu> menuList = menuDao.checkChildMenu(childMenu);
        //集合元素 0 表示编辑后菜单不重复 直接修改
        //集合元素 1 根据name path判定是否是原项目 是则可以修改
        //集合元素 2 表示编辑后项目重复 直接flase
        //设置一个扳机 表示可以修改的情况
        boolean flag = (menuList.size() == 0) ||
                ((menuList.size() == 1)
                        && (menuList.get(0).getId().equals(childMenu.getId())));
        if(flag){
            //查父标签索引
            Menu parentMenu = menuDao.findById(childMenu.getParentMenuId());
            //拼接子菜单索引
            childMenu.setPath("/"+parentMenu.getPath()+"-"+childMenu.getPath());
            menuDao.edit(childMenu);
        }else{
            throw new RuntimeException(MessageConstant.MENU_EXIST_EDIT);
        }
    }

    @Override
    public List<Integer> findIdsByRoleId(Integer roleId) {
        return menuDao.findByRoleId(roleId);
    }

    @Override
    public List<Map> findMenuNames() {
        try {
            //从redis中获取角色名称及关键字
            List<String> menuNames = jedisPool.getResource().lrange(RedisConstant.MENU_NAMES_KEY, 0, -1);
            //合并集合
            List<Map> result = new ArrayList<>();
            //如果集合为空，则从mysql数据库查询
            if (menuNames == null || menuNames.size() == 0) {
                menuNames = menuDao.findMenuNames();
                //如果集合依然为空则说明数据库内没有任何数据
                if (menuNames != null && menuNames.size() > 0) {
                    //将集合数据封装进map
                    setParams(result, menuNames);
                }
            }else {
                setParams(result, menuNames);
            }
            JedisUtils.updateRedis(jedisPool,RedisConstant.ROLE_NAMES_KEY, menuDao.findMenuNames());
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按名称查询
     * @param queryString
     * @return
     */
    @Override
    public List<Menu> findByName(String queryString) {
        //查所有主菜单
        List<Menu> menuList = menuDao.findMenuAll();
        for (int i = menuList.size() -1; i >= 0; i--) {
            Menu menu = menuList.get(i);
            List<Menu> childMenus = menuDao.findChildByName(queryString, menu.getId());
            if(childMenus.size() > 0){
                menu.setChildren(childMenus);
            }else if(!menu.getName().contains(queryString)){
                //不包含子菜单 同时主菜单名称不含关键字 则移除
                menuList.remove(i);
            }
        }
        return menuList;
    }

    private void setParams(List<Map> result, List<String> names) {
        for (String name : names) {
            if (name != null && !name.equals("")) {
                Map params = new HashMap();
                params.put("value", name);
                result.add(params);
            }
        }
    }
}

