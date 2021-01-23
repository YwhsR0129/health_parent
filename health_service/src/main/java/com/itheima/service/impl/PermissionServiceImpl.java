package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.PermissionDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.TreeNode;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import com.itheima.service.PermissionService;
import com.itheima.utils.JedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(interfaceClass = PermissionService.class)
@Transactional
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 分页查询
     */
    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //第一步：设置分页参数
        PageHelper.startPage(currentPage, pageSize);
        //第二步：查询数据库
        Page<Permission> permissions = permissionDao.findPage(queryString);
        return new PageResult(permissions.getTotal(), permissions.getResult());
    }

    /**
     * 新增
     */
    @Override
    public Boolean add(Permission permission) {
        String name = permission.getName();
        Permission permission1 = permissionDao.getByName(name);
        if (permission1 == null) {
            permissionDao.add(permission);
            return true;
        }
        return false;
    }

    /**
     * 编辑
     */
    @Override
    public void edit(Permission permission) {
        permissionDao.edit(permission);
    }

    /**
     * 查询权限
     */
    @Override
    public Permission findById(Integer id) {
        return permissionDao.findById(id);
    }

    /**
     * 删除
     */
    @Override
    public Boolean deleteById(Integer id) {
        List<Role> list = permissionDao.findRoleById(id);
        if (list == null || list.isEmpty()) {
            return true;
        }else  {
            throw new RuntimeException("当前权限与角色关联，删除失败");
        }
    }

    @Override
    public List<Permission> findAll() {
        return permissionDao.findAll();
    }

    @Override
    public List<TreeNode> findByDescendants() {
        String[] permissionCategories = {"检查项", "检查组", "套餐", "预约", "报表", "菜单", "角色", "用户"};
        List<TreeNode> params = new ArrayList<>();

        for (int i = 0; i < permissionCategories.length; i++) {
            params.add(new TreeNode(i + "P", permissionCategories[i], null));
        }

        List<Permission> permissions = permissionDao.findAll();

        for (TreeNode node : params) {
            List<TreeNode> children = new ArrayList<>();
            for (Permission permission : permissions) {
                if (permission.getName().contains(node.getLabel())) {
                    children.add(new TreeNode(permission.getId().toString(), permission.getName(), null));
                }
            }
            node.setChildren(children);
        }

        return params;
    }

    /**
     * 关联角色
     */
    @Override
    public List<Role> findRoleById(Integer id) {
        List<Role> list = permissionDao.findRoleById(id);
        return list;
    }

    /**
     * 获取全部权限的名称和关键字封装到result对象中
     *
     * @return
     */
    @Override
    public List<Map> findPermissionNames() {
        try {
            //从redis中获取角色名称及关键字
            List<String> permissionNames = jedisPool.getResource().lrange(RedisConstant.PERMISSION_NAMES_KEY, 0, -1);
            //合并集合
            List<Map> result = new ArrayList<>();
            //如果集合为空，则从mysql数据库查询
            if (permissionNames == null || permissionNames.size() == 0) {
                permissionNames = permissionDao.findAllPermissionNames();
                //如果集合依然为空则说明数据库内没有任何数据
                if (permissionNames != null && permissionNames.size() > 0) {
                    //将集合数据封装进map
                    setParams(result, permissionNames);
                }
            }else {
                setParams(result, permissionNames);
            }
            JedisUtils.updateRedis(jedisPool,RedisConstant.PERMISSION_NAMES_KEY, permissionDao.findAllPermissionNames());
            List<String> permissionKeywords = jedisPool.getResource().lrange(RedisConstant.PERMISSION_KEYWORD_KEY, 0, -1);
            if (permissionKeywords == null || permissionKeywords.size() == 0) {
                permissionKeywords = permissionDao.findAllPermissionKeywords();

                if (permissionKeywords != null && permissionKeywords.size() > 0) {
                    //将数据库中查询的角色关键字添加至redis缓存
                    //将集合数据封装进map
                    setParams(result, permissionKeywords);
                }
            } else {
                //将集合数据封装进map
                setParams(result, permissionKeywords);
            }
            JedisUtils.updateRedis(jedisPool,RedisConstant.PERMISSION_KEYWORD_KEY, permissionDao.findAllPermissionKeywords());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
