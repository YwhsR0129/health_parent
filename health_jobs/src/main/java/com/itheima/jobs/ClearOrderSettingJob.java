package com.itheima.jobs;


import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;

/**
 * 定时清理预约设置历史数据
 */
@Component
public class ClearOrderSettingJob {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private OrderSettingDao orderSettingDao;

    public void clearOrderSetting() throws Exception {

        //新建日期转换成string类型
        String date = DateUtils.parseDate2String(new Date());

        //查询需要删除的预约历史数据
        List<OrderSetting> findAll = orderSettingDao.findAllOrderSetting(date);

        //清理历史数据
        System.out.println("********清理准备中********");
        orderSettingDao.clearOrderSetting(date);
        System.out.println("********清理完成********");


    }
}
