package ma.tickethub.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.entity.Role;
import ma.tickethub.backend.entity.User;
import ma.tickethub.backend.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    public static final String DEMO_ORGANIZER_EMAIL = "organizer@tickethub.ma";

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Ne le crée qu'une seule fois (sinon : erreur email unique au 2e démarrage)
        if (userRepository.existsByEmail(DEMO_ORGANIZER_EMAIL)) {
            return;
        }
        User organizer = new User();
        organizer.setEmail(DEMO_ORGANIZER_EMAIL);
        organizer.setFullName("Organisateur Démo");
        organizer.setPasswordHash("temporaire"); // remplacé par BCrypt à l'étape 7
        organizer.setRole(Role.ORGANIZER);
        userRepository.save(organizer);
    }
}
