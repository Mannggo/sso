package com.larry.service.impl;

import com.larry.constant.RedisKeys;
import com.larry.enums.ServiceExceptionEnums;
import com.larry.enums.UserStatusEnums;
import com.larry.pojo.AuthInfo;
import com.larry.pojo.User;
import com.larry.pojo.UserInfo;
import com.larry.service.UserService;
import com.larry.util.JsonUtils;
import com.larry.util.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Optional.of;

/**
 * @author larry
 * @since 22:37 2019/05/26
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static List<User> USERS = Arrays.asList(
            new User("tony", "tony", 1000, UserStatusEnums.ACTIVE.getStatus()),
            new User("sarah", "sarah", 1001, UserStatusEnums.ACTIVE.getStatus()),
            new User("jerry", "jerry", 1002, UserStatusEnums.ACTIVE.getStatus()),
            new User("hok", "hok", 1003, UserStatusEnums.BANNED.getStatus())
    );

    @Override
    public AuthInfo verify(String username, String password, Integer systemId) {

        User user = USERS.stream()
                .filter(u -> Objects.equals(u.getUsername(), username))
                .findFirst().orElseThrow(ServiceExceptionEnums.USER_NOT_EXIST::getException);

        of(user).filter(u -> Objects.equals(u.getPassword(), password))
                .orElseThrow(ServiceExceptionEnums.PASSWORD_INCORRECT::getException);

        of(user).filter(u -> Objects.equals(u.getStatus(), UserStatusEnums.ACTIVE.getStatus()))
                .orElseThrow(ServiceExceptionEnums.USER_BANNED::getException);

        String token = "tk" + username;
        AuthInfo authInfo = AuthInfo.builder()
                .token(token)
                .userInfo(new UserInfo(user.getUsername(), ""))
                .lastSystemId(systemId)
                .build();
        return authInfo;
    }
}
