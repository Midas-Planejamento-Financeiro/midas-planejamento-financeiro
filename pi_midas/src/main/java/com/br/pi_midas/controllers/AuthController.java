package com.br.pi_midas.controllers;

import static org.springframework.http.ResponseEntity.ok;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.pi_midas.repository.UserRepository;
import com.br.pi_midas.security.CredenciaisConta;
import com.br.pi_midas.security.JwtProvider;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired	
	AuthenticationManager authenticationManager;

	@Autowired
	JwtProvider tokenProvider;

	@Autowired
	UserRepository repository;

	@PostMapping(value = "/signin", produces = { "application/json", "application/xml" }, 
			consumes = { "application/json",	"application/xml" })
	public ResponseEntity signin(@RequestBody CredenciaisConta cred) {
		
		try {
			var username = cred.getUsername();
			var password = cred.getPassword();
			
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password));
			
			var user = repository.findByUserName(username);
			var token = "";
			
			if (user !=null) {
				token = tokenProvider.createToken(username, user.getRoles());
			}else {
				throw new UsernameNotFoundException("Usuário " + username+ "não localizado");
			}
			Map<Object, Object> model = new HashMap<>();
			model.put("username", username);
			model.put("token", token);
			
			return ok(model);
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Usuário ou senha inválidos");
		}
		
	}

}