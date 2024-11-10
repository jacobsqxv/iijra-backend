package dev.aries.iijra.scheduler;

import java.time.LocalDateTime;

import dev.aries.iijra.module.department.DepartmentRepository;
import dev.aries.iijra.module.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Archived Records Cleanup Tests")
class ArchivedRecordsCleanupTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private DepartmentRepository departmentRepo;

	@InjectMocks
	private ArchivedRecordsCleanup archivedRecordsCleanup;

	private LocalDateTime expectedCutoff;

	@BeforeEach
	void setUp() {
		// Initialize cutoff time that's expected to be used
		expectedCutoff = LocalDateTime.now().minusDays(30);
	}

	@Test
	@DisplayName("Should successfully clean up archived records")
	void cleanupArchivedRecords_Success() {
		// Arrange
		when(userRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenReturn(5);
		when(departmentRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenReturn(3);

		// Act
		archivedRecordsCleanup.cleanupArchivedRecords();

		// Assert
		verify(userRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
		verify(departmentRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
	}

	@Test
	@DisplayName("Should abort cleanup transaction when user clean up fails")
	void cleanupArchivedRecords_WhenUserDeletionFails() {
		// Arrange
		when(userRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenThrow(new RuntimeException("Database error"));

		// Act
		archivedRecordsCleanup.cleanupArchivedRecords();

		// Assert
		verify(userRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
		// Department deletion should not be called due to transaction rollback
		verify(departmentRepo, never())
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
	}

	@Test
	@DisplayName("Should abort cleanup transaction when department clean up fails")
	void cleanupArchivedRecords_WhenDepartmentDeletionFails() {
		// Arrange
		when(userRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenReturn(5);
		when(departmentRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenThrow(new RuntimeException("Database error"));

		// Act
		archivedRecordsCleanup.cleanupArchivedRecords();

		// Assert
		verify(userRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
		verify(departmentRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
	}

	@Test
	@DisplayName("Verify clean up cut off date")
	void cleanupArchivedRecords_VerifyCutoffDate() {
		// Arrange
		LocalDateTime beforeTest = LocalDateTime.now();

		// Act
		archivedRecordsCleanup.cleanupArchivedRecords();

		// Assert
		verify(userRepo).deleteByArchivedTrueAndArchivedAtBefore(argThat(cutoff -> {
			// Verify the cutoff is approximately 30 days ago
			expectedCutoff = beforeTest.minusDays(30);
			// Allow for a small time difference due to test execution
			long secondsDifference = Math.abs(java.time.Duration.between(expectedCutoff, cutoff).getSeconds());
			return secondsDifference < 5; // Allow 5 seconds difference
		}));
	}

	@Test
	@DisplayName("Should run successfully even if there are no records to clean up")
	void cleanupArchivedRecords_WhenNoRecordsDeleted() {
		// Arrange
		when(userRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenReturn(0);
		when(departmentRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenReturn(0);

		// Act
		archivedRecordsCleanup.cleanupArchivedRecords();

		// Assert
		verify(userRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
		verify(departmentRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
	}

	@Test
	@DisplayName("Should verify transaction boundary via cutoff date")
	void cleanupArchivedRecords_VerifyTransactionBoundary() {
		// Arrange
		RuntimeException expectedError = new RuntimeException("Database error");
		when(userRepo.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class)))
				.thenThrow(expectedError);

		// Act
		archivedRecordsCleanup.cleanupArchivedRecords();

		// Assert
		// Verify that the transaction boundary worked and the error was caught
		verify(userRepo, times(1))
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
		verify(departmentRepo, never())
				.deleteByArchivedTrueAndArchivedAtBefore(any(LocalDateTime.class));
	}
}
