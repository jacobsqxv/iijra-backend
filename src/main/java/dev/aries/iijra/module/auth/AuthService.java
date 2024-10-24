package dev.aries.iijra.module.auth;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.TokenType;
import dev.aries.iijra.module.staff.Staff;
import dev.aries.iijra.module.staff.StaffRepository;
import dev.aries.iijra.module.token.TokenService;
import dev.aries.iijra.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
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

	private final StaffRepository staffRepo;
	private final JwtService jwtService;
	private final AuthenticationManager authManager;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;

	public LoginResponse login(LoginRequest request) {
		Staff staff = checkStaff(request.email());
		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						request.email(), request.password()
				));
		String token = jwtService.generateToken(auth);
		return LoginResponse.newResponse(staff, token);
	}

	private Staff checkStaff(String email) {
		return staffRepo.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.STAFF_EMAIL_DOESNT_EXIST + email));
	}

	public String forgotPassword(ForgotPassword request) {
		Staff staff = checkStaff(request.email());
		tokenService.addNewToken(staff, TokenType.PASSWORD_RESET);
		return "Code has been sent to your email address";
	}

	@Transactional
	public String resetPassword(String token, ResetPassword request) {
		Staff staff = checkStaff(request.email());
		tokenService.validateToken(staff, token);
		staff.setPassword(passwordEncoder.encode(request.password()));
		staffRepo.save(staff);
		tokenService.deleteUsedToken(staff, token);
		return "Password reset successfully! Proceed to login";
	}

}
