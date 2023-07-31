package com.soul.videoserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soul.videoserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
