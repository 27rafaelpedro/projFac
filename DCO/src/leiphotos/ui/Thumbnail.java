package leiphotos.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import leiphotos.domain.facade.IPhoto;

/**
 * Thumbnail represents a small thumbnail image of a photo file.
 * A thumbnail is selectable by the mouse, and draws itself with a highlight 
 * when it's selected.
 * @author jbaek, rcm
 *
 * @version $Revision: 1.0 $
 */
public class Thumbnail extends JComponent {		

	private static final long serialVersionUID = 1L;

	/**
	 * Maximum dimension of a thumbnail image.  A thumbnail
	 * must fit in a square bounding box that is 
	 * THUMBNAIL_SIZE pixels on a side. 
	 */
	public static final int THUMBNAIL_SIZE = 150;
	
	// the photo on disk
	private IPhoto photo;
	
	// the loaded, displayable thumbnail image
	private BufferedImage bufferedImage;
	
	// width & height in pixels of the thumbnail image (not the
	// original photo!)
	private int width;
	private int height;
	
	// true iff this thumbnail is selected
	private boolean isSelected;

	// true iff this thumbnail matches the current search
	private boolean isHighlighted;
	
	/**
	 * Make a thumbnail for a photo.
	 * @param p Photo
	 */
	public Thumbnail(IPhoto p) {
		photo = p;
		
		ImageIcon icon = new ImageIcon(p.file().getAbsolutePath());
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		if (w < THUMBNAIL_SIZE && h < THUMBNAIL_SIZE) {
			// image smaller than the box
			width = w; 
			height = h;
		} else if (w > h) {
			width = THUMBNAIL_SIZE;
			height = THUMBNAIL_SIZE * h / w;
		} else {
			height = THUMBNAIL_SIZE;
			width = THUMBNAIL_SIZE * w / h;
		}
		
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage
		     .createGraphics()
		     .drawImage(icon.getImage(), 0, 0, width, height, null);
		
		setPreferredSize(new Dimension(width, height));

		isSelected = false;
		isHighlighted = false;
		
		addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				toggle();
			}
		});
		
	}

	/**
	 * @return the photo for this thumbnail */
	public IPhoto getPhoto() {
		return photo;
	}
	
	/**
	 * @return true iff this thumbnail was selected by the user */
	public boolean isSelected() { 
		return isSelected;
	}
	
	/**
	 * Toggles the selection state of this thumbnail.
	 */
	public void toggle() { 
		isSelected = !isSelected; 
		repaint();
	}

	/**
	 * Sets whether this thumbnail is highlighted as a search match.
	 * @param highlighted true to highlight, false to clear highlight
	 */
	public void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
		repaint();
	}
	
	/**
	 * Method paintComponent.
	 * @param g Graphics
	 */
	@Override
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D)g;
		
		// put the origin at the center of our bounds
		Dimension d = getSize();
		g2.translate(d.width/2, d.height/2);
		
		// draw the image at (-w/2, -h/2) relative to the center point
		int x = -width/2;
		int y = -height/2;
		g2.drawImage(bufferedImage, x, y, this);

		// draw selection highlight over the image
		if (isSelected()) {
			g2.setColor(new Color(0.0f /*red*/, 0.0f /*green*/, 1.0f /*blue*/, 0.5f /*alpha (transparency)*/));
			g2.fillRect(x, y, width, height);
		}

		// draw search-match highlight as a yellow border inside the image
		if (isHighlighted) {
			int borderThickness = 4;
			g2.setColor(new Color(1.0f, 0.85f, 0.0f)); // gold/yellow
			g2.setStroke(new BasicStroke(borderThickness));
			g2.drawRect(x + borderThickness / 2, y + borderThickness / 2,
					width - borderThickness, height - borderThickness);
		}
	}
}