package com.mmall.service.impI;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userService")
public class UserServiceimpI implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("用户名不存在");
        //密码登录md5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null)
            return ServerResponse.createByErrorMessage("密码错误");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> result = checkNotExisted(user.getUsername(), Const.USERNAME);
        if (!result.isSuccess())
            return result;
        result = checkNotExisted(user.getEmail(), Const.Email);
        if (!result.isSuccess())
            return result;

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0)
            return ServerResponse.createByErrorMessage("注册失败");
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkNotExisted(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                if (userMapper.checkUsername(str) > 0)
                    return ServerResponse.createByErrorMessage("用户已存在");
            }
            if (Const.Email.equals(type)) {
                if (userMapper.checkEmail(str) > 0)
                    return ServerResponse.createByErrorMessage("邮箱已存在");
            }
        } else
            return ServerResponse.createByErrorMessage("参数错误");
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> getPasswordQuestion(String username) {
        ServerResponse<String> result = checkNotExisted(username, Const.USERNAME);
        if (result.isSuccess())
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        String question = userMapper.selectPasswordQuestionByUsername(username);
        if (StringUtils.isNotBlank(question))
            return ServerResponse.createBySuccessData(question);
        return ServerResponse.createByErrorMessage("没有找回密码问题");
    }

    @Override
    public ServerResponse<String> checkPasswordAnswer(String username, String question, String answer) {
        if (userMapper.checkPasswordAnswer(username, question, answer) > 0) {
            String passwordToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, passwordToken);
            return ServerResponse.createBySuccessMessage(passwordToken);
        }
        return ServerResponse.createByErrorMessage("答案错误");
    }

    @Override
    public ServerResponse<String> resetPasswordByPasswordToken(String username, String passwordNew, String passwordToken) {
        if (StringUtils.isBlank(passwordToken))
            return ServerResponse.createByErrorMessage("需要Token");
        String serverToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(serverToken))
            return ServerResponse.createByErrorMessage("token过期");
        if (StringUtils.equals(serverToken, passwordToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int result = userMapper.updatePasswordByUsername(username, md5Password);
            if (result > 0) {
                TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, StringUtils.EMPTY);
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }

        } else
            return ServerResponse.createByErrorMessage("Token错误");
        return ServerResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServerResponse<String> resetPasswordByPasswordOld(String passwordOld, String passwordNew, User user) {
        int checkResult = userMapper.checkPasswordByPrimaryKey(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (checkResult == 0)
            return ServerResponse.createByErrorMessage("旧密码错误");
        String passwordNewSalt = MD5Util.MD5EncodeUtf8(passwordNew);
        checkResult = userMapper.checkPasswordByPrimaryKey(passwordNewSalt, user.getId());
        if (checkResult > 0)
            return ServerResponse.createByErrorMessage("新密码重复");
        user.setPassword(passwordNewSalt);
        int updateResult = userMapper.updateByPrimaryKeySelective(user);
        if (updateResult > 0)
            return ServerResponse.createBySuccessMessage("密码更新成功");
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInfo(User user) {
        //username不能更新
        //email需要进行校验
        int checkResult = userMapper.checkEmailNotPrimaryKey(user.getEmail(), user.getId());
        if (checkResult > 0)
            return ServerResponse.createByErrorMessage("email已被使用");
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateResult = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateResult > 0) {
            User updatedUser = getInfo(user.getId()).getData();
            if (updatedUser != null)
                return ServerResponse.createBySuccess("更新信息成功", updatedUser);
            return ServerResponse.createBySuccess("更新信息成功", user);
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    @Override
    public ServerResponse<User> getInfo(Integer userId) {
        User userResult = userMapper.selectByPrimaryKey(userId);
        if (userResult == null)
            return ServerResponse.createByErrorMessage("找不到用户");
        userResult.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessData(userResult);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN)
            return ServerResponse.createBySuccess();
        return ServerResponse.createByError();
    }
}
