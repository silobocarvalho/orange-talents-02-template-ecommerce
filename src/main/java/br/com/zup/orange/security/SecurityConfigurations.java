package br.com.zup.orange.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.zup.orange.repository.UserRepository;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter{

	@Autowired
	AuthenticationService authenticationService;
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	// Authentication configurations (login and password)
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(authenticationService).passwordEncoder(new BCryptPasswordEncoder());
	}

	// Endpoints - url configuration
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/auth").permitAll()
		.antMatchers("/h2-console/**").permitAll()
		.antMatchers(HttpMethod.POST, "/users/**").hasRole("MODERATOR")
		.antMatchers(HttpMethod.POST, "/categories/**").hasRole("MODERATOR")
		.antMatchers(HttpMethod.POST, "/order/**").hasRole("USER")
		.antMatchers(HttpMethod.POST, "/order/**").hasRole("MODERATOR")
		.antMatchers(HttpMethod.GET, "/products/**").hasRole("USER")
		.antMatchers(HttpMethod.POST, "/products/**").hasRole("USER")
		.antMatchers(HttpMethod.GET, "/products/**").hasRole("MODERATOR")
		.antMatchers(HttpMethod.POST, "/products/**").hasRole("MODERATOR")
		.anyRequest().authenticated().and().csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.addFilterBefore(new TokenAuthenticationFilter(tokenService, userRepository), UsernamePasswordAuthenticationFilter.class)
		.headers().frameOptions().disable();
	}

	// static resources configuration: js, css, html, images, etc.
	@Override
	public void configure(WebSecurity web) throws Exception {
	}
}
