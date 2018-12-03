package org.destiny.activiti.coreapi;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

@Slf4j
public class IdentityServiceTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testIdentity() {
        IdentityService identityService = activitiRule.getIdentityService();
        User user1 = identityService.newUser("user1");
        user1.setEmail("destinywk@163.com");
        User user2 = identityService.newUser("user2");
        user2.setEmail("destinywk@126.com");
        identityService.saveUser(user1);
        identityService.saveUser(user2);

        Group group1 = identityService.newGroup("group1");
        identityService.saveGroup(group1);
        Group group2 = identityService.newGroup("group2");
        identityService.saveGroup(group2);

        // 创建关系
        identityService.createMembership("user1", "group1");
        identityService.createMembership("user2", "group1");
        identityService.createMembership("user1", "group2");

        List<User> userList = identityService.createUserQuery()
                .memberOfGroup("group1")
                .listPage(0, 100);

        for (User user : userList) {
            log.info("user: {}", ToStringBuilder.reflectionToString(user, ToStringStyle.JSON_STYLE));
        }

        List<Group> groupList = identityService.createGroupQuery()
                .groupMember("user1").listPage(0, 100);
        for (Group group : groupList) {
            log.info("group: {}", ToStringBuilder.reflectionToString(group, ToStringStyle.JSON_STYLE));
        }
    }

}
