package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.UserDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Menu;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.JedisUtils;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口实现类
 */
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    public static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserDao userDao;

    @Autowired
    private JedisPool jedisPool;
    /**
     * 根据用户查询用户对象
     * @param username
     * @return
     */
    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    /**
     * 根据用户名获取用户显示菜单
     *
     * @param username
     * @return
     */
    @Override
    public List<Map> findMenuByusername(String username) {
        //查询用户主菜单
        List<Menu> listMenu = userDao.findMenuByusername(username);
        //存储map类型键值用于前端返回
        List<Map> rslist = new ArrayList<>();

        //查询用户子菜单
        List<Menu> listSubMenu = userDao.findSubMenuByusername(username);


        if (listMenu!=null&&listMenu.size()>0){
            //遍历主菜单
            for (Menu menu : listMenu) {

                //主菜单的path
                String path = menu.getPath();
                String name = menu.getName();
                String icon = menu.getIcon();
                String linkUrl = menu.getLinkUrl();

                Map menuMap = new HashMap<>();
                menuMap.put("path", path);
                menuMap.put("name", name);
                menuMap.put("icon", icon);
                //子菜单集合
                //   List<Menu> children = menu.getChildren();
                List<Menu> children = menu.getChildren();

                if (listSubMenu!=null&&listSubMenu.size()>0){
                    for (Menu child : listSubMenu) {
                        String[] split = child.getPath().split("-");
                        //获取子菜单中类似于/2这样的
                        String subPath = split[0];
                        //比较
                        String prePath = "/"+path;
                        if (prePath.equals(subPath)){
                            children.add(child);
                        }
                    }
                }


                menuMap.put("children", children);

                rslist.add(menuMap);
            }
        }

        return rslist;
    }

    /**
     * :修改密码
     *
     * @Param: [password]
     */
    @Override
    public void updatePassword(String username, Map map) {
        if ((String)map.get("oldPassword")!=null&&((String)map.get("oldPassword")).length()>0
                &&(String)map.get("newPassword")!=null&&((String)map.get("newPassword")).length()>0
                &&(String)map.get("affirmPassword")!=null&&((String)map.get("affirmPassword")).length()>0) {
        //数据库中的密码
        String password = userDao.findPasswordByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean b = encoder.matches((String) map.get("oldPassword"), password);
            if (b) {
                //原始密码和数据库中的密码相等
                if (((String) map.get("newPassword")).equals((String) map.get("affirmPassword"))) {
                    //判断新密码和原始密码是否相同
                    if (((String) map.get("oldPassword")).equals((String) map.get("newPassword"))) {
                        //新密码和原始密码相同
                        throw new RuntimeException(MessageConstant.NEWPASSWORD_OLDPASSWORD_FAIL);
                    }
                    //新密码和重新输入的新密码相同
                    // 对密码进行加密，同时通过用户名修改密码
                    map.put("newPassword", passwordEncoder.encode((String) map.get("newPassword")));
                    map.remove("oldPassword");
                    map.remove("affirmPassword");
                    map.put("username", username);
                    userDao.updatePassword(map);
                } else {
                    //两次输入的新密码不相同，请重新输入
                    throw new RuntimeException(MessageConstant.AFFIRM_PASSWORD_FAIL);
                }
            } else {
                //密码输入错误
                throw new RuntimeException(MessageConstant.PASSWORD_FAIL);
            }
        } else {
            throw new RuntimeException(MessageConstant.PASSWORD_NULL_FAIL);
        }
    }


    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        //第二步：查询数据库（代码一定要紧跟设置分页代码  没有手动分页 select * from table where name = 'xx'  ）
        Page<User> userPage = userDao.selectByCondition(queryString);
        return new PageResult(userPage.getTotal(),userPage.getResult());
    }

    @Override
    public void add(User user, Integer[] roleIds) {
        //第一步：保存用户表
        //加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.add(user);
        //第二步：获取用户id
        Integer userId = user.getId();
        //第三步：往用户角色中间表 遍历插入关系数据
        setUserAndRole(userId,roleIds);
    }

    @Override
    public void deleteById(Integer id) {
        int count1 = userDao.findRoleByUserId(id);
        if(count1>0){
            userDao.deleteRoleById(id);
        }
        userDao.deleteById(id);
    }

    @Override
    public List<Integer> findRoleIdsByUserId(Integer userId) {
        return userDao.findRoleIdsByUserId(userId);
    }

    @Override
    public void edit(User user, Integer[] roleIds) {
        //1.先根据用户id从用户角色中间表 删除关系数据
        userDao.deleteRoleById(user.getId());
        //2.根据页面传入的检查项ids 和 检查组重新建立关系
        setUserAndRole(user.getId(),roleIds);
        //加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //3根据检查组id 更新检查组数据
        userDao.edit(user);
    }

    @Override
    public User findById(Integer userId) {
        User checkUser = userDao.findById(userId);
        //去密码
        checkUser.setPassword("");
        return checkUser;
    }

    @Override
    public List<Map> findAllUserNames() {
        try {
            //从redis中获取角色名称及关键字
            List<String> usernames = jedisPool.getResource().lrange(RedisConstant.USER_NAMES_KEY, 0, -1);
            //合并集合
            List<Map> result = new ArrayList<>();
            //如果集合为空，则从mysql数据库查询
            if (usernames == null || usernames.size() == 0) {
                usernames = userDao.findAllUsernames();
                //如果集合依然为空则说明数据库内没有任何数据
                if (usernames != null && usernames.size() > 0) {
                    //将集合数据封装进map
                    setParams(result, usernames);
                }
            }else {
                setParams(result, usernames);
            }
                JedisUtils.updateRedis(jedisPool,RedisConstant.USER_NAMES_KEY, userDao.findAllUsernames());
            List<String> phones = jedisPool.getResource().lrange(RedisConstant.USER_PHONES_KEY, 0, -1);
            if (phones == null || phones.size() == 0) {
                phones = userDao.findAllUserPhones();

                if (phones != null && phones.size() > 0) {
                    //将集合数据封装进map
                    setParams(result, phones);
                }
            }else {
                //将集合数据封装进map
                setParams(result, phones);
            }
                JedisUtils.updateRedis(jedisPool,RedisConstant.USER_PHONES_KEY, userDao.findAllUserPhones());
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setUserAndRole(Integer userId, Integer[] roleIds) {
        if(roleIds != null && roleIds.length>0){
            for (Integer roleId : roleIds) {
                Map<String,Object> map = new HashMap<>();
                map.put("userId",userId);
                map.put("roleId",roleId);
                userDao.setUserAndRole(map);
            }
        }
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