package dev.aries.iijra.controller;

import dev.aries.iijra.global.Response;
import dev.aries.iijra.module.admin.AdminRequest;
import dev.aries.iijra.module.admin.AdminService;
import dev.aries.iijra.module.user.PasswordUpdateRequest;
import dev.aries.iijra.module.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('SYS_ADMIN')")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final AdminService adminService;

	@PostMapping("/admins")
	public ResponseEntity<Object> addNewAdmin(@Valid @RequestBody AdminRequest request) {
		return Response.success(HttpStatus.CREATED, adminService.addNewSystemAdmin(request));
	}

	@PutMapping("{id}/password")
	@PreAuthorize("isFullyAuthenticated()")
	public ResponseEntity<Object> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateRequest request) {
		return Response.success(HttpStatus.OK, userService.updatePassword(id, request));
	}

	@PutMapping("{id}/archive")
	public ResponseEntity<Object> archiveUser(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, userService.archiveUser(id));
	}

	@PutMapping("{id}/restore")
	public ResponseEntity<Object> restoreArchivedUser(@PathVariable Long id) {
		return Response.success(HttpStatus.OK, userService.restoreArchivedUser(id));
	}
}
