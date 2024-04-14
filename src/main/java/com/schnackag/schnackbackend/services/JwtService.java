package com.schnackag.schnackbackend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
	private static final String SECRET_KEY = "ad7646d529b1c1a8a90850522750a3c8d797f557a6de45c1c3c16ff3418cc8df";

	/* Diese Methode nimmt ein JWT als String und gibt die Claim-Daten in Form
	 * eines Claims-Objekts zurück. Ein Claim in JWT ist eine Aussage über einen Benutzer,
	 * die Daten über den Benutzer und zusätzliche Metadaten enthält.
	 * Diese Methode verwendet das Keys.hmacShaKeyFor()-Verfahren und den Schlüssel,
	 * um das Token zu entschlüsseln und die Ansprüche zu extrahieren.*/
	private Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/* Diese Methode dient zum Extrahieren eines spezifischen Claims aus dem Token.
	 * Sie nimmt eine Funktion als Parameter, die angibt, welcher Claim extrahiert werden soll. */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/* Diese Methode verwendet die extractClaim()-Methode zur Extrahierung des Ablaufdatums des Tokens. */
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/* Diese Methode, verwendet die extractClaim()-Methode zur Extrahierung des Benutzernamens aus dem Token. */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/* generateToken(UserDetails userDetails) und generateToken(Map<String, Object> extraClaims, UserDetails userDetails):
	 * Diese Methoden werden verwendet, um ein neues JWT zu erstellen. Sie nehmen ein UserDetails-Objekt und ggf.
	 * eine Map von zusätzlichen Claims als Parameter. Das Token wird verschlüsselt und als String zurückgegeben. */
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return Jwts
				.builder()
				.claims().empty().add(extraClaims).and()
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
				.signWith(getSignInKey())
				.compact();
	}

	/* Diese Methode konvertiert den gegebenen Schlüssel (als Base64-String) in ein SecretKey-Objekt,
	 * das zum Verschlüsseln und Entschlüsseln des JWT verwendet wird. */
	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/* Diese Methode prüft, ob das Token abgelaufen ist,
	 * indem sie das Ablaufdatum des Tokens mit dem aktuellen Datum vergleicht. */
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/* Diese Methode verwendet extractUsername() und isTokenExpired() zur Überprüfung der Gültigkeit des Tokens.
	 * Sie gibt true zurück, wenn der Benutzername des Tokens dem Benutzernamen
	 * des übergebenen UserDetails-Objekts entspricht und das Token nicht abgelaufen ist. */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}
}
