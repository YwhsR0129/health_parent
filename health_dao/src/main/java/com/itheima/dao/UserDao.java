package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Menu;
import com.itheima.pojo.User;

import java.util.List;
import java.util.Map;

/**
 * 用户持久层接口
 */
public interface UserDao {
    /**
     * 根据用户查询用户对象
     * @param username
     * @return
     */
    User findUserByUsername(String username);
    //根据用户名获取用户显示菜单
    List<Menu> findMenuByusername(String username);

    List<Menu> findSubMenuByusername(String username);

    /**
     *:修改密码
     *@Param: [map]
     */
    void updatePassword(Map map);

    /**
     *:通过用户名获取用户在数据库中的密码
     *@Param: [username]
     */
    String findPasswordByUsername(String username);


    Page<User> selectByCondition(String queryString);

    void add(User user);

    void setUserAndRole(Map<String, Object> map);

    void deleteById(Integer id);

    int findRoleByUserId(Integer id);

    void deleteRoleById(Integer id);

    List<Integer> findRoleIdsByUserId(Integer userId);

    void edit(User user);

    User findById(Integer userId);

    List<String> findAllUsernames();

    List<String> findAllUserPhones();

    int selectImgsByUserId(Integer id);

    void insertUserImg(Integer id, String fileName);

    void updateImg(Integer id, String fileName);

    String findImgNameById(Integer id);
}
