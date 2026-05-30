package leiphotos.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import leiphotos.domain.facade.IAlbumsController;
import leiphotos.domain.facade.ILibrariesController;
import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.facade.IViewsController;
import leiphotos.domain.facade.LEIPhotos;
import leiphotos.domain.facade.ViewsType;

/**
 * End-to-end tests that exercise the application through the 
 * three controllers, mirroring the scenarios demonstrated 
 * in SimpleClient.
 */
class SimpleClientE2ETest {

    private ILibrariesController plc;
    private IViewsController vlc;
    private IAlbumsController alc;
    private List<IPhoto> photos;

    /**
     * Each test gets a fresh application instance with 14 photos loaded.
     *
     * LoaderPhotos imports 15 paths but the last one does not exist,
     * so exactly 14 photos are added to the main library.
     */
    @BeforeEach
    void setUp() {
        LEIPhotos app = new LEIPhotos();
        plc = app.libsController();
        vlc = app.viewsController();
        alc = app.albumsController();
        photos = LoaderPhotos.load(plc);
    }

    // -------------------------------------------------------------------------
    // Loading / initial state
    // -------------------------------------------------------------------------

    @Test
    void testLoad_allValidPhotosAppearInMainView() {
        assertEquals(14, vlc.getPhotos(ViewsType.ALL_MAIN).size());
    }

    @Test
    void testLoad_trashIsInitiallyEmpty() {
        assertTrue(vlc.getPhotos(ViewsType.ALL_TRASH).isEmpty());
    }

    @Test
    void testLoad_favouritesInitiallyEmpty() {
        assertTrue(vlc.getPhotos(ViewsType.FAVOURITES).isEmpty());
    }

    // -------------------------------------------------------------------------
    // toggleFavourite
    // -------------------------------------------------------------------------

    @Test
    void testToggleFavouritePhotosAppearInFavouritesView() {
        plc.toggleFavourite(new HashSet<>(List.of(photos.get(0), photos.get(1), photos.get(2))));

        List<IPhoto> favs = vlc.getPhotos(ViewsType.FAVOURITES);
        assertEquals(3, favs.size());
        assertTrue(favs.contains(photos.get(0)));
        assertTrue(favs.contains(photos.get(1)));
        assertTrue(favs.contains(photos.get(2)));
    }

    @Test
    void testToggleFavouriteSecondToggleRemovesFromFavourites() {
        Set<IPhoto> set = Set.of(photos.get(0));
        plc.toggleFavourite(set);
        assertTrue(vlc.getPhotos(ViewsType.FAVOURITES).contains(photos.get(0)));

        plc.toggleFavourite(set);
        assertFalse(vlc.getPhotos(ViewsType.FAVOURITES).contains(photos.get(0)));
    }

    @Test
    void testToggleFavouriteDoesNotAffectMainLibrarySize() {
        int before = vlc.getPhotos(ViewsType.ALL_MAIN).size();
        plc.toggleFavourite(new HashSet<>(List.of(photos.get(0), photos.get(1))));
        assertEquals(before, vlc.getPhotos(ViewsType.ALL_MAIN).size());
    }

    // -------------------------------------------------------------------------
    // deletePhotos
    // -------------------------------------------------------------------------

    @Test
    void testDeletePhotosMovesPhotosFromMainToTrash() {
        int initialMain = vlc.getPhotos(ViewsType.ALL_MAIN).size();
        IPhoto p0 = photos.get(0);
        IPhoto p6 = photos.get(6);

        plc.deletePhotos(new HashSet<>(List.of(p0, p6)));

        List<IPhoto> main  = vlc.getPhotos(ViewsType.ALL_MAIN);
        List<IPhoto> trash = vlc.getPhotos(ViewsType.ALL_TRASH);

        assertEquals(initialMain - 2, main.size());
        assertFalse(main.contains(p0));
        assertFalse(main.contains(p6));
        assertTrue(trash.contains(p0));
        assertTrue(trash.contains(p6));
    }

    @Test
    void testDeleteFavouritePhotoRemovedFromFavouritesView() {
        IPhoto p1 = photos.get(1);
        plc.toggleFavourite(Set.of(p1));
        assertTrue(vlc.getPhotos(ViewsType.FAVOURITES).contains(p1));

        plc.deletePhotos(Set.of(p1));
        assertFalse(vlc.getPhotos(ViewsType.FAVOURITES).contains(p1));
    }

    // -------------------------------------------------------------------------
    // emptyTrash
    // -------------------------------------------------------------------------

    @Test
    void testEmptyTrashTrashBecomesEmpty() {
        plc.deletePhotos(new HashSet<>(List.of(photos.get(0), photos.get(6), photos.get(12))));
        assertFalse(vlc.getPhotos(ViewsType.ALL_TRASH).isEmpty());

        plc.emptyTrash();
        assertTrue(vlc.getPhotos(ViewsType.ALL_TRASH).isEmpty());
    }

