package dev.aries.iijra.module.user;

import java.util.UUID;

import dev.aries.iijra.constant.ExceptionConstant;
import dev.aries.iijra.enums.Role;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
		return UUID.randomUUID().toString();
	}

	private void validateUserEmail(@NonNull String email) {
		if (userRepo.existsByEmail(email)) {
			throw new EntityExistsException(ExceptionConstant.USER_EMAIL_ALREADY_EXISTS + email);
		}
	}

	private void checkCurrentPassword(String encodedPassword, String rawPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new IllegalArgumentException(ExceptionConstant.INVALID_CURRENT_PASSWORD);
		}
	}

	private User getUserById(Long id) {
		return userRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.USER_ID_DOESNT_EXIST + id));
	}

	@Transactional
	public String updatePassword(Long id, @Valid PasswordUpdateRequest request) {
		User existingUser = getUserById(id);
		checkCurrentPassword(existingUser.getPassword(), request.currentPassword());
		existingUser.setPassword(passwordEncoder.encode(request.newPassword()));
		userRepo.save(existingUser);
		return "Password updated successfully";
	}

	@Transactional
	public String archiveUser(Long id) {
		User user = getUserById(id);
		if (Boolean.TRUE.equals(user.getArchived())) {
			throw new IllegalStateException(ExceptionConstant.USER_ALREADY_ARCHIVED + id);
		}
		user.archive();
		userRepo.save(user);
		return String.format("User %s has been archived", user.getEmail());
	}

	@Transactional
	public String restoreArchivedUser(Long id) {
		User user = getUserById(id);
		if (Boolean.FALSE.equals(user.getArchived())) {
			throw new IllegalStateException(ExceptionConstant.USER_NOT_ARCHIVED + id);
		}
		user.restore();
		userRepo.save(user);
		return String.format("User %s has been restored", user.getEmail());
	}
}
