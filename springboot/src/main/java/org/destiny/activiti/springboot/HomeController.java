package org.destiny.activiti.springboot;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        value = "测试使用"
)
@RestController
@Slf4j
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "hello world";
    }

}
