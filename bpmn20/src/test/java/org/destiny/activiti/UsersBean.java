package org.destiny.activiti;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author destiny
 * destinywk@163.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * design by 2018/12/16 09:58
 * @version 1.8
 * @since JDK 1.8.0_101
 */
@Slf4j
public class UsersBean implements Serializable {

    public List<String> getUsers(String userId) {
        log.info("userId: {}", userId);
        return Arrays.asList("destiny", "freedom", "justice");
    }
}
