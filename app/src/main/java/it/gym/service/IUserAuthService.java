package it.gym.service;

import it.gym.model.AUser;
import it.gym.model.VerificationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface IUserAuthService extends UserDetailsService {

    AUser register(AUser user);

    VerificationToken createOrChangeVerificationToken(AUser user, String token);

    VerificationToken getVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String existingToken);

    AUser changePassword(String email, String password);
}
