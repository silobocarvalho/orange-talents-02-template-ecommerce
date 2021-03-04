package br.com.zup.orange.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.zup.orange.repository.UserRepository;
import br.com.zup.orange.user.User;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private TokenService tokenService;
	private UserRepository userRepository;
	
	public TokenAuthenticationFilter(TokenService tokenService, UserRepository userRepository) {
		this.tokenService = tokenService;
		this.userRepository = userRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getTokenFromRequest(request);
		
		boolean tokenIsValid = tokenService.isTokenValid(token);
		
		System.out.println("Token from request: " + token);
		System.out.println("Token is valid? - " + tokenIsValid);
		
		if(tokenIsValid) {
			Long userId = tokenService.getUserIdFromToken(token);
			User user = userRepository.findById(userId).get();
			UsernamePasswordAuthenticationToken userAuthentication =  new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		}
		
		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}

}
