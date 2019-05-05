package it.gym.repository;

import it.gym.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

// TODO add preauthorize to repositories (CLI)
@RepositoryRestResource(path="/admins")
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByEmail(String email);

}
