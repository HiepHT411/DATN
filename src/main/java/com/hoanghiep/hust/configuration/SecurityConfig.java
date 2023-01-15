package com.hoanghiep.hust.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

//import com.hoanghiep.hust.controller.rest.UserController;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	DataSource dataSource;

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService,
			PasswordEncoder encoder) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth
				.inMemoryAuthentication()
				.withUser("user").password(passwordEncoder().encode("password")).roles("USER")
				.and()
				.withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
	}

	@Configuration
	@Order(1)
	public static class RestWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/api/**")
					.authorizeRequests()
					.anyRequest()
						.permitAll()
				.and()
					.httpBasic()
				.and()
					.csrf()
						.disable()
					.logout()
						.logoutUrl("/user/logout")
						.logoutSuccessUrl("/home")
						.deleteCookies("JSESSIONID")
						.invalidateHttpSession(true);
		}
	}

	@Configuration
	public static class WebWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		PersistentTokenRepository persistentTokenRepository;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.formLogin()
					.loginPage("/user/login")
					.failureUrl("/user/login-error")
					.defaultSuccessUrl("/home", true)
				.and()
					.rememberMe()
					.tokenRepository(persistentTokenRepository)
				.and()
					.csrf()
						.disable()
					.logout()
					.logoutUrl("/user/logout")
						.logoutSuccessUrl("/home")
						.deleteCookies("JSESSIONID")
						.invalidateHttpSession(true);
		}
	}

}