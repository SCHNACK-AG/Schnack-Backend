package com.schnackag.schnackbackend.repositories;

import com.schnackag.schnackbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/* Das Repository fungiert als Mittler zwischen der Datenzugriffsschicht und der Anwendungslogikschicht.
*  Es kapselt die Logik, die Notwendig ist um Daten zu speichern und zu lesen.
*  Dieses Repository erweitert JpaRepository und stellt somit schon die grundlegenden Operationen zum
*  Erstellen, Lesen, Ändern und Löschen der Daten bereit. */
public interface UserRepository extends JpaRepository<User, UUID> {

	/* Spring generiert zur Laufzeit automatisch die Implementierung für die Methode findByEmail(String email).
	*  https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.query-creation */
	Optional<User> findByEmail(String email);
}
