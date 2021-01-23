package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;
import java.util.Map;

/**
 * 会员服务接口
 */
public interface MemberService {
    /**
     * 根据手机号码查询会员信息
     * @param telephone
     * @return
     */
    Member findByTelephone(String telephone);

    /**
     * 自动注册方法
     * @param member
     */
    void add(Member member);
    /**
     * 会员数量折线图
     */
    Map<String,Object> getMemberReport(String startDate, Integer diffmonths);

    /**
     *:会员饼图-获取会员性别
     *@Param: []
     */
    Map<String, Object> getMemberReportSex();

    /**
     *:会员饼图-获取会员年龄阶段，和该年龄阶段的会员数量
     *@Param: []
     */
    Map<String, Object> getMemberReportAge();
}
