package org.destiny.activiti.workflow;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-12 11:18
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class DbIdTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mysql.xml");

    @Test
    public void testIdentity() {
        IdentityService identityService = activitiRule.getIdentityService();
        User user1 = identityService.newUser("user1");
        user1.setFirstName("firstName");
        user1.setLastName("lastName");
        user1.setEmail("user1@126.com");
        user1.setPassword("pwd");
        identityService.saveUser(user1);

        User user2 = identityService.newUser("user2");
        identityService.saveUser(user2);

        Group group1 = identityService.newGroup("group1");
        group1.setName("for test");
        identityService.saveGroup(group1);

        identityService.createMembership(user1.getId(), group1.getId());
        identityService.createMembership(user2.getId(), group1.getId());

        // 扩展信息
        identityService.setUserInfo(user1.getId(), "age", "18");
        identityService.setUserInfo(user1.getId(), "identity", "destiny");
    }
}
