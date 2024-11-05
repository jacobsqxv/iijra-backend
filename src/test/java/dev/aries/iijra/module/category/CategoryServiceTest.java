package dev.aries.iijra.module.category;

import java.util.List;
import java.util.Optional;

import dev.aries.iijra.TestDataFactory;
import dev.aries.iijra.constant.ExceptionConstant;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
	private static final String TEST_CATEGORY_NAME = "Category";
	private static final Long TEST_CATEGORY_ID = 1L;

	@InjectMocks
	private CategoryService categoryService;
	@Mock
	private CategoryRepository categoryRepo;

	private Category testCategory;

	@Nested
	@DisplayName("Add category tests")
	class AddCategoryTests {
		private CategoryRequest testRequest;

		@BeforeEach
		void setUp() {
			testRequest = new CategoryRequest(TEST_CATEGORY_NAME);
		}

		@Test
		@DisplayName("Should successfully add new category if name does not exist")
		void addCategory_WithValidName_Success() {
			when(categoryRepo.existsByName(TEST_CATEGORY_NAME)).thenReturn(false);

			CategoryResponse response = categoryService.addNewCategory(testRequest);

			ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
			verify(categoryRepo).save(categoryCaptor.capture());
			Category savedCategory = categoryCaptor.getValue();

			assertEquals(savedCategory.getId(), response.id());
			assertEquals(savedCategory.getName(), response.name());
		}

		@Test
		@DisplayName("Should throw exception when category name already exists")
		void addCategory_ShouldThrowException_NameExists() {
			when(categoryRepo.existsByName(TEST_CATEGORY_NAME)).thenReturn(true);

			EntityExistsException ex = assertThrows(EntityExistsException.class,
					() -> categoryService.addNewCategory(testRequest));

			assertTrue(ex.getMessage().contains(ExceptionConstant.CATEGORY_NAME_ALREADY_EXISTS));
			verify(categoryRepo, never()).save(any(Category.class));
		}
	}

	@Nested
	@DisplayName("Get category(s) tests")
	class GetCategoryTests {
		private Category testCategory2;

		@BeforeEach
		void setUp() {
			testCategory = TestDataFactory.newCategory();
			testCategory.setId(1L);
			testCategory2 = TestDataFactory.newCategory();
			testCategory2.setId(2L);
		}

		@Test
		@DisplayName("Should successfully return category if ID exists")
		void getCategory_WithExistingId_Success() {
			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(testCategory));
			CategoryResponse response = categoryService.getCategoryById(TEST_CATEGORY_ID);
			assertEquals(TEST_CATEGORY_ID,response.id());
			verify(categoryRepo, times(1)).findById(TEST_CATEGORY_ID);
		}

		@Test
		@DisplayName("Should throw exception if id does not exist")
		void getCategory_WithNonExistingId_ShouldThrowException() {
			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.empty());
			EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
					() -> categoryService.getCategoryById(TEST_CATEGORY_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.CATEGORY_ID_DOESNT_EXIST));
		}

		@Test
		@DisplayName("Should successfully return list of categories")
		void getCategories_Success() {
			List<Category> testCategories = List.of(testCategory, testCategory2);
			when(categoryRepo.findAll()).thenReturn(testCategories);
			List<CategoryResponse> response = categoryService.getAllCategories();
			verify(categoryRepo).findAll();
			assertEquals(testCategories.size(),response.size());
		}
	}

	@Nested
	@DisplayName("Update category tests")
	class UpdateCategoryTests {
		private CategoryRequest testRequest;

		@BeforeEach
		void setUp() {
			testRequest = new CategoryRequest("Update category");
		}

		@Test
		@DisplayName("Should update category if exists and new name is valid")
		void updateCategory_IfIdExists_Success() {
			testCategory = TestDataFactory.newCategory();
			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(testCategory));
			when(categoryRepo.existsByName(testRequest.name())).thenReturn(false);

			CategoryResponse response = categoryService.updateCategory(TEST_CATEGORY_ID, testRequest);

			ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
			verify(categoryRepo).save(categoryCaptor.capture());
			Category savedCategory = categoryCaptor.getValue();

			assertEquals(savedCategory.getId(), response.id());
			assertEquals(savedCategory.getName(), response.name());
		}
	}

	@Nested
	@DisplayName("Archive category tests")
	class ArchiveCategoryTests {
		@BeforeEach
		void setUp() {
			testCategory = TestDataFactory.newCategory();
		}

		@Test
		@DisplayName("Should successfully archive category when not already archived")
		void archiveCategory_WithValidCategoryId_SuccessTest() {
			testCategory.setIsArchived(false);

			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(testCategory));

			String response = categoryService.archiveCategory(TEST_CATEGORY_ID);

			assertTrue(response.contains(testCategory.getName()));
			verify(categoryRepo, times(1)).save(any(Category.class));
		}

		@Test
		@DisplayName("Should throw exception when category is already archived")
		void archiveCategory_WithAlreadyArchivedCategory_ShouldThrowException() {
			testCategory.setIsArchived(true);

			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(testCategory));

			IllegalStateException ex = assertThrows(IllegalStateException.class,
					() -> categoryService.archiveCategory(TEST_CATEGORY_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.CATEGORY_ALREADY_ARCHIVED));
			assertTrue(ex.getMessage().contains(TEST_CATEGORY_ID.toString()));
		}
	}

	@Nested
	@DisplayName("Restore archived category tests")
	class RestoreCategoryTests {

		@BeforeEach
		void setUp() {
			testCategory = TestDataFactory.newCategory();
		}

		@Test
		@DisplayName("Should successfully restore archived category when already archived")
		void restoreArchivedCategory_WithValidCategoryId_SuccessTest() {
			testCategory.setIsArchived(true);

			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(testCategory));

			String response = categoryService.restoreArchivedCategory(TEST_CATEGORY_ID);

			assertTrue(response.contains(testCategory.getName()));
			verify(categoryRepo, times(1)).save(any(Category.class));
		}

		@Test
		@DisplayName("Should throw exception when category is not archived")
		void restoreArchivedCategory_WithAlreadyArchivedCategory_ShouldThrowException() {
			testCategory.setIsArchived(false);

			when(categoryRepo.findById(TEST_CATEGORY_ID)).thenReturn(Optional.of(testCategory));

			IllegalStateException ex = assertThrows(IllegalStateException.class,
					() -> categoryService.restoreArchivedCategory(TEST_CATEGORY_ID));
			assertTrue(ex.getMessage().contains(ExceptionConstant.CATEGORY_NOT_ARCHIVED));
			assertTrue(ex.getMessage().contains(TEST_CATEGORY_ID.toString()));
		}
	}

}
