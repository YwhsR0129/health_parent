package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.UserDao;
import com.itheima.service.MainPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service(interfaceClass = MainPageService.class)
@Transactional
public class MainPageServiceImpl implements MainPageService {

    @Autowired
    private UserDao userDao;

    @Override
    public void updateUserImg(String username, String fileName) {
        Integer id = userDao.findUserByUsername(username).getId();
        int count = userDao.selectImgsByUserId(id);
        if(count == 0) {
           userDao.insertUserImg(id, fileName);
        }else {
            String name = userDao.findImgNameById(id);
            File file = new File("/itcast_health_team02/health_web/src/main/webapp/img/userImg/" + name);
            file.delete();
            userDao.updateImg(id, fileName);
        }
    }

    @Override
    public String getPageImageByUsername(String username) {
        Integer id = userDao.findUserByUsername(username).getId();
        return userDao.findImgNameById(id);
    }
}
