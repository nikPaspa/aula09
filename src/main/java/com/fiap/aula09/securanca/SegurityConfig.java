package com.fiap.aula09.securanca;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SegurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> 
		auth.anyRequest()
		.authenticated())
				.formLogin(withDefaults()
						).httpBasic(withDefaults());
		return httpSecurity.build();
	}

	@Bean
	public UserDetailsService multipleUsers() {

		UserBuilder user = User.withDefaultPasswordEncoder();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(user.username("coordenador")
				.password("senha01")
				.roles("coordenador")
				.build());

		return manager;
	}

	@Bean
	public SecurityFilterChain configureMultipleUserRoles(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf((csrf) -> 
		csrf.disable())
		.securityMatcher("/todo/list**")
				.authorizeHttpRequests(auth -> 
				auth.anyRequest().hasRole("ADMIN"))
				.httpBasic(withDefaults());

		return httpSecurity.build();

	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder,
			UserDetailsService userDetailsService) throws Exception {
		ProviderManager authenticationManager = (ProviderManager) http
				.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder).and()
				.build();
		return authenticationManager;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}