package com.mmall.controller.backend;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Controller
@RequestMapping("/manage/user")
public class UserManagerController {
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> result = userService.login(username, password);
        if (result.isSuccess()) {
            User LoginUser = result.getData();
            if (LoginUser.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, LoginUser);
            } else
                return ServerResponse.createByErrorMessage("权限不足");
        }
        return result;
    }
}
