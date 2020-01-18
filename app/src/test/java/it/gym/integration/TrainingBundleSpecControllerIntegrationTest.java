package it.gym.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.gym.model.*;
import it.gym.repository.TrainingBundleRepository;
import it.gym.repository.TrainingBundleSpecificationRepository;
import it.gym.utility.HateoasTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.gym.utility.Calendar.getNextMonday;
import static it.gym.utility.Fixture.*;
import static it.gym.utility.HateoasTest.expectOption;
import static it.gym.utility.HateoasTest.expectTrainingBundleSpec;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingBundleSpecControllerIntegrationTest extends AbstractIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired private TrainingBundleSpecificationRepository repository;
    @Autowired private TrainingBundleRepository bundleRepository;

    private PersonalTrainingBundleSpecification personalBundle;
    private CourseTrainingBundleSpecification courseBundleSpec;

    @Before
    public void before() {
        personalBundle = createPersonalBundleSpec(1L, "personal", 11);
        courseBundleSpec = createCourseBundleSpec(1L, "course", 1, 1, 111.);
        personalBundle = repository.save(personalBundle);
        courseBundleSpec = repository.save(courseBundleSpec);
    }

    @After
    public void after() {
        bundleRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    public void findPersonalBundleSpecIdOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/" + personalBundle.getId()))
                .andExpect(status().isOk());
        logger.info(bundleRepository.findAll().toString());
        expectTrainingBundleSpec(result, personalBundle);
    }

    @Test
    public void whenPatchOK() throws Exception {
        personalBundle.setDescription("desco");
        ObjectMapper objectMapper = new ObjectMapper();
        ResultActions result = mockMvc.perform(patch("/bundleSpecs/"+personalBundle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(personalBundle)))
                .andExpect(status().isOk());
        expectTrainingBundleSpec(result, personalBundle);
    }

    @Test
    public void findCourseBundleSpecIdOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/" + courseBundleSpec.getId()))
                .andExpect(status().isOk());
        expectTrainingBundleSpec(result, courseBundleSpec);
    }

    @Test
    public void deletePersonalBundleSpecIdOK() throws Exception {
        mockMvc.perform(delete("/bundleSpecs/" + personalBundle.getId()))
                .andExpect(status().isOk());
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void deletePersonalBundleSpecId_throwException() throws Exception {
        bundleRepository.save(personalBundle.createTrainingBundle());
        mockMvc.perform(delete("/bundleSpecs/" + personalBundle.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCourseBundleSpecIdOK() throws Exception {
        mockMvc.perform(delete("/bundleSpecs/" + courseBundleSpec.getId()))
                .andExpect(status().isOk());
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void deleteCourseBundleSpecId_throwsException() throws Exception {
        CourseTrainingBundle bundle = createCourseBundle(1L, getNextMonday(),
                courseBundleSpec,
                courseBundleSpec.getOptions().toArray(new TimeOption[] {})[0]);
        bundleRepository.save(bundle);
        mockMvc.perform(delete("/bundleSpecs/" + courseBundleSpec.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deletePersonalBundleSpecId_throwsException() throws Exception {
        ATrainingBundle bundle = personalBundle.createTrainingBundle();
        ATrainingSession session = bundle.createSession(new Date(), new Date());
        session.setCompleted(true);
        bundle.addSession(session);
        bundleRepository.save(bundle);

        mockMvc.perform(delete("/bundleSpecs/" + personalBundle.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postPersonalBundleSpecOK() throws Exception {
        Object randomObj = new Object() {
            public final boolean disabled = false;
            public final String name = "pacchetto";
            public final String description = "pacchetto";
            public final Double price = 1.0;
            public final String type = "P";
            public final Integer numSessions = 11;
        };

        PersonalTrainingBundleSpecification expected = new PersonalTrainingBundleSpecification();
        expected.setNumSessions(11);
        expected.setName("pacchetto");
        expected.setDescription("pacchetto");
        expected.setDisabled(false);
        expected.setPrice(1.0);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(randomObj);
        logger.info(json);

        ResultActions result = mockMvc.perform(post("/bundleSpecs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        Long id = repository.findAll()
                .stream()
                .filter(a -> a.getName().equals("pacchetto"))
                .limit(1)
                .collect(Collectors.toList())
                .get(0)
                .getId();
        expected.setId(id);
        expectTrainingBundleSpec(result, expected);
    }

    @Test
    public void postCourseBundleSpecOptionOK() throws Exception {
        Object randomObj = new Object() {
            public final Integer number = 1;
            public final String name = "One Month Option";
            public final Double price = 111.0;
        };

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(randomObj);

        ResultActions result = mockMvc.perform(post("/bundleSpecs/"+courseBundleSpec.getId()+"/option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        CourseTrainingBundleSpecification expected = (CourseTrainingBundleSpecification)
                repository.findById(courseBundleSpec.getId()).get();

        expectTrainingBundleSpec(result, expected);
        ArrayList<TimeOption> options = new ArrayList<>(courseBundleSpec.getOptions());
        options.sort((o1, o2) -> (int) (o2.getId() - o1.getId()));
        logger.info(options.toString());
        for (int i = 0; i < options.size(); i++) {
            expectOption(result, options.get(i), "options["+i+"]");
        }
    }

    @Test
    public void whenFindAllOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs"))
                .andExpect(status().isOk());

        expectTrainingBundleSpec(result, personalBundle, "content["+0+"]");
        expectTrainingBundleSpec(result, courseBundleSpec, "content["+1+"]");
    }

    @Test
    public void whenSearchWAllParametersOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/search" +
                "?name="+ courseBundleSpec.getName()+
                "&disabled=false"))
                .andExpect(status().isOk());

        expectTrainingBundleSpec(result, courseBundleSpec, "content["+0+"]");
    }

    @Test
    public void whenSearchWDisabledTrueOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/search"+
                "?name="+ courseBundleSpec.getName()+
                "&disabled=true"))
                .andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void whenSearchWOParametersOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/search"))
                .andExpect(status().isOk());
        expectTrainingBundleSpec(result, personalBundle, "content["+0+"]");
        expectTrainingBundleSpec(result, courseBundleSpec, "content["+1+"]");
    }

    @Test
    public void whenSearchWDisabledFalseOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/search?disabled=false"))
                .andExpect(status().isOk());
        expectTrainingBundleSpec(result, personalBundle, "content["+0+"]");
        expectTrainingBundleSpec(result, courseBundleSpec, "content["+1+"]");
    }

    @Test
    public void whenSearchNotDisabledOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/searchNotDisabled?" +
                "query="+ courseBundleSpec.getName()))
                .andExpect(status().isOk());

        expectTrainingBundleSpec(result, courseBundleSpec, "content["+0+"]");    }

    @Test
    public void whenGetNotDisabledOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/bundleSpecs/getNotDisabled"))
                .andExpect(status().isOk());

        expectTrainingBundleSpec(result, personalBundle, "content["+0+"]");
        expectTrainingBundleSpec(result, courseBundleSpec, "content["+1+"]");
    }


    @Test
    public void postCourseBundleSpecOK() throws Exception {
        Object randomObj = new Object() {
            public final boolean disabled = false;
            public final String name = "pacchetto";
            public final String description = "pacchetto";
            public final Double price = 1.0;
            public final String type = "C";
            public final Integer maxCustomers = 11;
        };

        CourseTrainingBundleSpecification expected = new CourseTrainingBundleSpecification();
        expected.setName("pacchetto");
        expected.setDescription("pacchetto");
        expected.setDisabled(false);
        expected.setMaxCustomers(11);
        TimeOption o = new TimeOption();
        o.setPrice(1.0);
        o.setNumber(1);
        expected.addOption(o);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(randomObj);
        logger.info(json);

        ResultActions result = mockMvc.perform(post("/bundleSpecs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        Long id = repository.findAll()
                .stream()
                .filter(a -> a.getName().equals("pacchetto"))
                .limit(1)
                .collect(Collectors.toList())
                .get(0)
                .getId();
        expected.setId(id);
        bundleRepository.deleteAll();
        HateoasTest.expectTrainingBundleSpec(result, expected);
    }
}
