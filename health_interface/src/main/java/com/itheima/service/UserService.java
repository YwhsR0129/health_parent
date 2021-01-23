package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.pojo.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 根据用户查询用户对象
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    //根据用户名获取用户显示菜单
    List<Map> findMenuByusername(String username);

    /**
     *:修改密码
     *@Param: [password]
     */
    void updatePassword(String username,Map map);

    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

    void add(User user, Integer[] roleIds);

    void deleteById(Integer id);

    List<Integer> findRoleIdsByUserId(Integer userId);

    void edit(User user, Integer[] roleIds);

    User findById(Integer userId);

    List<Map> findAllUserNames();
}

