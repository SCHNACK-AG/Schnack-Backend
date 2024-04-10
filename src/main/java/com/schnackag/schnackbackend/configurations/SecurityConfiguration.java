package com.schnackag.schnackbackend.configurations;

import com.schnackag.schnackbackend.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/* CORS (Cross-Origin Resource Sharing).
	 * Die Klasse CorsFilter ist eine konkrete Implementierung von OncePerRequestFilter.
	 * Dieser Filter ist verantwortlich für die Verarbeitung von CORS-Pre-Flight- und
	 * Ressourcenauslastungsanforderungen gemäß der CORS-Spezifikation. */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// Erlaubt die Verwendung von Cookies und Authentifizierungsanforderungen.
		config.setAllowCredentials(true);

		// Liste von URLs die Anforderungen senden dürfen.
		// http://localhost:4200 ist die Standard-URL von Angular in der Entwicklung.
		config.setAllowedOrigins(List.of("http://localhost:4200"));

		// Liste der erlaubten Header die mit der Anforderung gesendet werden dürfen.
		config.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT));

		// Liste der erlaubten Methoden die mit der Anforderung gesendet werden dürfen.
		config.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name()));

		// Maximale Lebensdauer von CORS-Pre-Flight-Anfragen.
		config.setMaxAge(3600L);

		// Registriert die CORS-Konfiguration.
		// Mit dem Pattern "/**" wird die Konfiguration auf alle Pfade angewendet.
		source.registerCorsConfiguration("/**", config);

		return new CorsFilter(source);
	}

	/* Eine SecurityFilterChain ist grundsätzlich eine Sammlung von Spring Security Filtern,
	 * die in einer bestimmten Reihenfolge arrangiert sind. Wenn eine HTTP-Anforderung ankommt,
	 * wird diese durch diese Filterkette verarbeitet. Jeder Filter in der Kette hat die Möglichkeit,
	 * die Anforderung zu analysieren, etwas zu ändern, die Verarbeitung zu unterbrechen
	 * oder die Anforderung einfach zum nächsten Filter in der Kette weiterzuleiten. */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity

				// Deaktiviert den CSRF (Cross-Site Request Forgery)-Schutz.
				.csrf(AbstractHttpConfigurer::disable)

				// Sicherheitsregeln für Requests.
				.authorizeHttpRequests(auth -> auth

						// Erlaube anonyme Anfragen an /api/v1/auth/**.
						// Damit ist es möglich Login- und Registrierungsanfragen zu senden.
						.requestMatchers("/api/v1/auth/**").permitAll()

						// Jeder andere Request muss authentifiziert sein.
						.anyRequest().authenticated())

				// Konfiguriert die Session-Erstellungspolitik.
				// Da wir nur JWT verwenden setzen wir sie auf STATELESS.
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Legt den Authentication-Provider fest.
				.authenticationProvider(authenticationProvider)

				// Legt einen benutzerdefinierten Filter fest,
				// der vor der Benutzer-Passwortauthentifizierung ausgeführt wird.
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}
}
