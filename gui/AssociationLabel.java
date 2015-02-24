package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.*;

public class AssociationLabel extends JPanel implements AssociationObject,Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3057442193078236588L;
    private JTextField title;
    private boolean changeSize;
    private Set<DrawEdge> edgeList = new HashSet<DrawEdge>();

	@SuppressWarnings("serial")
	public AssociationLabel(int x, int y){
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setBounds(x,y,50,25);
		setLayout(new BorderLayout());
		setOpaque(false);
		title = new JTextField("Label") {
		    @Override public void setBorder(Border border) {
		        // No border!
		    }
		};
		title.setOpaque(false);
		add(title);
		ListenOnMove lom = new ListenOnMove(); 
		addMouseMotionListener(lom);
		addMouseListener(lom);
		ListenOnFocus listenOnFocus = new ListenOnFocus();
		addFocusListener(listenOnFocus);
		title.addFocusListener(listenOnFocus);
		title.getDocument().addDocumentListener(new TitleEditingListener());
    }
	
	public void setUneditable() {
		title.setEditable(false);
	}
	public void setListeners() {
		ListenOnMove lom = new ListenOnMove(); 
		addMouseMotionListener(lom);
		addMouseListener(lom);
		ListenOnFocus listenOnFocus = new ListenOnFocus();
		addFocusListener(listenOnFocus);
		title.addFocusListener(listenOnFocus);
		title.getDocument().addDocumentListener(new TitleEditingListener());
	}
	private void resize() {
		if (getParent() != null) {
			((JComponent)getParent()).revalidate();
		}
	}
	public int getOrigoX() {
    	return getX()+getWidth()/2;
    }
    public int getOrigoY() {
    	return getY()+getHeight()/2;
    }
    public void addEdge(DrawEdge de) {
    	edgeList.add(de);
    }
    public void removeEdge(DrawEdge de) {
    	edgeList.remove(de);
    }
    public Set<DrawEdge> getEdges() {
    	return edgeList;
    }
    private class ListenOnMove extends MouseAdapter{
		private int dx, dy;
		public void mousePressed(MouseEvent mev){
		    dx = mev.getX();
		    dy = mev.getY();
		    if (dx < getWidth() - getWidth()/3 || dy < getHeight() - getHeight()/3) {
		    	changeSize = false;
		    }
		    else {
		    	changeSize = true;
		    	setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		    }
		    requestFocus();
		}
		public void mouseMoved(MouseEvent mev) {
			if (mev.getX() < getWidth() - getWidth()/3 || mev.getY() < getHeight() - getHeight()/3) 
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
    private class ListenOnFocus implements FocusListener{
		public void focusGained(FocusEvent fev){
		    title.setBackground(Color.GRAY);
		    setBorder(BorderFactory.createLineBorder(Color.black));
		}
		public void focusLost(FocusEvent fev){
			while (title.getText().length() < 3)
    			title.setText(title.getText() + " ");
		    title.setBackground(Color.WHITE);
		    setBorder(null);
		    validate();
		}
    }
    private class TitleEditingListener implements DocumentListener{
    	public void insertUpdate(DocumentEvent de) {
    		validate();
    	}
    	public void removeUpdate(DocumentEvent de) {
    		validate();
    	}
    	public void changedUpdate(DocumentEvent de) {
    	}
    }
    public boolean contains(int x, int y){
		return x > -7 && x < getWidth() +7  && y > -7 && y < getHeight()+7;
	}

}