package dev.aries.iijra.config;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import lombok.NonNull;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Override
	public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
		Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
		return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Collection<String> roles = jwt.getClaim("roles");
		if (roles == null || roles.isEmpty()) {
			return Collections.emptyList();
		}
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
	}
}