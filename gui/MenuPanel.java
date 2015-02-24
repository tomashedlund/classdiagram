package gui;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.*;
public class MenuPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3057442193078236588L;
    private JPanel buttonArea = new JPanel();
    private boolean changeSize;
    private Set<DrawEdge> edgeList = new HashSet<DrawEdge>();

	public MenuPanel(int x, int y){
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Menu");
		title.setTitleJustification(TitledBorder.CENTER);
		setBorder(title);
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setBounds(x,y,150, 40);
		setLayout(new BorderLayout());
		add(buttonArea);
		buttonArea.setOpaque(false);
		ListenOnMove lom = new ListenOnMove(); 
		addMouseMotionListener(lom);
		addMouseListener(lom);
		buttonArea.setLayout(new FlowLayout());
    }
	private void resize() {
		if (getParent() != null) {
			((JComponent)getParent()).revalidate();
		}
	}
	public void addButton(JButton b) {
    	buttonArea.add(b);
    	b.setPreferredSize(new Dimension(130, 20));
    	setBounds(getX(),getY(),150, getHeight()+25);
    	validate();
    }
    private class ListenOnMove extends MouseAdapter{
		private int dx, dy;
		public void mousePressed(MouseEvent mev){
		    dx = mev.getX();
		    dy = mev.getY();
		    if (dx < getWidth() - 15 || dy < getHeight() - 15) {
		    	changeSize = false;
		    }
		    else {
		    	changeSize = true;
		    	setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		    }
		    requestFocus();
		}
		public void mouseMoved(MouseEvent mev) {
			if (mev.getX() < getWidth() - 15 || mev.getY() < getHeight() - 15) 
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			else
				setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		}
		public void mouseDragged(MouseEvent mev){
			int w = getWidth();
			int h = getHeight();
			int x = mev.getX();
			int y = mev.getY();
			if (!changeSize)
				setLocation(getX()+x-dx, getY()+y-dy);
			else {
				setBounds(getX(), getY(), w + x- dx, h + y - dy);
				setSize(new Dimension(w + x- dx, h + y - dy));
				dx = x;
				dy = y;
				resize();
	        }
			for (DrawEdge de : edgeList)
				de.update();
		}
		public void mouseReleased(MouseEvent mev){
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
    }
    public boolean contains(int x, int y){
		return x > -3 && x < getWidth() +3  && y > -3 && y < getHeight()+3;
	}
}
