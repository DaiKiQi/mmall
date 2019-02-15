package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password")String password);

    String selectPasswordQuestionByUsername(String username);

    int checkPasswordAnswer(@Param("username") String username, @Param("question")String question, @Param("answer")String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew")String passwordNew);

    int checkPasswordByPrimaryKey(@Param("password") String password, @Param("id") Integer id);

    int checkEmailNotPrimaryKey(@Param("email") String email,@Param("id") Integer id);
}