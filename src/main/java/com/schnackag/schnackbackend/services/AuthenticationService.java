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
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);
		var user = repository.findByEmail(request.getEmail())
				.orElseThrow();
		// var jwtToken = jwtService.generateToken(user);
		var extraClaims = new HashMap<String, Object>();
		extraClaims.put("role", user.getRole());
		var jwtToken = jwtService.generateToken(extraClaims, user);
		return AuthenticationResponse
				.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse register(RegisterRequest request) {
		Role role;

		if ((long) repository.findAll().size() == 0) {
			role = Role.ADMINISTRATOR;
		} else {
			role = Role.USER;
		}

		var user = User
				.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(role)
				.build();

		repository.save(user);

		var jwtToken = jwtService.generateToken(user);

		return AuthenticationResponse
				.builder()
				.token(jwtToken)
				.build();
	}
}
