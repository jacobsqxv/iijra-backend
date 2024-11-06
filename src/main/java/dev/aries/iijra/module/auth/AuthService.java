package dev.aries.iijra.module.auth;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Status;
import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.exception.UnauthorizedAccessException;
import dev.aries.iijra.module.token.TokenService;
import dev.aries.iijra.module.user.User;
import dev.aries.iijra.module.user.UserRepository;
import dev.aries.iijra.security.JwtService;
import dev.aries.iijra.security.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepo;
	private final JwtService jwtService;
	private final AuthenticationManager authManager;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	public LoginResponse login(LoginRequest request) {
		Authentication auth = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.email(), request.password()
				));
		log.info("Principal class: {}", auth.getPrincipal().getClass());
		User user = ((UserDetailsImpl) auth.getPrincipal()).user();
		checkIsInactiveOrIsDeleted(user);
		String token = jwtService.generateToken(auth);
		return new LoginResponse(token);
	}

	private void checkIsInactiveOrIsDeleted(User user) {
		if (Boolean.TRUE.equals(user.getIsArchived()) || Status.INACTIVE.equals(user.getStatus())) {
			throw new UnauthorizedAccessException(ExceptionConstant.ACCOUNT_DEACTIVATED);
		}
	}

	private User checkUser(String email) {
		return userRepo.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.USER_EMAIL_DOESNT_EXIST + email));
	}

	public String forgotPassword(ForgotPassword request) {
		User user = checkUser(request.email());
		tokenService.addNewToken(user, TokenType.PASSWORD_RESET);
		return "Code has been sent to your email address";
	}

	@Transactional
	public String resetPassword(@NonNull String token, ResetPassword request) {
		User user = tokenService.validateToken(request.email(), token);
		user.setPassword(passwordEncoder.encode(request.password()));
		userRepo.save(user);
		tokenService.deleteUsedToken(user, token);
		return "Password reset successfully! Proceed to login";
	}

}
