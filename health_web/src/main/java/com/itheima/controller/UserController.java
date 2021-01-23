package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.service.UserService;
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
 * 用户管理控制层
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 缓存池对象 jedisPool
     */
    @Autowired
    JedisPool jedisPool;

    @Reference
    private UserService userService;

    /**
     * 获取用户名 从SecurityContextHolder获取用户数据
     *
     * @return
     */
    @RequestMapping(value = "/getUserName", method = RequestMethod.GET)
    public Result getUserName() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS, user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }
    }

    /**
     * 根据用户名获取用户显示菜单
     *
     * @return
     */
    @RequestMapping(value = "/findMenuByusername", method = RequestMethod.GET)
    public Result findMenuByusername(String username) {
        try {
            List<Map> listMenu;
            //拼接字符串key
            String menusKey = "menus_" + username;
            //从redis获取菜单数据
            String menusValue = jedisPool.getResource().get(menusKey);
            if (menusValue != null && menusValue.length() > 0) {
                listMenu = JSON.parseObject(menusValue, List.class);
            } else {
                listMenu = userService.findMenuByusername(username);
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
     *:修改密码
     *@Param: [password]
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public Result updatePassword(@RequestBody Map map,String username) {
        try {
            userService.updatePassword(username,map);
            return new Result(true, MessageConstant.UPDATE_PASSWORD_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.UPDATE_PASSWORD_FAIL);
        }
    }

    /**
     * 用户分页查询
     */
    @RequestMapping(value = "/findPage", method = RequestMethod.POST)
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = userService.findPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize(), queryPageBean.getQueryString());
        return pageResult;
    }

    /**
     * 新增用户
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody com.itheima.pojo.User user, Integer[] roleIds) {
        try {
            userService.add(user,roleIds);
            return new Result(true, MessageConstant.ADD_USER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_USER_FAIL);
        }
    }
    /**
     * 根据用户id删除用户
     */
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET)
    public Result deleteById(Integer id) {
        try {
            userService.deleteById(id);
            return new Result(true, MessageConstant.DELETE_USER_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_USER_FAIL);
        }
    }

    /**
     * 根据用户id 查询角色id
     */
    @RequestMapping(value = "/findRoleIdsByUserId", method = RequestMethod.GET)
    public List<Integer> findRoleIdsByUserId(Integer userId) {
        return userService.findRoleIdsByUserId(userId);
    }

    /**
     * 根据用户id查询用户
     */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public Result findById(Integer userId) {
        try {
            com.itheima.pojo.User user = userService.findById(userId);
            return new Result(true, MessageConstant.GET_MENU_SUCCESS,user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MENU_FAIL);
        }
    }
    /**
     * 编辑用户
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result edit(@RequestBody com.itheima.pojo.User user, Integer[] roleIds) {
        try {
            userService.edit(user,roleIds);
            return new Result(true, MessageConstant.EDIT_USER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_USER_FAIL);
        }
    }

    @RequestMapping("/findUsernamesAndPhone")
    public Result findUsernamesAndPhone() {
        try {
            List<Map> usernames = userService.findAllUserNames();
            if (usernames != null) {
                return new Result(true, MessageConstant.QUERY_USER_NAMES_SUCCESS, usernames);
            }else {
                return new Result(false, MessageConstant.QUERY_USER_NAMES_FAIL);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_USER_NAMES_FAIL);
        }
    }
}
