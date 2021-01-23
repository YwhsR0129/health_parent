package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.entity.TreeNode;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.PermissionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Reference
    private PermissionService permissionService;

    /**
     * 权限分页查询
     */
    @RequestMapping(value = "/findPage", method = RequestMethod.POST)
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = permissionService.findPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize(),queryPageBean.getQueryString());
        return pageResult;
    }

    /**
     * 添加
     * */
    @RequestMapping("/add")
    public Result add(@RequestBody Permission permission){
        try {
           Boolean ps =  permissionService.add(permission);
           if (ps){
               return new Result(true, MessageConstant.ADD_POWER_SUCCESS);
           }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }
        return new Result(false,MessageConstant.ADD_POWER_FAIL);
    }

    /**
     * 编辑
     * */
    @RequestMapping("/edit")
    public Result edit(@RequestBody Permission permission){
        try {
            permissionService.edit(permission);
            return new Result(true, MessageConstant.EDIT_GROUP_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_GROUP_FAIL);
        }
    }

    /**
     * 查询权限
     * */
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public Result findById(Integer id) {
        try {
            Permission permission = permissionService.findById(id);
            return new Result(true, MessageConstant.QUERY_PERMISSION_SUCCESS,permission);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_PERMISSION_ROLE_FAIL);
        }
    }

    /**
     * 删除
     * */
    @RequestMapping(value = "/deleteById", method = RequestMethod.GET)
    public Result deleteById(Integer id) {
        try {
            Boolean sb = permissionService.deleteById(id);
            if (sb) {
                return new Result(true, MessageConstant.DELETE_GROUP_SUCCESS);
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.DELE_POWER_FAIL);
    }

    /**
     * 关联角色
     * */

    @RequestMapping(value = "/findRoleById", method = RequestMethod.GET)
    public Result findRoleById(Integer id) {
        try {
            List<Role> list = permissionService.findRoleById(id);
            return new Result(true, MessageConstant.QUERY_PERMISSION_ROLE_SUCCESS,list);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_PERMISSION_ROLE_FAIL);
        }
    }

    @RequestMapping("/findAll")
    public Result findAll() {
        try {
            List<TreeNode> list = permissionService.findByDescendants();
            System.out.println(list);
            return new Result(true, MessageConstant.QUERY_PERMISSION_SUCCESS, list);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.QUERY_PERMISSION_FAIL);
        }
    }

    /**
     * 获取全部权限的名称和关键字封装到result对象中
     * @return
     * */
    @RequestMapping(value = "/findPermissionNames", method = RequestMethod.GET)
    public Result findPermissionNames() {
        try {
            List<Map> permissionNames = permissionService.findPermissionNames();
            if (permissionNames != null) {
                return new Result(true, MessageConstant.QUERY_PERMISSION_LIST_SUCCESS, permissionNames);
            }else {
                return new Result(false, MessageConstant.QUERY_PERMISSION_LIST_FAIL);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_PERMISSION_LIST_FAIL);
        }
    }
}
