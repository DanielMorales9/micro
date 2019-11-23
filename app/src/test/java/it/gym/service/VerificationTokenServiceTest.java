package it.gym.service;

import it.gym.model.AUser;
import it.gym.model.Admin;
import it.gym.model.VerificationToken;
import it.gym.repository.VerificationTokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static it.gym.utility.Fixture.createAdmin;
import static it.gym.utility.Fixture.createToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
public class VerificationTokenServiceTest {

    @MockBean private VerificationTokenRepository repository;

    @TestConfiguration
    static class VerificationTestContextConfiguration {

        @Bean
        public VerificationTokenService service() {
            return new VerificationTokenService();
        }
    }

    @Autowired
    private VerificationTokenService service;

    @Test
    public void save() {
        this.service.save(createToken(1L, "ababa", createAdmin(1L)));
        Mockito.verify(repository).save(any(VerificationToken.class));
    }

    @Test
    public void findById() {
        VerificationToken token = createToken(1L, "ababa", createAdmin(1L));
        Mockito.when(repository.findById(1L)).thenAnswer(invocationOnMock -> Optional.of(token));
        VerificationToken vk = this.service.findById(1L);
        assertThat(vk).isEqualTo(token);
        Mockito.verify(repository).findById(anyLong());
    }

    @Test
    public void findByToken() {
        VerificationToken token = createToken(1L, "ababa", createAdmin(1L));
        Mockito.when(repository.findByToken("ababa")).thenAnswer(invocationOnMock -> Optional.of(token));
        VerificationToken vk = this.service.findByToken("ababa");
        assertThat(vk).isEqualTo(token);
        Mockito.verify(repository).findByToken(anyString());
    }


    @Test
    public void findByUser() {
        VerificationToken token = createToken(1L, "ababa", createAdmin(1L));
        Mockito.when(repository.findByUser(createAdmin(1L))).thenAnswer(invocationOnMock -> Optional.of(token));
        VerificationToken vk = this.service.findByUser(createAdmin(1L));
        assertThat(vk).isEqualTo(token);
        Mockito.verify(repository).findByUser(any(AUser.class));
    }

    @Test
    public void createOrChangeVerificationToken() {
        VerificationToken vk = this.service.createOrChangeVerificationToken(createAdmin(1L));
        Mockito.verify(repository).findByUser(any(AUser.class));
    }

    @Test
    public void invalidateToken() {
        VerificationToken token = createToken(1L, "ababa", createAdmin(1L));
        service.invalidateToken(token);
        assertThat(token.isExpired()).isTrue();
    }

}
