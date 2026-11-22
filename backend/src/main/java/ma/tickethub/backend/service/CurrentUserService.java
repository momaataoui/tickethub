package ma.tickethub.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.config.DataInitializer;
import ma.tickethub.backend.entity.User;
import ma.tickethub.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    // TEMPORAIRE : au sprint 3, on lira l'utilisateur dans le JWT
    public User getCurrentUser() {
        return userRepository.findByEmail(DataInitializer.DEMO_ORGANIZER_EMAIL)
                .orElseThrow();
    }
}
