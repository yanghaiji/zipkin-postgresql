package com.javayh.zipkin.admin.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.javayh.zipkin.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-05-22
 */
@Component
@Mapper
public interface UserRepository extends BaseMapper<User> {

    /**
     * <p>
     * 查询自动当前用户
     * </p>
     *
     * @param username
     * @return com.javayh.zipkin.admin.entity.User
     * @version 1.0.0
     * @author hai ji
     * @since 2023/5/22
     */
    @Select("SELECT ID, username,PASSWORD FROM zipkin_users WHERE enabled = true AND username= #{username}")
    User findByUsername(String username);
}
