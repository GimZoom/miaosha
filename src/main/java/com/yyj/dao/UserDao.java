package com.yyj.dao;

import com.yyj.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserDao {
    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from user where id=#{id}")
    User findById(@Param("id") Long id);

    /**
     * 更新用户密码
     *
     * @param update
     */
    @Update("update user set password=#{password} where id=#{id}")
    void updatePassword(User update);
}
