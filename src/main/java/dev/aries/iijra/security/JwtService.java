package dev.aries.iijra.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import dev.aries.iijra.module.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

	private final JwtEncoder encoder;

	public String generateToken(Authentication auth) {
		Instant now = Instant.now();
		List<String> roles = auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		User user = ((UserDetailsImpl) auth.getPrincipal()).user();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("IIJRA")
				.subject(user.getId().toString())
				.claim("email", user.getEmail())
				.claim("status", user.getStatus().name())
				.claim("roles", roles)
				.issuedAt(now)
				.expiresAt(now.plus(4, ChronoUnit.HOURS))
				.build();
		return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}
