package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);
    /**
     * 添加用户
     * @param user
     */
    @Insert("insert into user (openid,name,create_time) values (#{openid},#{name},#{createTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insert(User user);
    /**
     * 根据id查询用户
     * @param currentId
     * @return
     */
    @Select("select * from user where id = #{currentId}")
    User getById(Long currentId);
    /**
     * 查询用户数量
     * @param map
     * @return
     */
    Integer countUserByMap(Map map);

}
