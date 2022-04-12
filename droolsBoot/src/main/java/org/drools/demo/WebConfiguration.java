package org.drools.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer  {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);

        registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");

//        registry.addResourceHandler("/static/**").addResourceLocations("/resources/static/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/fonts/");

        registry.addResourceHandler("/images/**").addResourceLocations("/resources/images/");

        registry.addResourceHandler("/javascripts/**").addResourceLocations("/resources/javascripts/");

        registry.addResourceHandler("/templates/**").addResourceLocations("/resources/templates/");

        registry.addResourceHandler("/libs/**").addResourceLocations("/resources/libs/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public DispatcherServlet dispatcherServlet(){
        DispatcherServlet dispatcherServlet = new DispatcherServlet();

        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        return dispatcherServlet;
    }

    /**
     * 以下是解决中文乱码
     */
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(responseBodyConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }




//    @Bean
//    public ViewResolver templateResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix(VIEW_PREFIX);
//        resolver.setSuffix(VIEW_SUFFIX);
//        resolver.setOrder(1);
//        resolver.setContentType(VIEW_CONTENT_TYPE);
//        return resolver;
//    }
}

