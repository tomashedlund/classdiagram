package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.*;

public class ClassTable extends JPanel implements AssociationObject,Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3057442193078236588L;
	private JPanel header = new JPanel();
    private JTextArea textArea = new JTextArea();
    private JTextField title;
    private boolean changeSize;
    private Set<DrawEdge> edgeList = new HashSet<DrawEdge>();

	@SuppressWarnings("serial")
	public ClassTable(int x, int y){
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setBounds(x,y,200,150);
		header.setPreferredSize(new Dimension(200, 30));
		setLayout(new BorderLayout());
		add(header, BorderLayout.NORTH);
		header.setBackground(new Color(255,255,255,255));
		header.setBorder(BorderFactory.createLineBorder(Color.black));
		title = new JTextField("New empty class") {
		    @Override public void setBorder(Border border) {
		        // No border!
		    }
		};
		title.setBackground(Color.WHITE);
		header.add(title);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		textArea.setBackground(Color.WHITE);
		ListenOnMove lom = new ListenOnMove(); 
		addMouseMotionListener(lom);
		addMouseListener(lom);
		ListenOnFocus listenOnFocus = new ListenOnFocus();
		addFocusListener(listenOnFocus);
		title.addFocusListener(listenOnFocus);
		title.getDocument().addDocumentListener(new TitleEditingListener());
		textArea.addFocusListener(new ListenOnFocus());
		textArea.setLineWrap(true);
    }
	
	public void setUneditable() {
		textArea.setEditable(false);
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
		textArea.addFocusListener(new ListenOnFocus());
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
    public void setTitle(String title) {
    	this.title.setText(title);
    	this.title.validate();
    }
    public void setDialog(String text) {
    	this.textArea.setText(text);
    }
    public void setHeight(int height) {
    	setBounds(getX(), getY(), getWidth(), height);
    }
    public void setWidth(int width) {
    	setBounds(getX(), getY(), width, getHeight());
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
    private class ListenOnFocus implements FocusListener{
		public void focusGained(FocusEvent fev){
		    header.setBackground(Color.GRAY);
		    title.setBackground(Color.GRAY);
		}
		public void focusLost(FocusEvent fev){
			while (title.getText().length() < 3)
    			title.setText(title.getText() + " ");
		    header.setBackground(Color.WHITE);
		    title.setBackground(Color.WHITE);
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
		return x > -3 && x < getWidth() +3  && y > -3 && y < getHeight()+3;
	}

}
