package com.itheima.service;

public interface MainPageService {
    void updateUserImg(String username, String newFileName);

    String getPageImageByUsername(String username);
}
