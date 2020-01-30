package it.gym.facade;

import it.gym.controller.ReservationController;
import it.gym.exception.BadRequestException;
import it.gym.exception.NotFoundException;
import it.gym.model.*;
import it.gym.repository.OptionRepository;
import it.gym.service.TrainingBundleService;
import it.gym.service.TrainingBundleSpecificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;


@Component
@Transactional
public class TrainingBundleSpecificationFacade {

    private static final Logger logger = LoggerFactory.getLogger(TrainingBundleSpecificationFacade.class);

    @Autowired
    private TrainingBundleSpecificationService service;

    @Autowired
    private OptionRepository repository;

    @Autowired
    @Qualifier("trainingBundleService")
    private TrainingBundleService bundleService;

    public ATrainingBundleSpecification findById(Long id) {
        return service.findById(id);
    }

    public void delete(ATrainingBundleSpecification spec) {
        List<ATrainingBundle> bundles = bundleService.findBundlesBySpec(spec);
        boolean isDeletable = bundles.isEmpty();
        if (!isDeletable) {
            throw new BadRequestException("Non è possibile eliminare un pacchetto attualmente in uso.");
        }
        service.delete(spec);
    }

    public ATrainingBundleSpecification createTrainingBundleSpecification(ATrainingBundleSpecification spec) {
        return service.save(spec);
    }

    public Page<ATrainingBundleSpecification> findByNameContains(String name, Boolean disabled, Pageable pageable) {
        if (disabled == null && name == null)
            return service.findAll(pageable);
        else if (disabled == null) {
            return service.findByNameContains(name, pageable);
        } if (name == null) {
            return service.findByIsDisabled(disabled, pageable);
        } else {
            return service.findByNameAndIsDisabled(name, disabled, pageable);
        }
    }

    public ATrainingBundleSpecification save(ATrainingBundleSpecification spec) {
        return service.save(spec);
    }

    public Page<ATrainingBundleSpecification> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    public boolean existsByName(String name) {
        return service.existsByName(name);
    }

    public ATrainingBundleSpecification createOptionToBundleSpec(Long id, TimeOption option) {
        CourseTrainingBundleSpecification bundleSpec = (CourseTrainingBundleSpecification) this.service.findById(id);
        bundleSpec.addOption(option);
        return service.save(bundleSpec);
    }

    public List<ATrainingBundleSpecification> findByIsDisabled(Boolean disabled) {
        return service.findByIsDisabled(disabled);
    }

    public ATrainingBundleSpecification deleteOption(Long id, Long optionId) {
        CourseTrainingBundleSpecification c = (CourseTrainingBundleSpecification) this.findById(id);
        TimeOption o = this.repository.findById(optionId).orElseThrow(() -> new NotFoundException("Opzione non trovata"));
        c.getOptions().remove(o);
        try {
            repository.delete(o);
        } catch (Exception e) {
            throw new BadRequestException("Impossibile eliminare opzione in uso");
        }
        return service.save(c);
    }
}
