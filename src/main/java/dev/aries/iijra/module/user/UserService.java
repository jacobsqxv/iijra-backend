package dev.aries.iijra.module.user;

import java.util.UUID;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User createUser(String email, Role role) {
		validateUserEmail(email);
		String tempPassword = generateTemporaryPassword();
		User newUser = new User(
				email,
				passwordEncoder.encode(tempPassword),
				role);
		return userRepo.save(newUser);
	}

	private String generateTemporaryPassword() {
		String password = UUID.randomUUID().toString();
		log.info("Generated temporary password: {}", password);
		return password;
	}

	private void validateUserEmail(@NonNull String email) {
		if (userRepo.existsByEmail(email)) {
			throw new EntityExistsException(ExceptionConstant.USER_EMAIL_ALREADY_EXISTS + email);
		}
	}

	private void checkPassword(String encodedPassword, String rawPassword) {
		if (passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new IllegalArgumentException(ExceptionConstant.PASSWORD_ALREADY_USED);
		}
	}

	/**
	 * Promotes a user to the role of SYS_ADMIN based on their email.
	 *
	 * @param email the email of the user to be promoted
	 * @throws jakarta.persistence.EntityNotFoundException if no user with the given email is found
	 */
	@Transactional
	public void promoteToSysAdmin(String email) {
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(
						ExceptionConstant.USER_EMAIL_DOESNT_EXIST + email));
		if (user.getStaff() != null) {
			throw new IllegalStateException("Staff cannot be an admin");
		}
		user.setRole(Role.SYS_ADMIN);
		userRepo.save(user);
		log.info("User: with email {} has been promoted to a SYS_ADMIN", email);
	}
}
