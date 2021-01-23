package com.itheima.constant;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图标
 *
 * @Author Sven Yang
 * @Date 2020/12/26 11:15
 */

public class IconConstant {
    public static final List<Map> ICON_LIST = new ArrayList<>();

    static{
        Map map = new HashMap();
        map.put("label", "铃铛");
        map.put("value", "el-icon-bell");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "时间");
        map.put("value", "el-icon-time");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "信息");
        map.put("value", "el-icon-message");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "耳机");
        map.put("value", "el-icon-service");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "定位");
        map.put("value", "el-icon-location");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "修改");
        map.put("value", "el-icon-edit");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "上传");
        map.put("value", "el-icon-upload");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "分享");
        map.put("value", "el-icon-share");
        ICON_LIST.add(map);
        map = new HashMap();
        map.put("label", "删除");
        map.put("value", "el-icon-delete");
        ICON_LIST.add(map);
    }
}

