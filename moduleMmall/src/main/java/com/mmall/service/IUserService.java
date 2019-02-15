package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkNotExisted(String str, String type);

    ServerResponse<String> getPasswordQuestion(String username);

    ServerResponse<String> checkPasswordAnswer(String username, String question, String answer);

    ServerResponse<String> resetPasswordByPasswordToken(String username, String passwordNew, String passwordToken);

    ServerResponse<String> resetPasswordByPasswordOld(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInfo(User user);

    ServerResponse<User> getInfo(Integer userId);

    ServerResponse checkAdminRole(User user);
}
