package org.destiny.activiti.springboot;

import org.destiny.activiti.ApiDispatcherServletConfiguration;
import org.destiny.activiti.AppDispatcherServletConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class SpringbootApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringbootApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean apiDispatcher() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setContextClass(AnnotationConfigWebApplicationContext.class);
		dispatcherServlet.setContextConfigLocation(ApiDispatcherServletConfiguration.class.getName());
		ServletRegistrationBean registrationBean = new ServletRegistrationBean();
		registrationBean.setServlet(dispatcherServlet);
		registrationBean.addUrlMappings("/api/*");
		registrationBean.setLoadOnStartup(1);
		registrationBean.setAsyncSupported(true);
		registrationBean.setName("api");
		return registrationBean;
	}

	@Bean
	public ServletRegistrationBean appDispatcher() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setContextClass(AnnotationConfigWebApplicationContext.class);
		dispatcherServlet.setContextConfigLocation(AppDispatcherServletConfiguration.class.getName());
		ServletRegistrationBean registrationBean = new ServletRegistrationBean();
		registrationBean.setServlet(dispatcherServlet);
		registrationBean.addUrlMappings("/app/*");
		registrationBean.setLoadOnStartup(1);
		registrationBean.setAsyncSupported(true);
		registrationBean.setName("app");
		return registrationBean;
	}
}
