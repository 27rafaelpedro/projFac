package leiphotos.domain.core;

import com.sun.source.tree.AssertTree;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import leiphotos.domain.facade.IPhoto;

class RecentlyDeletedLibraryTest {

	private static final int SECONDS_IN_TRASH = 3; //CHANGE ME
	private static final int SECONDS_TO_CHECK = 2;  //CHANGE ME
	
	private RecentlyDeletedLibrary library;

	@BeforeEach
	void setUp() {
		library = new RecentlyDeletedLibrary();
	}

	@Test
	void testAddPhoto() {
		MockPhoto photo = new MockPhoto(new File("Test.jpg"));

		assertTrue(library.addPhoto(photo));
		assertTrue(library.getPhotos().contains(photo));
		assertEquals(1, library.getNumberOfPhotos());
	}

	@Test
	void testAddExistingPhoto() {
		MockPhoto photo = new MockPhoto(new File("Test.jpg"));

        assertTrue(library.addPhoto(photo)); // Adicionar foto
		assertTrue(library.getPhotos().contains(photo)); // Verificar se foto foi adicionada
		assertFalse(library.addPhoto(photo)); // Tentar adicionar mesma foto, deve devolver False
		assertEquals(1, library.getNumberOfPhotos()); // Existe somente uma foto na biblioteca
	}


	@Test
	void testDeletePhoto() {
		MockPhoto photo = new MockPhoto(new File("Test.jpg"));
		library.addPhoto(photo); // Adicionar foto
		
		assertTrue(library.getPhotos().contains(photo)); // Verificar se foi adiconada
		assertEquals(1, library.getNumberOfPhotos()); // Temos somente uma foto na biblioteca
		assertTrue(library.deletePhoto(photo)); // Remover esta foto
		assertEquals(0, library.getNumberOfPhotos()); // Biblioteca vazia
		
	}

	@Test
	void testDeleteNotExistingPhoto() {
		MockPhoto photo1 = new MockPhoto(new File("One.jpg"));
		MockPhoto photo2 = new MockPhoto(new File("Two.jpg"));
		library.addPhoto(photo1); // Adicionar foto
		
		assertTrue(library.getPhotos().contains(photo1)); // Verificar se foi adiconada
		assertEquals(1, library.getNumberOfPhotos()); // Temos somente uma foto na biblioteca
		assertFalse(library.deletePhoto(photo2)); // Tentar apagar foto que não existe na biblioteca
	}


	@Test
	void testDeleteAll() {
		MockPhoto photo1 = new MockPhoto(new File("One.jpg"));
		MockPhoto photo2 = new MockPhoto(new File("Two.jpg"));
		library.addPhoto(photo1);
		library.addPhoto(photo2);

		assertEquals(2, library.getNumberOfPhotos()); // Temos duas fotos na biblioteca
		assertTrue(library.deleteAll()); // Apagar todas as fotos da biblioteca
		assertEquals(0, library.getNumberOfPhotos()); // Biblioteca vazia
	}

	@Test
	void testGetMatchesEmpty() {
		Collection<IPhoto> matches = library.getMatches(".*");
		assertNotNull(matches);

		assertTrue(matches.isEmpty());
	}

	@Test
	void testGetMatchesNotEmpty() {
		MockPhoto photoY = new MockPhoto(new File("Y.jpg"),true);
		MockPhoto photoN = new MockPhoto(new File("N.jpg"),false);
		library.addPhoto(photoY);
		library.addPhoto(photoN);
		Collection<IPhoto> matches = library.getMatches(".*");
		
		assertNotNull(matches);
		assertFalse(matches.isEmpty());
	}

	@Test
	void testAutomaticDelete() throws InterruptedException {
		MockPhoto photo1 = new MockPhoto(new File("One.jpg"));
		MockPhoto photo2 = new MockPhoto(new File("Two.jpg"));
		library.addPhoto(photo1);
		library.addPhoto(photo2);
		Thread.sleep(SECONDS_IN_TRASH * 1000);
		Collection<IPhoto> photos = library.getPhotos();

		assertNotNull(photos);
		assertTrue(photos.isEmpty());
		assertEquals(0, photos.size());
	}


	@Test
	void testAutomaticDeleteNoEffectTooSoon() {
		MockPhoto photo1 = new MockPhoto(new File("One.jpg"));
		MockPhoto photo2 = new MockPhoto(new File("Two.jpg"));
		library.addPhoto(photo1);
		library.addPhoto(photo2);
		Collection<IPhoto> photos = library.getPhotos();

		assertEquals (2 ,photos.size());
		assertTrue(photos.contains(photo1));
		assertTrue(photos.contains(photo2));
	}

	@Test
	void testAutomaticDeleteNoEffectCheckedJustBefore() throws InterruptedException {    	
		MockPhoto photo1 = new MockPhoto(new File("One.jpg"));
		MockPhoto photo2 = new MockPhoto(new File("Two.jpg"));
		library.addPhoto(photo1);
		library.addPhoto(photo2);
		Thread.sleep(SECONDS_TO_CHECK * 1000);
		Collection<IPhoto> photos = library.getPhotos();

		assertNotNull(photos);
		assertEquals(2 ,photos.size());
		assertTrue(photos.contains(photo1));
		assertTrue(photos.contains(photo2));

	}
}

