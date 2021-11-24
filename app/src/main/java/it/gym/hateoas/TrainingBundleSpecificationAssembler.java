package it.gym.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import it.gym.controller.ReservationController;
import it.gym.model.ATrainingBundleSpecification;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class TrainingBundleSpecificationAssembler
    extends RepresentationModelAssemblerSupport<
        ATrainingBundleSpecification, TrainingBundleSpecificationResource> {

  public TrainingBundleSpecificationAssembler() {
    super(
        ATrainingBundleSpecification.class,
        TrainingBundleSpecificationResource.class);
  }

  @Override
  public TrainingBundleSpecificationResource toModel(
      ATrainingBundleSpecification specs) {
    TrainingBundleSpecificationResource resource =
        new TrainingBundleSpecificationResource(specs);
    resource.add(
        linkTo(ReservationController.class)
            .slash("bundleSpecs")
            .slash(specs.getId())
            .withSelfRel());
    return resource;
  }
}
