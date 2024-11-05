package dev.aries.iijra.config;

import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aries.iijra.global.Response;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class JwtAuthConfig {
	private final ObjectMapper objMapper = new ObjectMapper();

	@Bean
	public AuthenticationEntryPoint jwtAuthEntryPoint() {
		return (request, response, authException) -> {
			HttpStatus status = HttpStatus.UNAUTHORIZED;

			response.setStatus(status.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			Response.ErrorResponse errorResponse = new Response.ErrorResponse(
					status.getReasonPhrase(),
					Collections.singleton(getDetailedMessage(authException)));

			response.getWriter().write(objMapper.writeValueAsString(errorResponse));
		};
	}

	@Bean
	public AccessDeniedHandler jwtAccessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			HttpStatus status = HttpStatus.FORBIDDEN;
			response.setStatus(status.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			Response.ErrorResponse errorResponse = new Response.ErrorResponse(
					status.getReasonPhrase(),
					Collections.singleton(accessDeniedException.getMessage()));

			response.getWriter().write(objMapper.writeValueAsString(errorResponse));
		};
	}

	private String getDetailedMessage(Exception exception) {
		if (exception.getCause() != null) {
			String message = exception.getCause().getMessage().toLowerCase();
			if (message.contains("expired")) {
				return "The JWT token has expired";
			} else if (message.contains("signature")) {
				return "Invalid JWT signature";
			} else if (message.contains("malformed")) {
				return "Malformed JWT token";
			}
		}
		return exception.getMessage();
	}
}
