/**
 * <p>Title: SwaggerConfig.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.landasoft.com</p>  
 * @author wulinyun  
 * @date 2019年2月28日 下午5:01:21 
 * @version 1.0  
 */
package com.landasoft.mas.demo.solr.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>Title: SwaggerConfig</p>  
 * <p>Description: swagger配置</p>  
 * @author wulinyun  
 * @date 2019年2月28日 下午5:01:21
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
	@Bean
    public Docket globalApi() {
 
        List<Parameter> pars = new ArrayList<>();
        //pars.add(new ParameterBuilder().name("corp_key").description("当前企业标识").modelRef(new ModelRef("string")).parameterType("header").required(false).build());
        //pars.add(new ParameterBuilder().name("token").description("用户凭证").modelRef(new ModelRef("string")).parameterType("header").required(false).build());
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars)
                .securitySchemes(Collections.singletonList(new BasicAuth("basicAuth")))
                .securityContexts(Collections.singletonList(securityContext()))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }
 
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(basicAuth())
                .forPaths(PathSelectors.any())
                .build();
    }
 
    private List<SecurityReference> basicAuth() {
        return Collections.singletonList(SecurityReference.builder()
                .reference("basicAuth")
                .scopes(new AuthorizationScope[0])
                .build());
    }
 
 
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("springboot-demo-solr微应用")
                .description("demo微应用")
                .version("v1")
                .build();
    }
 
    @Bean
    public TranslationOperationBuilderPlugin translationPlugin() {
        return new TranslationOperationBuilderPlugin();
    }
 
    @Order(Ordered.LOWEST_PRECEDENCE)
    public static class TranslationOperationBuilderPlugin implements OperationBuilderPlugin {
 
        @Autowired
        private MessageSource translator;
 
        @Override
        public boolean supports(DocumentationType delimiter) {
            return true;
        }
 
        @Override
        public void apply(OperationContext context) {
            Set<ResponseMessage> messages = context.operationBuilder().build().getResponseMessages();
            Set<ResponseMessage> translated = new HashSet<>();
            for (ResponseMessage untranslated : messages) {
                String translation = translator.getMessage(untranslated.getMessage(), null, untranslated.getMessage(), null);
                translated.add(new ResponseMessage(untranslated.getCode(),
                        translation,
                        untranslated.getResponseModel(),
                        untranslated.getHeaders(),
                        untranslated.getVendorExtensions()));
            }
            context.operationBuilder().responseMessages(translated);
        }
 
    }
}
