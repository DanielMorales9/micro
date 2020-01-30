package it.gym.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gym.facade.TrainingBundleFacade;
import it.gym.hateoas.TrainingBundleAssembler;
import it.gym.hateoas.TrainingBundleResource;
import it.gym.model.ATrainingBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RepositoryRestController
@RequestMapping("/bundles")
@PreAuthorize("isAuthenticated()")
public class TrainingBundleController {

    @Autowired
    private TrainingBundleFacade service;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    @ResponseBody
    public Page<ATrainingBundle> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TrainingBundleResource> findById(@PathVariable Long id) {
        ATrainingBundle bundle = service.findById(id);
        return ResponseEntity.ok(new TrainingBundleAssembler().toResource(bundle));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<TrainingBundleResource> delete(@PathVariable Long id) {
        ATrainingBundle bundle = service.deleteById(id);
        return ResponseEntity.ok(new TrainingBundleAssembler().toResource(bundle));
    }

    @GetMapping("/search")
    @ResponseBody
    public Page<ATrainingBundle> findBySpecs(@RequestParam Long specId, Pageable pageable) {
        return service.findBundlesBySpecId(specId, pageable);
    }

    // TODO this and the above could be merged into a single call
    @GetMapping("/searchNotExpired")
    @ResponseBody
    public ResponseEntity<List<TrainingBundleResource>> findBySpecsNotExpired(@RequestParam Long specId) {
        List<ATrainingBundle> bundles = service.findBundlesBySpecIdNotExpired(specId);
        return ResponseEntity.ok(new TrainingBundleAssembler().toResources(bundles));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<TrainingBundleResource> patch(@PathVariable Long id,
                                                        HttpServletRequest request) throws IOException {
        ATrainingBundle bundle = service.findById(id);
        bundle = objectMapper.readerForUpdating(bundle).readValue(request.getReader());
        bundle = service.save(bundle);
        return ResponseEntity.ok(new TrainingBundleAssembler().toResource(bundle));
    }
}
