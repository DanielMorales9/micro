package it.gym.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(
        name = "it.gym.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class TenantSchemaResolver implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_SCHEMA = "goodfellas";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t =  TenantContext.getCurrentTenant();
        if(t!=null){
            return t;
        } else {
            return DEFAULT_SCHEMA;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
