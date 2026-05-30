package leiphotos.domain.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import leiphotos.domain.facade.GPSCoordinates;

class PhotoTest {

	@Test
	void testCreatePhotoWithoutGPS(){
		LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		PhotoMetadata meta = new PhotoMetadata(java.util.Optional.empty(), expectedCapturedDate, "test camara",
				"test manufaturer");
		File expectedFile = new File("test.jpg");
		String expectedTitle = "Test Photo";
		LocalDateTime expectedAddedDate = LocalDateTime.now();
		Photo photo = new Photo(expectedTitle, expectedAddedDate, meta, expectedFile);

		LocalDateTime actualCapturedDate = photo.capturedDate();
		assertEquals(expectedCapturedDate, actualCapturedDate);

		LocalDateTime actualAddedDate = photo.addedDate();
		assertEquals(expectedAddedDate, actualAddedDate);

		boolean isFavourite = photo.isFavourite();
		assertFalse(isFavourite);

		String actualTitle = photo.title();
		assertEquals(expectedTitle, actualTitle);

		Optional<? extends GPSCoordinates> actualPlace = photo.getPlace();
		assertEquals(Optional.empty(), actualPlace);
	}

	@Test
	void testCreatePhotoWithGPS() {

	 GPSLocation expectedGps = new leiphotos.domain.core.GPSLocation(0.0, 0.0, "");
	 LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
	 PhotoMetadata meta = new PhotoMetadata(java.util.Optional.of(expectedGps), expectedCapturedDate, "test camara", "test manufaturer");
	 
	 File fileWithGPS = new File("test.jpg");
	 Photo p = new Photo("Test", LocalDateTime.now(), meta, fileWithGPS);
	 
	
	 assertTrue(p.getPlace().isPresent());
	 assertEquals(expectedGps, p.getPlace().get());
    }
	

	@Test
	void testToggleFavourite(){
		LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		PhotoMetadata meta = new PhotoMetadata(java.util.Optional.empty(), expectedCapturedDate, "test camara",
				"test manufaturer");
		File expectedFile = new File("test.jpg");
		String expectedTitle = "Test Photo";
		LocalDateTime expectedAddedDate = LocalDateTime.now();
		Photo photo = new Photo(expectedTitle, expectedAddedDate, meta, expectedFile);

		photo.toggleFavourite();
		boolean isFavouriteAfterToggle1 = photo.isFavourite();
		photo.toggleFavourite();
		boolean isFavouriteAfterToggle2 = photo.isFavourite();

		// Assert
		assertTrue(isFavouriteAfterToggle1);
		assertFalse(isFavouriteAfterToggle2);
	}

	@Test
	void testSize() { // requires the use of a mock file class
		long expectedSize = 1024;
		MockFile expectedFile = new MockFile("test.jpg", expectedSize);
		GPSLocation expectedGps = new leiphotos.domain.core.GPSLocation(0.0, 0.0, "");
	    LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
	    PhotoMetadata meta = new PhotoMetadata(java.util.Optional.of(expectedGps), expectedCapturedDate, "test camara", "test manufaturer");

		Photo p = new Photo("", expectedCapturedDate, meta, expectedFile);
	
		assertEquals (expectedSize, p.size(), "O tamanho da foto deve ser 1024.");
	}

	@Test
	void testNoMatches(){
		String regexp = "Exp.*";
		PhotoMetadata meta = new PhotoMetadata(java.util.Optional.empty(), LocalDateTime.now(), "", "");
		Photo photo = new Photo("Test Photo", LocalDateTime.now(), meta, new File("test.jpg"));

		boolean matches = photo.matches(regexp);
		assertFalse(matches);
	}

	@Test
	void testMatchesTitle() {

		String testTitle = "banana";
		GPSLocation expectedGps = new leiphotos.domain.core.GPSLocation(0.0, 0.0, "");
	    LocalDateTime expectedCapturedDate = LocalDateTime.now();
	    PhotoMetadata meta = new PhotoMetadata(java.util.Optional.of(expectedGps), expectedCapturedDate, "test camara", "test manufaturer");
		MockFile mock = new MockFile(testTitle, 0);

		Photo p1 = new Photo(testTitle, expectedCapturedDate, meta, mock);

		assertTrue(p1.matches("ba.*"));
		assertFalse(p1.matches("pêra"));
	}

	@Test
	void testMatchesFile() {

	    String testTitle = "banana";
		GPSLocation expectedGps = new leiphotos.domain.core.GPSLocation(0.0, 0.0, "");
	    LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
	    PhotoMetadata meta = new PhotoMetadata(java.util.Optional.of(expectedGps), expectedCapturedDate, "test camara", "test manufaturer");
		MockFile mock = new MockFile(testTitle, 0);

		Photo p1 = new Photo(testTitle, expectedCapturedDate, meta, mock);

		assertTrue(p1.matches("tes.*"));
		assertFalse(p1.matches("aaaa"));
	}

	@Test
	void testEquals() {
		File file1 = new File("test1.jpg");
		File file2 = new File("test2.jpg");

		String testTitle = "banana";
		GPSLocation expectedGps = new leiphotos.domain.core.GPSLocation(0.0, 0.0, "");
	    LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
	    PhotoMetadata meta = new PhotoMetadata(java.util.Optional.of(expectedGps), expectedCapturedDate, "test camara", "test manufaturer");

		Photo p1 = new Photo(testTitle, expectedCapturedDate, meta, file2);
		Photo p2 = new Photo(testTitle, expectedCapturedDate, meta, file2);
		Photo p3 = new Photo(testTitle, expectedCapturedDate, meta, file1);
        
		assertTrue(p1.equals(p2)); // São iguais se tiverem o mesmo path
		assertFalse(p1.equals(p3));
	}

	@Test
	void testHashCode() {
		File file1 = new File("test1.jpg");
		File file2 = new File("test2.jpg");

		String testTitle = "banana";
		GPSLocation expectedGps = new leiphotos.domain.core.GPSLocation(0.0, 0.0, "");
	    LocalDateTime expectedCapturedDate = LocalDateTime.of(2024, 1, 1, 0, 0);
	    PhotoMetadata meta = new PhotoMetadata(java.util.Optional.of(expectedGps), expectedCapturedDate, "test camara", "test manufaturer");

		Photo p1 = new Photo(testTitle, expectedCapturedDate, meta, file2);
		Photo p2 = new Photo(testTitle, expectedCapturedDate, meta, file2);
		Photo p3 = new Photo(testTitle, expectedCapturedDate, meta, file1);

		assertTrue(p1.hashCode() == p2.hashCode());
		assertFalse(p1.hashCode() == p3.hashCode());
	}
}