    @Test
    void testEmptyTrashDeletedPhotosAreGonePermanently() {
        IPhoto p0 = photos.get(0);
        plc.deletePhotos(Set.of(p0));
        plc.emptyTrash();

        assertFalse(vlc.getPhotos(ViewsType.ALL_MAIN).contains(p0));
        assertFalse(vlc.getPhotos(ViewsType.ALL_TRASH).contains(p0));
    }

    @Test
    void testEmptyTrashOnEmptyTrashNoEffect() {
        plc.emptyTrash(); 
        assertTrue(vlc.getPhotos(ViewsType.ALL_TRASH).isEmpty());
    }

    // -------------------------------------------------------------------------
    // setSortingCriteria / getPhotos ordering
    // -------------------------------------------------------------------------

    @Test
    void testSetSortingCriteriaMainViewSortedByTitle() {
        Comparator<IPhoto> byTitle = Comparator.comparing(IPhoto::title);
        vlc.setSortingCriteria(ViewsType.ALL_MAIN, byTitle);

        List<String> titles = vlc.getPhotos(ViewsType.ALL_MAIN)
                .stream().map(IPhoto::title).toList();
        List<String> sortedTitles = titles.stream().sorted().toList();

        assertEquals(sortedTitles, titles);
    }

    // -------------------------------------------------------------------------
    // getMatches (search)
    // -------------------------------------------------------------------------

    @Test
    void tesSearchPhotosReturnsMatchesForKnownRegex() {
        // "Octopus Vasconcelos" title and files AnelJVasconcelos / SapatoJVasconcelos
        // match this regex – three results expected in the ordering of the 
        // initial sorting criteria (file size ascending)
        List<IPhoto> matches = vlc.getMatches(ViewsType.ALL_MAIN, ".*Vasconcelos.*");
        assertEquals(3, matches.size());
        assertEquals("SapatoJVasconcelos.JPG", matches.get(0).file().getName());
        assertEquals("AnelJVasconcelos.jpeg", matches.get(1).file().getName());
        assertEquals("Octopus Vasconcelos", matches.get(2).title());
    }


