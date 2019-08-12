package com.dexmohq.spring.eureka;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * @author Henrik Drefs
 */
@CommonsLog
public class MicroserviceGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        final AccessDecisionManager regularAccessDecisionManager = super.accessDecisionManager();
        final EnableGlobalSystemAccess globalSystemAccess = AnnotationUtils.findAnnotation(getClass(), EnableGlobalSystemAccess.class);
        if (globalSystemAccess == null) {
            return regularAccessDecisionManager;
        }
        log.info("Configuring global system access with role: " + globalSystemAccess.roleName());
        return new SystemRoleAwareAccessDecisionManager(regularAccessDecisionManager, globalSystemAccess.roleName());
    }

    private static boolean hasSystemRole(Authentication authentication, String roleName) {
        return authentication.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals(roleName));
    }

    private static class SystemRoleAwareAccessDecisionManager implements AccessDecisionManager {
        private final AccessDecisionManager regularAccessDecisionManager;
        private final String systemRole;

        public SystemRoleAwareAccessDecisionManager(AccessDecisionManager regularAccessDecisionManager, String systemRole) {
            this.systemRole = systemRole;
            this.regularAccessDecisionManager = regularAccessDecisionManager;
        }

        @Override
        public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
            if (hasSystemRole(authentication, systemRole)) {
                return;
            }
            regularAccessDecisionManager.decide(authentication, object, configAttributes);
        }

        @Override
        public boolean supports(ConfigAttribute attribute) {
            return regularAccessDecisionManager.supports(attribute);
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return regularAccessDecisionManager.supports(clazz);
        }
    }
}
