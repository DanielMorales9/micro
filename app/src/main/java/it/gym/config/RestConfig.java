package it.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.gym.model.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RestConfig extends RepositoryRestConfigurerAdapter {


    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder() {
            @Override
            public void configure(ObjectMapper objectMapper) {
                objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
                objectMapper.registerSubtypes(PersonalTrainingBundleSpecification.class);
                objectMapper.registerSubtypes(PersonalTrainingBundle.class);
                super.configure(objectMapper);
            }
        };
    }

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {

        return new RepositoryRestConfigurerAdapter() {

            @Override
            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
                config.setRepositoryDetectionStrategy(
                        RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED);
            }
        };
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(
                AUser.class,
                Sale.class,
                SalesLineItem.class,
                Payment.class,
                AEvent.class,
                ATrainingEvent.class,
                CourseEvent.class,
                PersonalEvent.class,
                Holiday.class,
                TimeOff.class,
                Gym.class,
                Reservation.class,
                ATrainingBundleSpecification.class,
                ATrainingBundle.class,
                ATrainingSession.class,
                CourseTrainingBundleSpecification.class,
                CourseTrainingBundle.class,
                CourseTrainingSession.class,
                TimeOption.class,
                PersonalTrainingBundleSpecification.class,
                PersonalTrainingBundle.class,
                PersonalTrainingSession.class,
                Admin.class,
                Role.class,
                Trainer.class,
                Customer.class);
    }
}
