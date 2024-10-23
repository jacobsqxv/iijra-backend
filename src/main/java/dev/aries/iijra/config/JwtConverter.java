package dev.aries.iijra.config;

import java.util.Collection;
import java.util.List;
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
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
	@Override
	public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
		Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
		return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Collection<String> roles = jwt.getClaimAsStringList("roles");
		return roles != null ?
				roles.stream()
						.map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
						.collect(Collectors.toList()) :
				List.of();
	}
}
