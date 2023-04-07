package com.lee.framework.security.common.annotation;

import com.lee.framework.security.WebSecurityImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(WebSecurityImportSelector.class)
public @interface EnableAutoSecurity {

}
