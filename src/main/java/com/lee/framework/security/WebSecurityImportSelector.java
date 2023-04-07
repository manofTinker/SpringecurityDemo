package com.lee.framework.security;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class WebSecurityImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{GlobalWebSecurityConfiguration.class.getName()};
    }
}
