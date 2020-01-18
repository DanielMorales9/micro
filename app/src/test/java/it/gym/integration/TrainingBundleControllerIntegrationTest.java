package it.gym.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.gym.model.*;
import it.gym.repository.TrainingBundleRepository;
import it.gym.repository.TrainingBundleSpecificationRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.Date;

import static it.gym.utility.Calendar.getNextMonday;
import static it.gym.utility.Fixture.*;
import static it.gym.utility.HateoasTest.expectTrainingBundle;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingBundleControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private TrainingBundleRepository repository;
    @Autowired private TrainingBundleSpecificationRepository specRepository;
    private PersonalTrainingBundle personalBundle;
    private CourseTrainingBundle courseBundle;
    private CourseTrainingBundleSpecification courseBundleSpec;

    @Before
    public void before() {

        PersonalTrainingBundleSpecification personalBundleSpec = createPersonalBundleSpec(1L, "personal", 11);
        courseBundleSpec = createCourseBundleSpec(1L, "course", 1, 1, 111.);

        courseBundleSpec = specRepository.save(courseBundleSpec);
        personalBundleSpec = specRepository.save(personalBundleSpec);

        TimeOption option = courseBundleSpec.getOptions().toArray(new TimeOption[]{})[0];

        courseBundle = createCourseBundle(1L, getNextMonday(), courseBundleSpec, option);
        personalBundle = createPersonalBundle(1L, personalBundleSpec);

        personalBundle = repository.save(personalBundle);
        courseBundle = repository.save(courseBundle);
    }

    @After
    public void after() {
        repository.deleteAll();
        specRepository.deleteAll();
    }

    @Test
    public void whenFindAllOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundles"))
                .andExpect(status().isOk());

        expectTrainingBundle(result, personalBundle, "content["+0+"]");
        expectTrainingBundle(result, courseBundle, "content["+1+"]");
    }

    @Test
    public void whenFindByIdOK() throws Exception {

        ResultActions result = mockMvc.perform(get("/bundles/"+courseBundle.getId()))
                .andExpect(status().isOk());

        expectTrainingBundle(result, courseBundle);
    }

    @Test
    public void whenFindByIdGotNotFound() throws Exception {
        mockMvc.perform(get("/bundles/1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchOK() throws Exception {

        ResultActions result = mockMvc.perform(get("/bundles/search?specId="+courseBundle
                .getBundleSpec().getId()))
                .andExpect(status().isOk());

        expectTrainingBundle(result, courseBundle, "content["+0+"]");
    }

    @Test
    public void whenSearchNotExpiredOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundles/searchNotExpired?specId="+courseBundle
                .getBundleSpec().getId()))
                .andExpect(status().isOk());

        expectTrainingBundle(result, courseBundle, "["+0+"]");
    }

    @Test
    public void whenDeleteOK() throws Exception {
        ResultActions result = mockMvc.perform(delete("/bundles/"+courseBundle.getId()))
                .andExpect(status().isOk());

        expectTrainingBundle(result, courseBundle);

    }

}
