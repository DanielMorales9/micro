package it.goodfellas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.goodfellas.exception.AvailabilityCheckException;
import it.goodfellas.exception.NotAllowedException;
import it.goodfellas.exception.POJONotFoundException;
import it.goodfellas.hateoas.ReservationAssembler;
import it.goodfellas.hateoas.ReservationResource;
import it.goodfellas.hateoas.TrainingSessionAssembler;
import it.goodfellas.hateoas.TrainingSessionResource;
import it.goodfellas.model.*;
import it.goodfellas.repository.TimeOffRepository;
import it.goodfellas.repository.TrainerRepository;
import it.goodfellas.repository.TrainingSessionRepository;
import it.goodfellas.repository.UserRepository;
import it.goodfellas.service.CustomerService;
import it.goodfellas.service.ReservationService;
import it.goodfellas.service.TrainingBundleService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class TrainingReservationController {

    private final TimeOffRepository timeRepository;
    private final TrainerRepository trainerRepository;
    private final CustomerService customerService;
    private final ReservationService reservationService;
    private final TrainingBundleService trainingBundleService;
    private final TrainingSessionRepository sessionRepository;
    private final UserRepository userRepository;

    private final static Logger logger = LoggerFactory.getLogger(TrainingReservationController.class);


    @Autowired
    public TrainingReservationController(TimeOffRepository timeRepository,
                                         TrainerRepository trainerRepository,
                                         TrainingBundleService trainingBundleService,
                                         TrainingSessionRepository sessionRepository,
                                         CustomerService customerService,
                                         UserRepository userRepository,
                                         ReservationService reservationService) {
        this.timeRepository = timeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingBundleService = trainingBundleService;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.customerService = customerService;
        this.reservationService = reservationService;
    }

    @GetMapping(path = "/reservations/checkAvailabilityAndEnablement",
            produces = "text/plain")
    @Transactional
    ResponseEntity<String> checkAvailableDay(@RequestParam("date")
                                             @DateTimeFormat(pattern="dd-MM-yyyy_HH:mm")
                                                     Date date,
                                             @RequestParam("id") Long id) {

        if (checkDateBeforeToday(date)) return new ResponseEntity<String>("Data non valida", HttpStatus.NOT_ACCEPTABLE);

        Customer customer = this.customerService.findById(id);

        boolean allDeleted = customer
                .getCurrentTrainingBundles()
                .parallelStream()
                .filter(ATrainingBundle::isExpired)
                .map(customer::deleteBundle)
                .reduce(Boolean::logicalAnd).orElse(true);

        if (!allDeleted) throw new AvailabilityCheckException();

        long bundleCount = customer
                .getCurrentTrainingBundles()
                .parallelStream()
                .filter(aTrainingBundle -> !aTrainingBundle.isExpired()).count();
        if (bundleCount == 0)
            return new ResponseEntity<String>("Non hai pacchetti", HttpStatus.NOT_ACCEPTABLE);

        Date endDate = DateUtils.addHours(date, 1);
        List<TimeOff> timesOff = this.timeRepository.findAllTimesOff(date, endDate);
        Stream<String> countAdmin = timesOff
                .parallelStream()
                .map(TimeOff::getType)
                .filter(s -> s.equals("admin")).limit(1);
        if (countAdmin.count() == 1)
            return new ResponseEntity<String>("Chiusura Aziendale", HttpStatus.NOT_ACCEPTABLE);

        Long numTrainers = this.trainerRepository.countAllTrainer();
        Long numOffTrainers = timesOff.parallelStream().filter(t -> t.getType().equals("worker")).count();
        long numAvailableTrainers = numTrainers - numOffTrainers;
        if (numAvailableTrainers == 0)
            return new ResponseEntity<String>("Nessun Personal Trainer", HttpStatus.NOT_ACCEPTABLE);

        List<Reservation> reservations = this.reservationService.findByStartTime(date);
        numAvailableTrainers = numAvailableTrainers - reservations.size();

        if (numAvailableTrainers == 0)
            return new ResponseEntity<String>("Questo orario è già stato prenotato", HttpStatus.NOT_ACCEPTABLE);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean checkDateBeforeToday(@DateTimeFormat(pattern = "dd-MM-yyyy_HH:mm")
                                         @RequestParam("date") Date date) {
        return date.before(new Date());
    }


    @GetMapping(path = "/reservations/book/{customerId}")
    @Transactional
    ResponseEntity<ReservationResource> book(@PathVariable Long customerId,
                                             @RequestParam("date")
                                             @DateTimeFormat(pattern="dd-MM-yyyy_HH:mm") Date startTime) {

        if (checkDateBeforeToday(startTime)) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Customer c = this.customerService.findById(customerId);
        List<ATrainingBundle> currentTrainingBundles = c.getCurrentTrainingBundles();
        if (currentTrainingBundles.size() == 0) {
            throw new AvailabilityCheckException();
        }
        ATrainingBundle trainingBundle = currentTrainingBundles.get(0);
        Date endTime = DateUtils.addHours(startTime, 1);
        Reservation res = trainingBundle.book(c, startTime, endTime);

        ATrainingSession session = this.sessionRepository.save(res.getSession());
        res.setSession(session);
        res = this.reservationService.save(res);
        trainingBundle.addSession(session);
        this.trainingBundleService.save(trainingBundle);

        // TODO: send notification to trainer

        return ResponseEntity.ok(new ReservationAssembler().toResource(res));

    }

    @DeleteMapping(path = "/reservations/{reservationId}")
    @Transactional
    ResponseEntity<ReservationResource> delete(@PathVariable Long reservationId,
                                               Principal principal) {
        Reservation res = this.reservationService.findById(reservationId);

        ATrainingSession session = res.getSession();

        AUser user = this.userRepository.findByEmail(principal.getName());
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        if (session.isDeletable() || (roles.contains("ADMIN") || roles.contains("TRAINER")))
            session.deleteMeFromBundle();
        else
            throw new NotAllowedException("Non è possibile eliminare la prenotazione");

        this.trainingBundleService.save(session.getTrainingBundle());
        sessionRepository.delete(session);
        this.reservationService.delete(res);

        return ResponseEntity.ok(new ReservationAssembler().toResource(res));
    }

    @GetMapping(path="/reservations")
    ResponseEntity<List<ReservationResource>> getReservations(@RequestParam(value = "id", required = false) Long id,
                                                        @RequestParam(value = "endDay")
                                                        @DateTimeFormat(pattern="dd-MM-yyyy_HH:mm") Date endDay,
                                                        @RequestParam(value = "startDay")
                                                        @DateTimeFormat(pattern="dd-MM-yyyy_HH:mm") Date startDay) {
        List<Reservation> res = this.reservationService
                .findByStartTimeAndEndTimeAndId(Optional.ofNullable(id), startDay, endDay);
        return ResponseEntity.ok(new ReservationAssembler().toResources(res));
    }

    @GetMapping(path="/reservations/complete/{sessionId}")
    @ResponseBody
    ResponseEntity<TrainingSessionResource> complete(@PathVariable(value = "sessionId") Long sessionId) {
        ATrainingSession session = this.sessionRepository.findById(sessionId)
                .orElseThrow(() -> new POJONotFoundException("Sessione", sessionId));
        session.complete();
        return ResponseEntity.ok(new TrainingSessionAssembler().toResource(session));
    }

    @GetMapping(path="/reservations/confirm/{reservationId}")
    @ResponseBody
    ResponseEntity<ReservationResource> confirm(@PathVariable(value = "reservationId") Long reservationId) {
        Reservation res = this.reservationService.findById(reservationId);
        res.setConfirmed(true);
        res = this.reservationService.save(res);
        return ResponseEntity.ok(new ReservationAssembler().toResource(res));
    }

    /*
    @GetMapping(path = "/reservation/confirm/{reservationId}")
    ResponseEntity<ReservationResource> book(@PathVariable Long reservationId,
                                             Principal principal) {
        Reservation a = this.reservationService.findById(reservationId);


        String trainerEmail = a.getTrainer().getEmail();
        String authEmail = principal.getName();

        if (!trainerEmail.equals(authEmail))
            throw new InvalidTrainerException(trainerEmail, authEmail);

        a.setConfirmed(true);

        a = this.reservationService.save(a);

        return ResponseEntity.ok(new ReservationAssembler().toResource(a));

    }*/


    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder() {
            public void configure(ObjectMapper objectMapper) {
                objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
                objectMapper.registerSubtypes(PersonalTrainingBundleSpecification.class);
                objectMapper.registerSubtypes(PersonalTrainingBundle.class);
                super.configure(objectMapper);
            };
        };
    }


    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public class RelProvider extends EvoInflectorRelProvider {
        @Override
        public String getCollectionResourceRelFor(final Class<?> type) {
            return super.getCollectionResourceRelFor(ATrainingBundleSpecification.class);
        }

        @Override
        public String getItemResourceRelFor(final Class<?> type) {
            return super.getItemResourceRelFor(ATrainingBundleSpecification.class);
        }

        @Override
        public boolean supports(final Class<?> delimiter) {
            return ATrainingBundleSpecification.class.isAssignableFrom(delimiter);
        }
    }


    @Configuration
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public class TrainingTypesMapper {

        private Map<String, String> mapper;

        public TrainingTypesMapper () {
            this.mapper = new HashMap<>();
            this.mapper.put("P", PersonalTrainingBundle.class.getSimpleName());
        }

        public String getTrainingClass(String type) {
            return mapper.get(type);
        }

    }
}
