package it.gym.config;

import it.gym.model.Tenant;
import it.gym.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class MultiTenancyInterceptor extends OncePerRequestFilter {

    @Autowired
    TenantRepository repository;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
        String tenantUuid = request.getHeader("X-Tenant");
        logger.debug("preHandle TenantContext");
        // TODO this is fixed for single schema
        String defaultTenantSchemaName = repository.findAll().get(0).getSchemaName();
        Tenant tenant = tenantUuid!=null? repository.findById(tenantUuid).orElse(null): null;
        String schema = tenant!= null ? tenant.getSchemaName(): defaultTenantSchemaName;
        TenantContext.setCurrentTenantSchema(schema);
        filterChain.doFilter(request, response);
    }
}
