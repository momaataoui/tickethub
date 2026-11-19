package ma.tickethub.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.tickethub.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // connexion (étape 7)

    boolean existsByEmail(String email); // inscription : email déjà pris ?
}
