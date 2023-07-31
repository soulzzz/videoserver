package com.soul.videoserver.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger访问地址
 * http://localhost:80/swagger-ui/index.html
 * 一些常用注解说明
 * @Api：用在controller类，描述API接口
 * @ApiOperation：描述接口方法
 * @ApiModel：描述对象
 * @ApiModelProperty：描述对象属性
 * @ApiImplicitParams：描述接口参数
 * @ApiResponses：描述接口响应
 * @ApiIgnore：忽略接口方法
 */
@EnableKnife4j
@Configuration
@EnableOpenApi
public class Swagger3Config {

    @Bean
    public Docket coreApiConfig(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(getApiInfo())
                .groupName("apis")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.soul.videoserver.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("api文档")
                .description("管理系统接口描述")
                .version("1.0")
                .build();
    }
}
