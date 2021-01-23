package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员服务实现
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;
    /**
     * 根据手机号码查询会员信息
     * @param telephone
     * @return
     */
    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }
    /**
     * 自动注册方法
     * @param member
     */
    @Override
    public void add(Member member) {
        //密码
        String password = member.getPassword();
        if(!StringUtils.isEmpty(password)){
            //加密 MD5
            String md5Pwd  = MD5Utils.md5(password);
            member.setPassword(md5Pwd);
        }
        memberDao.add(member);
    }
    /**
     * @param startDate  开始月份
     * @param diffmonths 选择的月份差数
     *                   会员数量折线图
     */
    @Override
    public Map<String, Object> getMemberReport(String startDate, Integer diffmonths) {
        //定义返回结果Map
        Map<String, Object> rsMap = new HashMap<>();

        //1.key:months—[2020-01,2020-02…最近一年]  --List<String>
        Calendar calendar = Calendar.getInstance();
        //设置开始日期
        calendar.set(Calendar.YEAR, Integer.parseInt(startDate.split("-")[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(startDate.split("-")[1])-1);
        //遍历获取每一个月年月 ，并将年月放入List<String>集合中
        List<String> months = new ArrayList<>();
        for (int i = 0; i < diffmonths; i++) {
            months.add(new SimpleDateFormat("yyyy-MM").format(calendar.getTime()));
            calendar.add(Calendar.MONTH, 1);
        }

        rsMap.put("months", months);

        //key:memberCount—[50,100,…最近一年]       List<Integer>
        //select count(*) from t_member where regTime <= ‘2020-01-31’
        List<Integer> memberCount = new ArrayList<>();
        if (months != null && months.size() > 0) {
            for (String month : months) {
                //month 2020-07 ==> 2020-07-31
                String newMonth = month + "-31";
                Integer mc = memberDao.findMemberCountBeforeDate(newMonth);
                memberCount.add(mc);
            }
        }
        rsMap.put("memberCount", memberCount);
        return rsMap;
    }

    /**
     *：会员饼图-获取会员性别
     *@Param: []
     */
    @Override
    public Map<String, Object> getMemberReportSex() {
        Map<String, Object> rsMap = new HashMap<>();

        //获取性别名称，和相应的数量
        List<Map>mapList=memberDao.getMemberReportSex();
        rsMap.put("setSexCount", mapList);
        //获取性别名称
        List<String> setSexs = new ArrayList<>();
        if (mapList != null && mapList.size() > 0) {
            for (Map map : mapList) {
                String sex = (String)map.get("name");
                setSexs.add(sex);
            }
            rsMap.put("setSexs", setSexs);
        }
        return rsMap;
    }

    /**
     *:会员饼图-获取会员年龄段
     *@Param: []
     */
    @Override
    public Map<String, Object> getMemberReportAge() {
        Map<String, Object> rsMap = new HashMap<>();

        //获取会员年龄段
        List<String> setAges = new ArrayList<>();
        //0-18、19-30、31-45、45以上
        setAges.add("0-18");
        setAges.add("19-30");
        setAges.add("31-45");
        setAges.add("45以上");
        rsMap.put("setAges", setAges);

        //获取会员年龄段相应的人数
        List<Integer> ageCounts = new ArrayList<>();
        ageCounts.add(memberDao.getMemberReportAgeA());
        ageCounts.add(memberDao.getMemberReportAgeB());
        ageCounts.add(memberDao.getMemberReportAgeC());
        ageCounts.add(memberDao.getMemberReportAgePlus());

        //遍历，将年龄阶段和该阶段的人数整合到setAgeCount
        List<Map> setAgeCount=new ArrayList<>();
        /*for (int i =0; i <4; i++) {
            Map<String,Object> map = new HashMap();
            map.put(setAges.get(i),ageCounts.get(i));
            setAgeCount.add(map);
        }*/
        for (int i = 0; i <4; i++) {
            Map<String,Object> map = new HashMap();
            map.put("name", setAges.get(i));
            map.put("value", ageCounts.get(i));
            setAgeCount.add(map);
        }
        rsMap.put("setAgeCount", setAgeCount);

        return rsMap;
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12);
        //遍历获取每一个月年月 ，并将年月放入List<String>集合中
        List<String>  months = new ArrayList<>();
        for (int i = 1;i<=12;i++){
            months.add(new SimpleDateFormat("yyyy-MM").format(calendar.getTime()));
            calendar.add(Calendar.MONTH, 1);
        }
        System.out.println("************************");
    }
}
