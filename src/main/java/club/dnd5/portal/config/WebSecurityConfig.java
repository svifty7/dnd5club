package club.dnd5.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/profile/**").hasRole("USER");
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");
		http.authorizeRequests().antMatchers("/webjars/**").permitAll();

		http.authorizeRequests().antMatchers("/robots.txt").permitAll();

		http.authorizeRequests().and().formLogin().loginPage("/login").defaultSuccessUrl("/", true).permitAll();
		http.authorizeRequests().and().logout().logoutSuccessUrl("/").permitAll();

		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/error.html");
		http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		http.cors().and().csrf().disable();

		http.httpBasic();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		 registry.addMapping("/**") .allowedOrigins( "https://svifty7.github.io/",
		 "http://localhost:8080", "http://localhost:30000", "http://localhost:8081",
		 "https://svifty7.stoplight.io/" ) .allowedMethods("*");
	}

	@Override
	public final void configure(final WebSecurity web) throws Exception {
		super.configure(web);
		web.ignoring().antMatchers("/resources/**");
		web.httpFirewall(new AnnotatingHttpFirewall());
		return;
	}
}
