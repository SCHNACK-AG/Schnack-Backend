package com.schnackag.schnackbackend.filters;

import com.schnackag.schnackbackend.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/* Diese Klasse ist ein benutzerdefinierter JWT Filter, der nur einmal pro Http-Request ausgeführt wird.
 * Der Filter verwendet den JwtService zum Lesen der Benutzerinformationen aus dem JWT und einen UserDetailsService zum
 * Abrufen der Benutzerdaten. */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

		/* Der Inhalt des Authorization Headers wird an authHeader übergeben.
		 * Beispiel für den Inhalt: "Bearer xxxxxxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxx" */
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		/* Wenn authHeader keinen Inhalt hat oder der String nicht mit "Bearer " beginnt,
		 * wird die Anfrage ohne weitere Aktion weitergegeben. */
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Das JWT beginnt an der 8. Stelle im String.
		jwt = authHeader.substring(7);

		// Mit dem JWT extrahieren wir die E-Mail des Benutzers.
		userEmail = jwtService.extractUsername(jwt);

		/* Wenn ein gültiger Benutzername im JWT gefunden wurde und aktuell keine Authentifizierung
		 * im SecurityContextHolder vorhanden ist, d.h. der Benutzer noch nicht authentifiziert ist,
		 * wird mithilfe des UserDetailsService das UserDetails Objekt geladen,
		 * was die Benutzerinformationen wie Benutzername, Passwort und Berechtigungen beinhaltet.
		 * Dann wird überprüft, ob das Token gültig ist. Ist dies der Fall, wird ein UsernamePasswordAuthenticationToken
		 * erstellt, der das UserDetails Objekt, die Anmeldedaten (die auf null gesetzt sind, da sie nicht benötigt werden,
		 * nachdem das Token bestätigt wurde) und die Berechtigungen des Benutzers erhält.
		 * Zusätzliche Authentifizierungsdetails werden von der Anfrage erstellt und zum Token hinzugefügt.
		 * Schließlich wird das Token im SecurityContextHolder gesetzt
		 * und die Anforderung wird durch die Filterkette weitergeleitet. */
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

			if (jwtService.isTokenValid(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
				);
				authToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
				);
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}

			filterChain.doFilter(request, response);
		}
	}
}
