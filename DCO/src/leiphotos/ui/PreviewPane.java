package leiphotos.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import leiphotos.domain.facade.IPhoto;

/**
 * PreviewPane is a panel that displays a set of photographs as
 * small thumbnails and allows the user to select a subset of them.
 * 
 * @author jbaek, rcm changed by malopes
 * @version $Revision: 1.0 $
 */
public class PreviewPane extends JScrollPane {

	private static final long serialVersionUID = 1L;

	// content panel
	private JPanel content;

	// list of thumbnails currently displayed
	private List<Thumbnail> thumbnails;	

	/**
	 * Make a PreviewPane.
	 */
	public PreviewPane() {
		content = new ScrollableFlowPanel();
		thumbnails = new ArrayList<Thumbnail>();
		setViewportView(content);
	}

	/**
	 * Clears the preview pane, so that it displays no photos.
	 */
	public void clear() {
		thumbnails.clear();
		for (Component c : content.getComponents()) {
			content.remove(c);
		}
	}

	/**
	 * Displays a set of photos, completely replacing the 
	 * previously-displayed set of photos.
	 * @param photos the set of photos to display
	 */
	public void display(Iterable<IPhoto> photos) {
		clear();

		// force the scroll pane to lay out again
		invalidate();
		validate();
		doLayout();
		repaint();


		// generate a thumbnail for each photo
		for (IPhoto p: photos) {
			Thumbnail t = new Thumbnail(p);
			content.add(t);
			thumbnails.add(t);
		}

		verticalScrollBar.doLayout();

		// force the scroll pane to lay out again
		invalidate();
		validate();
		doLayout();
		repaint();
		SwingUtilities.updateComponentTreeUI(content);
	}

	/**
	 * @return the highlighted photos, i.e. the subset of displayed photos 
	 * that the user selected.  */
	public Set<IPhoto> getSelectedPhotos() {
		Set<IPhoto> result = new HashSet<>();
		for (Thumbnail t: thumbnails) {
			if (t.isSelected()) { // if selected
				result.add(t.getPhoto()); // add to the list of returned ones
			}
		}
		return result;
	}

	/**
	 * Highlights the thumbnails whose photos are in the given set.
	 * All other thumbnails are unhighlighted.
	 * @param matchingPhotos the set of photos to highlight as search matches
	 */
	public void setHighlightedPhotos(Set<IPhoto> matchingPhotos) {
		for (Thumbnail t : thumbnails) {
			t.setHighlighted(matchingPhotos.contains(t.getPhoto()));
		}
	}


	/**
	 * ScrollableFlowPanel is a panel that flows its components left-to-right 
	 * and wraps them onto multiple lines.  Designed to be placed in a JScrollPane.
	 * Code from http://forums.sun.com/thread.jspa?forumID=57&threadID=701797&start=2
	 * @version $Revision: 1.0 $
	 */
	private static class ScrollableFlowPanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1L;

		public ScrollableFlowPanel() {
			setLayout(new FlowLayout(FlowLayout.LEADING));
		}

		/**
		 * Method setBounds.
		 * @param x int
		 * @param y int
		 * @param width int
		 * @param height int
		 */
		@Override
		public void setBounds( int x, int y, int width, int height ) {
			super.setBounds( x, y, getParent().getWidth(), height );
		}

		/**
		 * Method getPreferredSize.
		 * @return Dimension
		 */
		@Override
		public Dimension getPreferredSize() {
			return new Dimension( getWidth(), getPreferredHeight() );
		}

		//@Override
		/**
		 * Method getPreferredScrollableViewportSize.
		 * @return Dimension
		 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
		 */
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return super.getPreferredSize();
		}

		/**
		 * Method getScrollableUnitIncrement.
		 * @param visibleRect Rectangle
		 * @param orientation int
		 * @param direction int
		 * @return int
		 * @see javax.swing.Scrollable#getScrollableUnitIncrement(Rectangle, int, int)
		 */
		@Override
		public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
			int hundredth = ( orientation ==  SwingConstants.VERTICAL
					? getParent().getHeight() : getParent().getWidth() ) / 100;
			return ( hundredth == 0 ? 1 : hundredth ); 
		}

		
		/**
		 * Method getScrollableBlockIncrement.
		 * @param visibleRect Rectangle
		 * @param orientation int
		 * @param direction int
		 * @return int
		 * @see javax.swing.Scrollable#getScrollableBlockIncrement(Rectangle, int, int)
		 */
		@Override
		public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
			return orientation == SwingConstants.VERTICAL ? getParent().getHeight() : getParent().getWidth();
		}


		/**
		 * Method getScrollableTracksViewportWidth.
		 * @return boolean
		 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
		 */
		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		/**
		 * Method getScrollableTracksViewportHeight.
		 * @return boolean
		 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
		 */
		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		/*
		 * Compute the preferred height of the panel, given the
		 * width of the scroll panel.
		 * @return the preferred height of the panel, given the width of the scroll panel
		 */
		private int getPreferredHeight() {
			int rv = 0;
			for ( int k = 0, count = getComponentCount(); k < count; k++ ) {
				Component comp = getComponent( k );
				Rectangle r = comp.getBounds();
				int height = r.y + r.height;
				if ( height > rv )
					rv = height;
			}
			rv += ((FlowLayout) getLayout()).getVgap();
			return rv;
		}
	}
}