package br.com.zup.orange.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.zup.orange.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

	@Value("${forum.jwt.expirationTime}")
	private String expirationTime;

	@Value("${forum.jwt.secret}")
	private String secret;

	public String generateToken(Authentication authentication) {

		User loggedUser = (User) authentication.getPrincipal();
		Date today = new Date();
		Date expirationDate = new Date(today.getTime() + Long.parseLong(expirationTime));

		return Jwts.builder().setIssuer("Mercado Livre").setSubject(loggedUser.getId().toString()).setIssuedAt(today)
				.setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public boolean isTokenValid(String token) {
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

}
