package it.gym.service;

import it.gym.model.ATrainingBundle;
import it.gym.model.ATrainingBundleSpecification;
import it.gym.repository.CourseTrainingBundleRepository;
import it.gym.repository.TrainingBundleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static it.gym.utility.Fixture.createPersonalBundle;
import static it.gym.utility.Fixture.createPersonalBundleSpec;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class TrainingBundleServiceTest {

    @MockBean
    private TrainingBundleRepository repository;

    @MockBean
    private CourseTrainingBundleRepository courseRepository;

    @TestConfiguration
    static class TrainingBundleServiceTestContextConfiguration {

        @Bean
        public TrainingBundleService service() {
            return new TrainingBundleService();
        }
    }

    @Autowired
    private TrainingBundleService service;


    @Test
    public void findAll() {
        ATrainingBundleSpecification pe = createPersonalBundleSpec(1L, "personal", 11);
        ATrainingBundle bundle = createPersonalBundle(1L, pe);
        Mockito.when(repository.findAll()).thenAnswer(invocationOnMock -> Collections.singletonList(bundle));
        List<ATrainingBundle> u = this.service.findAll();
        assertThat(u).isEqualTo(Collections.singletonList(bundle));
        Mockito.verify(repository).findAll();
    }

}
