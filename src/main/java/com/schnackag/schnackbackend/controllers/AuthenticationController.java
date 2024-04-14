package com.schnackag.schnackbackend.controllers;

import com.schnackag.schnackbackend.requests.AuthenticationRequest;
import com.schnackag.schnackbackend.requests.RegisterRequest;
import com.schnackag.schnackbackend.responses.AuthenticationResponse;
import com.schnackag.schnackbackend.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authenticationService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}

	@GetMapping("/authenticated")
	public ResponseEntity<Boolean> isAuthenticated() {
		return ResponseEntity.ok(true);
	}
}
