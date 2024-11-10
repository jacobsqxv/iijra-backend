package dev.aries.iijra.security;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dev.aries.iijra.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
	@InjectMocks
	private JwtService jwtService;
	@Mock
	private JwtEncoder jwtEncoder;

	@Test
	@DisplayName("Generate token with valid authentication")
	void generateToken_WithValidAuth() {
		Authentication auth = mock(Authentication.class);
		UserDetailsImpl userDetails = new UserDetailsImpl(TestDataFactory.newUser());
		when(auth.getPrincipal()).thenReturn(userDetails);
		Collection<SimpleGrantedAuthority> authorities =
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
		when(auth.getAuthorities())
				.thenAnswer(invocation -> authorities);

		Jwt jwt = mock(Jwt.class);
		when(jwt.getTokenValue()).thenReturn("mocked.jwt.token");

		ArgumentCaptor<JwtEncoderParameters> paramsCaptor =
				ArgumentCaptor.forClass(JwtEncoderParameters.class);

		when(jwtEncoder.encode(paramsCaptor.capture())).thenReturn(jwt);

		String token = jwtService.generateToken(auth);

		assertNotNull(token);
		assertEquals("mocked.jwt.token", token);

		JwtClaimsSet claims = paramsCaptor.getValue().getClaims();
		assertEquals(String.valueOf(1L), claims.getSubject());
		assertTrue(claims.getExpiresAt().isAfter(Instant.now()));
		assertEquals(List.of("ROLE_USER"),
				(claims.getClaim("roles")));

		verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
	}
}
