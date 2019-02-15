package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        //service--mybatis--dao
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess())
            session.setAttribute(Const.CURRENT_USER, response.getData());
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return userService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return userService.checkNotExisted(str, type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null)
            return ServerResponse.createBySuccessData(user);
        return ServerResponse.createByErrorMessage("用户未登录");
    }

    @RequestMapping(value = "get_password_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getPasswordQuestion(String username) {
        return userService.getPasswordQuestion(username);
    }

    @RequestMapping(value = "check_password_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkPasswordAnswer(String username, String question, String answer) {
        return userService.checkPasswordAnswer(username, question, answer);
    }

    @RequestMapping(value = "reset_password_by_password_token.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPasswordByToken(String username, String passwordNew, String passwordToken) {
        return userService.resetPasswordByPasswordToken(username, passwordNew, passwordToken);
    }

    @RequestMapping(value = "reset_password_by_password_old.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPasswordByPasswordOld(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorMessage("用户未登录");
        ServerResponse<String> resetResult= userService.resetPasswordByPasswordOld(passwordOld, passwordNew, user);
        if(resetResult.isSuccess())
            session.removeAttribute(Const.CURRENT_USER);
        return resetResult;
    }

    @RequestMapping(value = "update_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null)
            return ServerResponse.createByErrorMessage("用户未登录");
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> updateResult = userService.updateInfo(user);
        if (updateResult.isSuccess())
            session.setAttribute(Const.CURRENT_USER, updateResult.getData());
        return updateResult;
    }

    @RequestMapping(value = "get_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfo(HttpSession session) {
        User currentUser= (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要登录");
        return userService.getInfo(currentUser.getId());
    }


}
