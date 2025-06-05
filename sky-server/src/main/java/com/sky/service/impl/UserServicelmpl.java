package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sky.properties.WeChatProperties;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
public class UserServicelmpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    //微信服务接口地址
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //通过HttpClient,构造登录凭证校验请求
        //构造请求参数
        Map<String, String> query = new HashMap<>();
        query.put("appid", weChatProperties.getAppid());
        query.put("secret", weChatProperties.getSecret());
        query.put("js_code", userLoginDTO.getCode());
        query.put("grant_type", "authorization_code");
        //调用HttpClientUtil工具类，发送请求
        String res = HttpClientUtil.doGet(WX_LOGIN_URL, query);
        log.info("微信登录返回的json字符串：{}", res);
        // 通过code获取openid
        JSONObject jsonObject = JSON.parseObject(res);
        String openid = jsonObject.getString("openid");
        if  (openid == null) {
            throw new LoginFailedException(MessageConstant.USER_NOT_LOGIN);
        }
        //  通过openid查询数据库，判断用户是否存在
        User user = userMapper.getByOpenid(openid);
        // 如果用户不存在，则自动注册
        if (user == null) {
            user = User.builder()
                    .createTime(LocalDateTime.now())
                    .name(openid.substring(0,5))
                    .openid(openid).
                    build();
            userMapper.insert(user);
        }
        // 返回用户信息
        return user;

    }
}
