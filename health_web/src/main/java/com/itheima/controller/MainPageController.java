package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MainPageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@RestController
@RequestMapping("/mainpage")
public class MainPageController {

    @Reference
    private MainPageService mainPageService;
    @RequestMapping("/upload")
    public Result uploadImage(@RequestBody MultipartFile file) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            //1.获取原始文件名
            String oldFileName = file.getOriginalFilename();
            //2.文件名称处理 最终得到新的文件名（保证唯一）
            String suffix = oldFileName.substring(oldFileName.indexOf("."));
            String fileName = user.getUsername() + UUID.randomUUID().toString();
            String newFileName = fileName+suffix;
            String path1 = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String projectPath = path1.substring(1, path1.indexOf("health_web")) + "health_web/src/main/webapp/img/userImg/";
            OutputStream out = new FileOutputStream(projectPath + newFileName);
            InputStream inputStream = file.getInputStream();
            byte[] bytes = new byte[64 * 1024];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, length);
            }
            out.flush();
            out.close();
            inputStream.close();
            mainPageService.updateUserImg(user.getUsername(), newFileName);
            return new Result(true, MessageConstant.UPLOAD_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.UPLOAD_FAIL);
        }
    }

    @RequestMapping("/getMainPageImg")
    public Result getMainPageImg() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            String fileName = mainPageService.getPageImageByUsername(user.getUsername());
            String filePathName = "/img/userImg/";
            String path1 = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String projectPath = path1.substring(1, path1.indexOf("health_web")) + "health_web/src/main/webapp/img/userImg/";
            File file = new File(projectPath +fileName);
            if (file.exists()){
                return new Result(true, "获取主页面图片成功", filePathName + fileName);
            }else {
                return new Result(false, "");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "");
        }
    }

}
