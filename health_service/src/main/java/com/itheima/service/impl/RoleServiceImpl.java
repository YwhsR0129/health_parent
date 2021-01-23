package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.MenuDao;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.RoleService;
import com.itheima.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(interfaceClass = RoleService.class)
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private MenuDao menuDao;
    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage, pageSize);
        Page<Role> rolePage = roleDao.findAllRolesByCondition(queryString);
        return new PageResult(rolePage.getTotal(), rolePage.getResult());
    }

    @Override
    public Set<Permission> findPermissionByRoleId(Integer id) {
        return permissionDao.findPermissionsByRoleId(id);
    }

    @Override
    public void add(Role role, String[] permissionIds, Integer[] menuIds) {
        roleDao.add(role);
        int id = role.getId();
        setPermissionAndRole(id, permissionIds);
        addRoleMenuRelation(id, menuIds);
    }

    @Override
    public void delete(Integer id) {
        int count1 = roleDao.selectRelatedUsers(id);
        int count2 = roleDao.selectRelatedMenus(id);
        if (count1 > 0) {
            throw new RuntimeException("该角色和已有用户存在关系，无法删除");
        }else if (count2 > 0) {
            throw new RuntimeException("该角色和已有菜单存在关系，无法删除");
        }else {
            roleDao.deleteRelation(id);
            roleDao.delete(id);
        }
    }

    @Override
    public Role findById(Integer id) {
        return roleDao.findById(id);
    }

    @Override
    public void editRole(Role role, String[] permissionIds, Integer[] menuIds) {
        int id = role.getId();
        removeAllRelations(id);
        setPermissionAndRole(id, permissionIds);
        addRoleMenuRelation(id, menuIds);
        roleDao.editRole(role);
    }
    /**
     * 获取全部角色的名称以及关键字
     * 并存入redis中
     * @return
     * */
    @Override
    public List<Map> findAllRoleNames() {
        try {
            //从redis中获取角色名称及关键字
            List<String> roleNames = jedisPool.getResource().lrange(RedisConstant.ROLE_NAMES_KEY, 0, -1);
            //合并集合
            List<Map> result = new ArrayList<>();
            //如果集合为空，则从mysql数据库查询
            if (roleNames == null || roleNames.size() == 0) {
                roleNames = roleDao.findAllRoleNames();
                //如果集合依然为空则说明数据库内没有任何数据
                if (roleNames != null && roleNames.size() > 0) {
                    //将集合数据封装进map
                    setParams(result, roleNames);
                }
            }else {
                setParams(result, roleNames);
            }
            JedisUtils.updateRedis(jedisPool,RedisConstant.ROLE_NAMES_KEY, roleDao.findAllRoleNames());
            List<String> roleKeywords = jedisPool.getResource().lrange(RedisConstant.ROLE_KEYWORD_KEY, 0, -1);
            if (roleKeywords == null || roleKeywords.size() == 0) {
                roleKeywords = roleDao.findAllRoleKeywords();

                if (roleKeywords != null && roleKeywords.size() > 0) {
                    //将集合数据封装进map
                    setParams(result, roleKeywords);
                }
            }else {
                //将集合数据封装进map
                setParams(result, roleKeywords);
            }
                JedisUtils.updateRedis(jedisPool,RedisConstant.ROLE_KEYWORD_KEY, roleDao.findAllRoleKeywords());
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void removeAllRelations(int id) {
        roleDao.deleteRelation(id);
        menuDao.deleteRelationByRoleId(id);
    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public List<Integer> findPermissionIdsByRoleId(Integer roleId) {
        return roleDao.findPermissionIdsByRoleId(roleId);
    }

    private void setPermissionAndRole(int id, String[] permissionIds) {
        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < permissionIds.length; i++) {
            String permissionId = permissionIds[i];
            if (permissionId != null && permissionId != "") {
                Pattern pattern = Pattern.compile("^[0-9]*$");
                Matcher matcher = pattern.matcher(permissionId);
                if (matcher.matches()) {
                    idList.add(Integer.valueOf(permissionId));
                }
            }
        }
        if (idList != null && idList.size() > 0) {
            for (Integer pid : idList) {
                Map<String, Object> map = new HashMap<>();
                map.put("roleId", id);
                map.put("permissionId", pid);
                roleDao.addRelation(map);
            }
        }
    }

    private void addRoleMenuRelation(int roleId, Integer[] menuIds) {
        for (int i = 0; i < menuIds.length; i++) {
            menuDao.addRoleRelation(roleId, menuIds[i]);
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
