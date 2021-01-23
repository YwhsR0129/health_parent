package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.PermissionService;
import com.itheima.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Reference
    private RoleService roleService;

    @Reference
    private PermissionService permissionService;
    /**
     * 获取角色列表页面
     * @return
     * */
    @RequestMapping("/findByPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult page = roleService.findPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize(), queryPageBean.getQueryString());
        return page;
    }

    @RequestMapping("/findPermissionsByRoleId")
    public Result findPermissionsByRoleId(@RequestParam Integer id) {
        try {
            Set<Permission> permissionList = roleService.findPermissionByRoleId(id);
            return new Result(true, MessageConstant.QUERY_PERMISSION_LIST_SUCCESS, permissionList);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_PERMISSION_LIST_FAIL);
        }
    }

    @RequestMapping("/add")
    public Result addRole(@RequestBody Role role, String[] permissionIds, Integer[] menuIds) {
        try {
            roleService.add(role, permissionIds, menuIds);
            return new Result(true, MessageConstant.ADD_ROLE_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_ROLE_FAIL);
        }
    }

    @RequestMapping("/deleteById")
    public Result deleteRoleById(Integer id) {
        try {
            roleService.delete(id);
            return new Result(true, MessageConstant.DELETE_ROLE_SUCCESS);
        }catch (RuntimeException re) {
            return new Result(false, re.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.DELETE_ROLE_FAIL);
        }
    }

    @RequestMapping("/findById")
    public Result findById(@RequestParam Integer id) {
        try {
            Role role = roleService.findById(id);
            return new Result(true, MessageConstant.QUERY_ROLE_SUCCESS, role);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ROLE_FAIL);
        }
    }

    @RequestMapping("/findPermissionIdsByRoleId")
    public Result findPermissionIdsByRoleId(@RequestParam Integer roleId) {
        try {
            List<Integer> permissionIds = roleService.findPermissionIdsByRoleId(roleId);
            return new Result(true, MessageConstant.QUERY_PERMISSION_LIST_SUCCESS, permissionIds);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_PERMISSION_LIST_FAIL);
        }
    }

    @RequestMapping("/edit")
    public Result edit(@RequestParam String[] permissionIds, @RequestBody Role role, @RequestParam Integer[] menuIds) {
        try {
            roleService.editRole(role, permissionIds, menuIds);
            return new Result(true, MessageConstant.EDIT_ROLE_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_ROLE_FAIL);
        }
    }
    /**
     * 获取全部角色的名称和关键字封装到result对象中
     * @return
     * */
    @RequestMapping(value = "/findRoleNames", method = RequestMethod.GET)
    public Result findAllRoleNames() {
        try {
            List<Map> roleNames = roleService.findAllRoleNames();
            if (roleNames != null) {
                return new Result(true, MessageConstant.QUERY_ROLE_LIST_SUCCESS, roleNames);
            }else {
                return new Result(false, MessageConstant.QUERY_ROLE_LIST_FAIL);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ROLE_LIST_FAIL);
        }
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.POST)
    public Result findAll() {
         try {
            List<Role> list = roleService.findAll();
            return new Result(true, MessageConstant.QUERY_ROLE_LIST_SUCCESS, list);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.QUERY_ROLE_LIST_FAIL);
        }
    }

}
