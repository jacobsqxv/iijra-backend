package dev.aries.iijra.controller;

import dev.aries.iijra.global.Response;
import dev.aries.iijra.module.auth.LoginRequest;
import dev.aries.iijra.module.auth.AuthService;
import dev.aries.iijra.module.auth.ResetPassword;
import dev.aries.iijra.module.auth.ForgotPassword;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<Object> authenticate(@RequestBody LoginRequest request) {
		return Response.success(HttpStatus.OK, authService.login(request));
	}

	@PostMapping("/password/forgot")
	public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPassword request) {
		return Response.success(HttpStatus.OK, authService.forgotPassword(request));
	}

	@PostMapping("/password/reset")
	public ResponseEntity<Object> resetPassword(@RequestParam String token, @RequestBody ResetPassword request) {
		return Response.success(HttpStatus.OK, authService.resetPassword(token,request));
	}
}
