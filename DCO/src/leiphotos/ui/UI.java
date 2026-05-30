package leiphotos.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;

import leiphotos.domain.facade.IAlbumsController;
import leiphotos.domain.facade.ILibrariesController;
import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.facade.IViewsController;
import leiphotos.domain.facade.ViewsType;


/**
 * UI is a window that allows arranging photos into
 * hierarchical albums and viewing the photos in each album.
 * 
 * @author rcm -- adapted by malopes
 * 
 */
public class UI extends JFrame  {
	
	private static final long serialVersionUID = 1L;

	private final DynamicTree treePanel;
	private final PreviewPane previewPane;

	private DefaultMutableTreeNode photos; 
	private DefaultMutableTreeNode albums;

	private DefaultMutableTreeNode allMain;
	private DefaultMutableTreeNode month12;
	private DefaultMutableTreeNode allTrash;

	private ILibrariesController libController;
	private IViewsController viewsController;
	private IAlbumsController albumController;

	// current view state – used by the search bar
	private ViewsType currentViewType = null;
	private boolean currentIsAlbum = false;

	private JTextField searchField;


	/**
	 * Make a UI window.
	 * 
	 * @param libController  the controller of the libraries
	 * @param viewsController  the controller of the views
	 * @param albumController  the controller of the albums
	 */
	public UI(ILibrariesController libController, 
			IViewsController viewsController, IAlbumsController albumController) {

		this.libController = libController;
		this.viewsController = viewsController;
		this.albumController = albumController;

		// set up the panel on the left with two subpanels in a vertical layout
		JPanel catalogPanel = new JPanel();
		catalogPanel.setLayout(new BoxLayout(catalogPanel,
				BoxLayout.PAGE_AXIS));

		// make the row of buttons 
		JPanel buttonPanel = makeButtonPanel();
		catalogPanel.add(buttonPanel);

		// make the album tree
		treePanel = new DynamicTree("LEIPhotos");
		populateTree(treePanel);
		catalogPanel.add(treePanel);


		// make the image previewer
		previewPane = new PreviewPane();

		// wrap preview pane with a search bar at the top
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.add(makeSearchPanel(), BorderLayout.NORTH);
		previewPanel.add(previewPane, BorderLayout.CENTER);

		// put the catalog tree and image previewer side by side, 
		// with an adjustable splitter between
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				catalogPanel, previewPanel);
		splitPane.setDividerLocation(450);
		this.add(splitPane);

		// give the whole window a good default size
		this.setTitle("LEI Photos");
		this.setSize(900,600);

