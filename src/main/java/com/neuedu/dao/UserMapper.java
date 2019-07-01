package com.neuedu.dao;

import com.neuedu.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    User selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(User record);


    /**
     *  查询用户名是否存在的方法
     */
    int selectUserByUserName(String userName);

    /**
     * 根据用户名和密码查询用户信息
     */
    User selectUserInfoByUserNameAndPsw(@Param("name") String userName, @Param("psw") String psw);


    /**
     * 查询邮箱是否存在
     */
    int selectEmailIsExists(String email);

    /**
     * 根据用户名查找密保问题
     */
    String getQuesByUserName(String userName);

    /**
     * 查询用户名问题答案是否存在
     */
    int queryUserAnswerIsExists(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);


    /**
     * 根据用户名修改密码
     */
    int  updateUserPswByUserName(@Param("username")String username,@Param("newpsw")String psw);


}