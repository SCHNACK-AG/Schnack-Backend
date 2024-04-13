package com.schnackag.schnackbackend.services;

import com.schnackag.schnackbackend.entities.Role;
import com.schnackag.schnackbackend.entities.User;
import com.schnackag.schnackbackend.repositories.UserRepository;
import com.schnackag.schnackbackend.requests.AuthenticationRequest;
import com.schnackag.schnackbackend.requests.RegisterRequest;
import com.schnackag.schnackbackend.responses.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;

	/* Diese Methode authentifiziert den Benutzer. Sie nimmt eine Authentifizierungsanforderung entgegen
	 * und gibt bei erfolgreicher Authentifizierung ein JWT zurück. */
	public AuthenticationResponse authenticate(AuthenticationRequest request) {

		/* Aus der E-Mail und dem Passwort wird ein UsernamePasswordAuthenticationToken erstellt, das dann mittels
		 * der Methode authenticate() des AuthenticationManagers überprüft wird. */
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);

		/* Nachdem der Benutzer authentifiziert wurde, wird er aus der Datenbank geladen. */
		var user = repository.findByEmail(request.getEmail())
				.orElseThrow();

		/* Hier werden die zusätzlichen Claims erstellt die dem JWT hinzugefügt werden. */
		var extraClaims = new HashMap<String, Object>();
		extraClaims.put("role", user.getRole());

		/* An den JwtService werden der Benutzer und die zusätzlichen Claims übergeben und ein JWT erstellt. */
		var jwtToken = jwtService.generateToken(extraClaims, user);

		/* Das JWT wird in eine AuthenticationResponse übertragen und zurückgegeben. */
		return AuthenticationResponse
				.builder()
				.token(jwtToken)
				.build();
	}

	/* Diese Methode ist für die Registrierung neuer Benutzer zuständig. Sie nimmt eine Registrierungsanforderung
	 * entgegen und gibt nach erfolgreicher Registrierung ein JWT zurück. */
	public AuthenticationResponse register(RegisterRequest request) {
		Role role;

		/* Wenn in der Benutzerdatenbank noch kein Benutzer vorhanden ist, weise dem ersten Benutzer die Rolle
		 * Administrator zu, jeden weiteren die Rolle User. */
		if ((long) repository.findAll().size() == 0) {
			role = Role.ADMINISTRATOR;
		} else {
			role = Role.USER;
		}

		/* Hier wird ein neuer Benutzer mit den Daten aus der Anforderung erstellt. Das Passwort wird mit
		 * dem PasswordEncoder verschlüsselt und dem Benutzer wird eine Rolle hinzugefügt. */
		var user = User
				.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(role)
				.build();

		/* Der Benutzer wird mittels des UserRepository in der Datenbank gespeichert. */
		repository.save(user);

		/* Mit den Daten des Benutzers wird mit dem JwtService eine JWT generiert. */
		var jwtToken = jwtService.generateToken(user);

		/* Das Token wird in eine Authentifizierungsantwort eingefügt und zurückgegeben. */
		return AuthenticationResponse
				.builder()
				.token(jwtToken)
				.build();
	}
}
