package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.IconConstant;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Menu;
import com.itheima.service.CheckGroupService;
import com.itheima.service.MenuService;
import com.itheima.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

/**
 * 检查组控制层 contrl+alt+o 去除多余的包
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    //引用服务
    @Reference
    private MenuService menuService;

    /**
     * 缓存池对象 jedisPool
     */
    @Autowired
    JedisPool jedisPool;

    @Reference
    private UserService userService;


    /**
     * 新增一级菜单
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody Menu menu) {
        try {
            menuService.add(menu);
            //调用本类方法 清空缓存
            delRedisValues();
            return new Result(true, MessageConstant.ADD_MENU_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_MENU_FAIL);
        }
    }

    /**
     * 新增二级菜单
     */
    @RequestMapping(value = "/addChild", method = RequestMethod.POST)
    public Result addChild(@RequestBody Menu menu) {
        try {
            menuService.addChild(menu);
            //调用本类方法 清空缓存
            delRedisValues();
            return new Result(true, MessageConstant.ADD_MENU_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_MENU_FAIL);
        }
    }

    /**
     * 根据id查主菜单信息
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public Result findById(Integer id) {
        try {
            Menu menu = menuService.findById(id);
            return new Result(true, null, menu);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_MENU_FAIL);
        }
    }

    /**
     * 根据id查子菜单信息
     */
    @RequestMapping(value = "/findChildById", method = RequestMethod.GET)
    public Result findChildById(Integer id) {
        try {
            Menu menu = menuService.findById(id);
            //编辑子菜单path
            menu.setPath(menu.getPath().split("-")[1]);
            return new Result(true, null, menu);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_MENU_FAIL);
        }
    }


    /**
     * 菜单查询
     */
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public Result findAll() {
        try {
            //获取当前用户
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = user.getUsername();
            List<Map> listMenu;
            //拼接字符串key
            String menusKey = "menus_" + username;
            //从redis获取菜单数据
            String menusValue = jedisPool.getResource().get(menusKey);
            if (menusValue != null && menusValue.length() > 0) {
                listMenu = JSON.parseObject(menusValue, List.class);
            } else {
                listMenu = menuService.findAll();
                //存到缓存
                String menusToJSONString = JSON.toJSONString(listMenu);
                jedisPool.getResource().set(menusKey, menusToJSONString);
            }
            return new Result(true, MessageConstant.GET_MENU_LIST_SUCCESS, listMenu);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MENU_LIST_FAIL);
        }
    }


    /**
     * 编辑菜单
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result edit(@RequestBody Menu menu) {
        try {
            menuService.edit(menu);
            //调用本类方法 清空缓存
            delRedisValues();
            return new Result(true, MessageConstant.EDIT_MENU_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_MENU_FAIL);
        }
    }

    /**
     * 编辑子菜单
     */
    @RequestMapping(value = "/editChild", method = RequestMethod.POST)
    public Result editChild(@RequestBody Menu menu) {
        try {
            menuService.editChild(menu);
            //调用本类方法 清空缓存
            delRedisValues();
            return new Result(true, MessageConstant.EDIT_MENU_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_MENU_FAIL);
        }
    }

    /**
     * 根据菜单id删除菜单
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET)
    public Result deleteById(Integer id) {
        try {
            menuService.deleteById(id);
            delRedisValues();
            return new Result(true, MessageConstant.DELETE_MENU_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_MENU_FAIL);
        }
    }

    /**
     * 根据子菜单id删除菜单
     */
    @RequestMapping(value = "/deleteChildById", method = RequestMethod.GET)
    public Result deleteChildById(Integer id) {
        try {
            menuService.deleteChildById(id);
            delRedisValues();
            return new Result(true, MessageConstant.DELETE_MENU_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_MENU_FAIL);
        }
    }

    /**
     * 私有方法 供本类调用
     * 清空当前用户菜单缓存数据
     */
    private void delRedisValues() {
        //清除缓存数据
        //获取当前用户
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        //拼接字符串key
        String menusKey = "menus_" + username;
        jedisPool.getResource().del(menusKey);
    }

    @RequestMapping("/findAllMenusByTree")
    public Result findAllMenusByTree() {
        try {
            List<Menu> menus = menuService.findAllMenusByTree();
            return new Result(true, MessageConstant.GET_MENU_LIST_SUCCESS, menus);
        } catch (Exception e) {
            return new Result(false, MessageConstant.GET_MENU_LIST_FAIL);
        }
    }

    @RequestMapping("/findRoleMenus")
    public Result findRoleMenus(Integer roleId) {
        try {
            List<Integer> menuIds = menuService.findIdsByRoleId(roleId);
            return new Result(true, MessageConstant.GET_MENU_LIST_SUCCESS, menuIds);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MENU_LIST_FAIL);
        }
    }

    @RequestMapping("/findMenuNames")
    public Result findMenuNames() {
        try {
            List<Map> nameList = menuService.findMenuNames();
            if (nameList != null && nameList.size() > 0) {
                return new Result(true, MessageConstant.GET_MENU_LIST_SUCCESS, nameList);
            } else {
                return new Result(false, MessageConstant.GET_MENU_LIST_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MENU_LIST_FAIL);
        }
    }

    /**
     * 关键字查询 getIconList
     */
    @RequestMapping(value = "/findByName", method = RequestMethod.POST)
    public Result findByName(@RequestBody Map map) {
        try {
            String queryString = map.get("queryString").toString();
            System.out.println(queryString);
            List<Menu> menuList = menuService.findByName(queryString);
            return new Result(true, MessageConstant.GET_MENU_LIST_SUCCESS, menuList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MENU_LIST_FAIL);
        }
    }

    /**
     * 获得图标集合
     */
    @RequestMapping(value = "/getIconList", method = RequestMethod.GET)
    public Result getIconList() {
        return new Result(true, MessageConstant.GET_ICON_SUCCESS, IconConstant.ICON_LIST);
    }
}