    @Test
    void testSearchPhotosNoMatchReturnsEmptyList() {
        List<IPhoto> matches = vlc.getMatches(ViewsType.ALL_MAIN, ".*NONEXISTENT_XYZ_999.*");
        assertTrue(matches.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Albums – create / remove / select
    // -------------------------------------------------------------------------

    @Test
    void testCreateAlbumAlbumAppearsInAlbumNames() {
        assertTrue(alc.createAlbum("Oppenheimer"));
        assertTrue(alc.getAlbumNames().contains("Oppenheimer"));
    }

    @Test
    void testCreateDuplicateAlbumReturnsFalseAndNotDuplicated() {
        alc.createAlbum("Monuments");
        boolean result = alc.createAlbum("Monuments");
        assertFalse(result);
        assertEquals(1L, alc.getAlbumNames().stream()
                .filter("Monuments"::equals).count());
    }

    @Test
    void testRemoveAlbumAlbumNoLongerExists() {
        alc.createAlbum("Monuments");
        alc.selectAlbum("Monuments");
        alc.removeAlbum();
        assertFalse(alc.getAlbumNames().contains("Monuments"));
    }

    @Test
    void testSelectAlbumSelectedAlbumReturnsName() {
        alc.createAlbum("Oppenheimer");
        alc.selectAlbum("Oppenheimer");
        assertTrue(alc.getSelectedAlbum().isPresent());
        assertEquals("Oppenheimer", alc.getSelectedAlbum().get());
    }

    // -------------------------------------------------------------------------
    // Albums – add / remove photos
    // -------------------------------------------------------------------------

    @Test
    void testAddPhotosToAlbumAlbumContainsAddedPhotos() {
        alc.createAlbum("Oppenheimer");
        alc.selectAlbum("Oppenheimer");
        Set<IPhoto> toAdd = new HashSet<>(List.of(photos.get(3), photos.get(4),
                photos.get(7), photos.get(8)));
        alc.addPhotos(toAdd);

        List<IPhoto> albumPhotos = alc.getPhotos();
        assertEquals(4, albumPhotos.size());
        assertTrue(albumPhotos.containsAll(toAdd));
    }

    @Test
    void testAddPhotosToAlbumMainLibraryUnchanged() {
        int before = vlc.getPhotos(ViewsType.ALL_MAIN).size();
        alc.createAlbum("Oppenheimer");
        alc.selectAlbum("Oppenheimer");
        alc.addPhotos(Set.of(photos.get(3), photos.get(4)));
        assertEquals(before, vlc.getPhotos(ViewsType.ALL_MAIN).size());
    }

    @Test
    void testAddPhotosWithNoAlbumSelectedNoEffect() {
        // No album selected – addPhotos should be a no-op
        alc.addPhotos(Set.of(photos.get(0))); // must not throw
    }

    // -------------------------------------------------------------------------
    // Interaction between library operations and albums
    // -------------------------------------------------------------------------

    @Test
    void testDeletePhotoThatIsInAlbumPhotoRemovedFromAlbum() {
        alc.createAlbum("Oppenheimer");
        alc.selectAlbum("Oppenheimer");
        IPhoto p7 = photos.get(7);
        IPhoto p8 = photos.get(8);
        alc.addPhotos(new HashSet<>(List.of(p7, p8)));

        plc.deletePhotos(Set.of(p7));

        alc.selectAlbum("Oppenheimer");
        List<IPhoto> albumPhotos = alc.getPhotos();
        assertFalse(albumPhotos.contains(p7));
        assertTrue(albumPhotos.contains(p8));
    }

    // -------------------------------------------------------------------------
    // Full-flow integration tests
    // -------------------------------------------------------------------------

    @Test
    void testFullFlow1() {
        // 1. Initial state
        assertEquals(14, vlc.getPhotos(ViewsType.ALL_MAIN).size());
        assertTrue(vlc.getPhotos(ViewsType.ALL_TRASH).isEmpty());

        // 2. Mark some photos as favourites
        plc.toggleFavourite(new HashSet<>(List.of(photos.get(0), photos.get(1), photos.get(2))));
        assertEquals(3, vlc.getPhotos(ViewsType.FAVOURITES).size());

        // 3. Delete some photos
        plc.deletePhotos(new HashSet<>(List.of(photos.get(0), photos.get(6), photos.get(12))));
        assertEquals(11, vlc.getPhotos(ViewsType.ALL_MAIN).size());
        assertEquals(3,  vlc.getPhotos(ViewsType.ALL_TRASH).size());
        // deleted favourite no longer in favourites view
        assertFalse(vlc.getPhotos(ViewsType.FAVOURITES).contains(photos.get(0)));

        // 4. Empty trash
        plc.emptyTrash();
        assertTrue(vlc.getPhotos(ViewsType.ALL_TRASH).isEmpty());

        // 5. Change sorting to title
        vlc.setSortingCriteria(ViewsType.ALL_MAIN, Comparator.comparing(IPhoto::title));

        // 6. Mark more photos as favourites (photos.get(1) toggled off, 9 and 10 toggled on)
        plc.toggleFavourite(new HashSet<>(List.of(photos.get(1), photos.get(9), photos.get(10))));
        List<IPhoto> favs = vlc.getPhotos(ViewsType.FAVOURITES);
        assertFalse(favs.contains(photos.get(1)));   // toggled off
        assertTrue(favs.contains(photos.get(2)));    // still on from step 2
        assertTrue(favs.contains(photos.get(9)));    // newly on
        assertTrue(favs.contains(photos.get(10)));   // newly on

        // 7. Search photos
        List<IPhoto> vasconcelos = vlc.getMatches(ViewsType.ALL_MAIN, ".*Vasconcelos.*");
        assertEquals(2, vasconcelos.size());

        // 8. Create albums (Monuments is duplicated → second call returns false)
        assertTrue(alc.createAlbum("Oppenheimer"));
        assertTrue(alc.createAlbum("Monuments"));
        assertTrue(alc.createAlbum("JoanaVasconcelos"));
        assertFalse(alc.createAlbum("Monuments"));
        assertEquals(3, alc.getAlbumNames().size());

        // 9. Add photos to albums
        alc.selectAlbum("Oppenheimer");
        alc.addPhotos(new HashSet<>(List.of(photos.get(3), photos.get(4),
                photos.get(7), photos.get(8))));
        assertEquals(4, alc.getPhotos().size());

        alc.selectAlbum("JoanaVasconcelos");
        alc.addPhotos(new HashSet<>(vasconcelos));
        assertEquals(vasconcelos.size(), alc.getPhotos().size());

        // 10. Remove album
        alc.selectAlbum("Monuments");
        alc.removeAlbum();
        assertFalse(alc.getAlbumNames().contains("Monuments"));
        assertEquals(2, alc.getAlbumNames().size());

        // 11. Delete photo that is in an album
        plc.deletePhotos(Set.of(photos.get(7)));
        alc.selectAlbum("Oppenheimer");
        List<IPhoto> oppPhotos = alc.getPhotos();
        assertFalse(oppPhotos.contains(photos.get(7)));
        assertEquals(3, oppPhotos.size());
    }
}