		// end the program when the user presses the window's Close button
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAppIcon();
	}

	private void setAppIcon() {
        ImageIcon icon = new ImageIcon(String.join(File.separator,"resources","logo_ciencias.jpg"));
        Image image = icon.getImage();
        this.setIconImage(image);
    }


	/**
	 * Make the button panel for manipulating albums and photos.
	 * @return JPanel
	 */
	private JPanel makeButtonPanel() {
		JPanel panel = new JPanel();

		// Using a BoxLayout so that buttons will be horizontally aligned
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		JButton newAlbumButton = new JButton("New Album");
		newAlbumButton.addActionListener(e -> {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) getSelectedTreeNode();
			if (n.equals(albums) ){
				String newAlbumName = promptForAlbumName();
				if (newAlbumName == null) return;
				System.out.println("New Album " + newAlbumName);
				albumController.createAlbum(newAlbumName);
				treePanel.addObject(newAlbumName);
			}
		});
		panel.add(newAlbumButton);

		JButton deleteAlbumButton = new JButton("Delete Album");
		deleteAlbumButton.addActionListener(e -> {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) getSelectedTreeNode();
				if (n.getParent() != null && n.getParent().equals(albums) ){
					System.out.println("Delete Album " + getSelectedTreeNode());
					albumController.selectAlbum(getSelectedTreeNode().toString());
					albumController.removeAlbum();
					treePanel.removeCurrentNode();
				}
		});
		panel.add(deleteAlbumButton);

		JButton addPhotosButton = new JButton("Add Photos");
		addPhotosButton.addActionListener( e -> {
				// adds photos to an Album.
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) getSelectedTreeNode();
				if (n.getParent() != null && n.getParent().equals(albums) ){
					System.out.println("Add " + previewPane.getSelectedPhotos().size() + " photos to Album " + n.toString());
					albumController.selectAlbum(getSelectedTreeNode().toString());
					albumController.addPhotos(previewPane.getSelectedPhotos());
				}
		});
		panel.add(addPhotosButton);

		JButton removePhotosButton = new JButton("Remove Photos");
		removePhotosButton.addActionListener(e -> {
				// removes photos from an Album or 'all photos'.
				if (getSelectedTreeNode() instanceof DefaultMutableTreeNode n){
					if (n.getParent() != null && n.getParent().equals(albums) ){
						albumController.selectAlbum(n.toString());
						albumController.removePhotos(previewPane.getSelectedPhotos());
						System.out.println("Delete " + previewPane.getSelectedPhotos().size() + " photos from Album " + n.toString());
					}else if (n.equals(allMain)){
						libController.deletePhotos(previewPane.getSelectedPhotos());
						System.out.println("Delete " + previewPane.getSelectedPhotos().size() + " photos from main library");
						showLibraryView(ViewsType.ALL_MAIN);
					}
				}
		});
		panel.add(removePhotosButton);
		return panel;
	}


	/**
	 * Make the tree showing album names.
	 * @param tree DynamicTree
	 */
	private void populateTree(DynamicTree tree) {

		// creates the nodes: "Photos", "All Photos", "Last 12 Months", "Trash" and albums.
		String namePhotos = "Photos"; 
		String namealbums = "Albums";

		String nameAll = "All";
		String nameRecent = "Last 12 Months";
		String nameTrash = "Recently Deleted";

		photos = tree.addObject(null,namePhotos);
		albums = tree.addObject(null,namealbums);

		allMain = tree.addObject(photos, nameAll);
		month12 = tree.addObject(photos, nameRecent);
		allTrash = tree.addObject(photos, nameTrash);

		for (String albumName: albumController.getAlbumNames()){
			tree.addObject(albums, albumName);
		}

		tree.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// if left-double-click
				System.out.println("left mouse clicked");

				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					// Display photos of "All Photos", "Favs", "Trash" and an Album.
					if (getSelectedTreeNode().equals(allMain)){
						System.out.println("Show all photos in 'All Photos'");
						showLibraryView(ViewsType.ALL_MAIN);
					}
					else if(getSelectedTreeNode().equals(allTrash)){
						System.out.println("Show all photos in Trash");
						showLibraryView(ViewsType.ALL_TRASH);
					}
					else if(getSelectedTreeNode().equals(month12)){
						System.out.println("Show all last 12 months");
						showLibraryView(ViewsType.MOST_RECENT);
					}
					else if (getSelectedTreeNode() instanceof DefaultMutableTreeNode n &&
							n.getParent() != null && n.getParent().equals(albums)){
						System.out.println("Show all photos in Album " + n +"");
						albumController.selectAlbum(n.toString());
						showAlbumView();
					}
				}
			}
		});
	}

	/**
	 * Displays a library view, clears the search field, and tracks state.
	 */
	private void showLibraryView(ViewsType viewType) {
		currentViewType = viewType;
		currentIsAlbum = false;
		searchField.setText("");
		previewPane.display(viewsController.getPhotos(viewType));
	}

	/**
	 * Displays the currently selected album, clears the search field, and tracks state.
	 */
	private void showAlbumView() {
		currentViewType = null;
		currentIsAlbum = true;
		searchField.setText("");
		previewPane.display(albumController.getPhotos());
	}

	/**
	 * Builds the search bar panel placed above the preview pane.
	 */
	private JPanel makeSearchPanel() {
		JPanel panel = new JPanel(new BorderLayout(4, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		panel.add(new JLabel("Search: "), BorderLayout.WEST);

		searchField = new JTextField();
		searchField.setToolTipText("Type a string and press Enter or click Search");
		// Allow pressing Enter in the text field to trigger search
		searchField.addActionListener(e -> performSearch());
		panel.add(searchField, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel(new BorderLayout(2, 0));
		JButton searchBtn = new JButton("Search");
		searchBtn.addActionListener(e -> performSearch());
		JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(e -> {
			searchField.setText("");
			previewPane.setHighlightedPhotos(new HashSet<>());
		});
		btnPanel.add(searchBtn, BorderLayout.WEST);
		btnPanel.add(clearBtn, BorderLayout.EAST);
		panel.add(btnPanel, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Performs a search in the currently displayed view/album and highlights matches.
	 */
	private void performSearch() {
		String query = searchField.getText().trim();
		if (query.isEmpty()) {
			previewPane.setHighlightedPhotos(new HashSet<>());
			return;
		}
		List<IPhoto> matches;
		if (currentViewType != null) {
			matches = viewsController.getMatches(currentViewType, ".*"+query+".*");
			System.out.println("Search for '" + query + "' in view " + currentViewType );
		} else if (currentIsAlbum) {
			matches = albumController.getPhotos().stream()
					.filter(p -> p.matches(".*"+query+".*"))
					.toList();
			System.out.println("Search for '" + query + "' in current album");
		} else {
			return; // no view loaded yet
		}
		previewPane.setHighlightedPhotos(new HashSet<>(matches));
	}

	/**
	 * Return the album currently selected in the album tree.
	 * Returns null if no selection.
	 * @return Object
	 */
	private Object getSelectedTreeNode() {
		return treePanel.getLastSelectedPathComponent();
	}

	/**
	 * Pop up a dialog box prompting the user for a name for a new album.
	 * Returns the name, or null if the user pressed Cancel.
	 * @return String
	 */
	private String promptForAlbumName() {
		return (String)
				JOptionPane.showInputDialog(
						treePanel, 
						"Album Name: ", 
						"Add Album",
						JOptionPane.PLAIN_MESSAGE, 
						null, 
						null, 
						"");		
	}
}
