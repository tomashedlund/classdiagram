package gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class ClassDiagramEditor extends JFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6689124632172416629L;
	private JPanel display;
    private ListenOnMouse lom = new ListenOnMouse();
    private ListenOnMouseAssociation lomA = new ListenOnMouseAssociation();
    private JFileChooser chooseFileWindow;
    private String filename;
    private Set<ClassTable> classList = new HashSet<ClassTable>();
    private Set<DrawEdge> edgeList = new HashSet<DrawEdge>();
    private Set<AssociationLabel> labelList = new HashSet<AssociationLabel>();
    private boolean newButtonPressed = false, associationClicked = false;
    private AssociationObject from,to;
    private ListenOnEdge lod = new ListenOnEdge();
    private ListenOnMouseLabel lomL = new ListenOnMouseLabel();
    private ListenOnLabel lol = new ListenOnLabel();
    private MenuPanel mp = new MenuPanel(2,2);
    
    public ClassDiagramEditor(){
		super("Class Diagram");
		chooseFileWindow = new JFileChooser(".");
		
		JButton newButton = new JButton("New class");
		newButton.addActionListener(new ListenOnNewButton());
		mp.addButton(newButton);
		
		JButton newAssociationButton = new JButton("New association");
		newAssociationButton.addActionListener(new ListenOnNewAssociationButton());
		mp.addButton(newAssociationButton);
		
		JButton newLabelButton = new JButton("New label");
		newLabelButton.addActionListener(new ListenOnNewLabelButton());
		mp.addButton(newLabelButton);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ListenOnSaveButton());
		mp.addButton(saveButton);
		
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ListenOnLoadButton());
		mp.addButton(loadButton);
		
		JButton savePictureButton = new JButton("Save shot.png");
		savePictureButton.addActionListener(new ListenOnSavePictureButton());
		mp.addButton(savePictureButton);
		
//		setUndecorated(true);
//		setOpacity(0.8f);
		getContentPane().setBackground(Color.WHITE);
		
		display = new JPanel();
		display.setLayout(null);
//		display.setOpaque(false);
		display.setBackground(Color.WHITE);
		add(display, BorderLayout.CENTER);
		display.add(mp);
		
		ClassTable ct = new ClassTable(400,150);
		ct.setTitle("Introduction");
		ct.setDialog("1) To remove classes, press the headers non-text area and press backspace\n\n"
					+"2) To remove other object, right click on their non-text area\n\n"
					+"3) To add associations, press two labels or classes non-text area");
		ct.setWidth(258);
		ct.setHeight(200);
	    ct.addKeyListener(new Eraser());
	    ct.addMouseListener(lomA);
	    display.add(ct);
	    classList.add(ct);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000,600);
		setVisible(true);
    }
    public void saveAs(){
		chooseFileWindow.setSelectedFile(new File("Name.myclassdiagram"));
		int status = chooseFileWindow.showSaveDialog(ClassDiagramEditor.this);
		if (status != JFileChooser.APPROVE_OPTION) 
			return;
		filename = chooseFileWindow.getSelectedFile().getAbsolutePath();
		try{
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos =new ObjectOutputStream(fos);
			display.remove(mp);
			oos.writeObject(new SaveObjects(display, classList, edgeList, labelList));
			oos.close();
			display.add(mp);
		}catch (IOException ioe){ 
			System.err.println("Write error:" + ioe);
		}
	}
    private class ListenOnNewButton implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			if (!newButtonPressed) {
				display.addMouseListener(lom);
				display.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				newButtonPressed = true;
			}
		}
	}
    private class ListenOnNewLabelButton implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			if (!newButtonPressed) {
				display.addMouseListener(lomL);
				display.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				newButtonPressed = true;
			}
		}
	}
    private class ListenOnNewAssociationButton implements ActionListener {
    	public void actionPerformed(ActionEvent ave){
			if (classList.size()+labelList.size() >= 2) {
				associationClicked = true;
				display.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		}
    }
    private class ListenOnSaveButton implements ActionListener{
    	public void actionPerformed(ActionEvent ave){
    		saveAs();
    	}
    }
    private class ListenOnLoadButton implements ActionListener{
    	public void actionPerformed(ActionEvent ave){
    		int status = chooseFileWindow.showOpenDialog(ClassDiagramEditor.this);
    		if (status != JFileChooser.APPROVE_OPTION) 
    			return;
    		filename = chooseFileWindow.getSelectedFile().getAbsolutePath();
    		try{
    			FileInputStream fis = new FileInputStream(filename); 
    			ObjectInputStream ois = new ObjectInputStream(fis);
    			display.remove(mp);
    			remove(display);
    			SaveObjects load = (SaveObjects)ois.readObject();
    			display = load.getDisplay();
    			display.add(mp);
    			classList = load.getClassList();
    			edgeList = load.getEdgeList();
    			labelList = load.getLabelList();
    			add(display);
    			for (ClassTable ct : classList) {
    				ct.setListeners();
    				ct.addMouseListener(lomA);
    				ct.addKeyListener(new Eraser());
    			}
    			for (DrawEdge de : edgeList) {
    				de.addMouseListener(lod);
    			}
    			for (AssociationLabel al : labelList){
    				al.setListeners();
    				al.addKeyListener(new EraseLabel());
    			    al.addMouseListener(lol);
    			    al.addMouseListener(lomA);
    			}
    				
    			ois.close();
    		}catch (FileNotFoundException fnfe){ 
    			System.err.println("Hittar inte filen!");
    		}catch (ClassNotFoundException cnfe){ 
    			System.err.println("Read error:" + cnfe);
    		}catch (IOException ioe){ 
    			System.err.println("Read error:" + ioe);
    		}
    		validate();
    		repaint();
    	}
    }
    private class ListenOnSavePictureButton implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			display.remove(mp);
		    BufferedImage im = new BufferedImage(display.getWidth(), display.getHeight(), BufferedImage.TYPE_INT_ARGB);
		    display.paint(im.getGraphics());
		    try {
		    	
				ImageIO.write(im, "PNG", new File("shot.png"));
		    }catch (IOException ioe){ 
				System.err.println("Write error:" + ioe);
			}
		    display.add(mp);
		}
	}
    private class ListenOnMouseAssociation extends MouseAdapter{
    	public void mouseClicked(MouseEvent mev){
    		if (associationClicked) {
    			AssociationObject ct = (AssociationObject)mev.getSource();
				if(from == null) 
					from = ct;
				else if (!ct.equals(from))
					to = ct;
				if(from != null && to != null) {
					DrawEdge de = new DrawEdge(from,to);
					from.addEdge(de);
					to.addEdge(de);
					display.add(de);
					edgeList.add(de);
					de.addMouseListener(lod);
					repaint();
					from = null;
					to = null;
					associationClicked = false;
					display.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
    		}
    	}
    	public void mouseMoved(MouseEvent mev) {
    		
    	}
    }
    private class ListenOnEdge extends MouseAdapter{
		public void mouseClicked(MouseEvent mev){
			DrawEdge de = (DrawEdge)mev.getSource();
			if (SwingUtilities.isRightMouseButton(mev)){
				display.remove(de);
				de.getFrom().removeEdge(de);
				de.getTo().removeEdge(de);
				edgeList.remove(de);
				repaint();
			}
		}
	}
    private class ListenOnLabel extends MouseAdapter{
		public void mouseClicked(MouseEvent mev){
			AssociationLabel al = (AssociationLabel)mev.getSource();
			if (SwingUtilities.isRightMouseButton(mev)){
				for(DrawEdge de : al.getEdges()){
		    		if(de.getTo().equals(al))
		    			de.getFrom().removeEdge(de);
		    		else
		    			de.getTo().removeEdge(de);
					edgeList.remove(de);
		    		display.remove(de);
		    	}
		    	labelList.remove(al);
		    	display.remove(al);
			    display.validate();
			    display.repaint();
			}
		}
	}
	private class ListenOnMouse extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent mev){
		    int x = mev.getX();
		    int y = mev.getY();
		    ClassTable p = new ClassTable(x,y);
		    p.addKeyListener(new Eraser());
		    p.addMouseListener(lomA);
		    display.add(p);
		    classList.add(p);
		    display.validate();
		    display.repaint();
		    display.removeMouseListener(lom);
		    display.setCursor(Cursor.getDefaultCursor());
		    newButtonPressed = false;
		}
    }
	private class ListenOnMouseLabel extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent mev){
		    int x = mev.getX();
		    int y = mev.getY();
		    AssociationLabel al = new AssociationLabel(x,y);
		    al.addKeyListener(new EraseLabel());
		    al.addMouseListener(lol);
		    al.addMouseListener(lomA);
		    display.add(al);
		    labelList.add(al);
		    display.validate();
		    display.repaint();
		    display.removeMouseListener(lomL);
		    display.setCursor(Cursor.getDefaultCursor());
		    newButtonPressed = false;
		}
    }
    private class Eraser extends KeyAdapter{
		public void keyPressed(KeyEvent kev){
			ClassTable ct = (ClassTable)kev.getSource();
		    if (kev.getKeyCode() == KeyEvent.VK_BACK_SPACE){
		    	for(DrawEdge de : ct.getEdges()){
		    		if(de.getTo().equals(ct))
		    			de.getFrom().removeEdge(de);
		    		else
		    			de.getTo().removeEdge(de);
					edgeList.remove(de);
		    		display.remove(de);
		    	}
		    	classList.remove(ct);
		    	display.remove(ct);
		    }
		    display.validate();
		    display.repaint();
		}
    }
    private class EraseLabel extends KeyAdapter{
		public void keyPressed(KeyEvent kev){
			AssociationLabel al = (AssociationLabel)kev.getSource();
		    if (kev.getKeyCode() == KeyEvent.VK_BACK_SPACE){
		    	for(DrawEdge de : al.getEdges()){
		    		if(de.getTo().equals(al))
		    			de.getFrom().removeEdge(de);
		    		else
		    			de.getTo().removeEdge(de);
					edgeList.remove(de);
		    		display.remove(de);
		    	}
		    	labelList.remove(al);
		    	display.remove(al);
		    }
		    display.validate();
		    display.repaint();
		}
    }
}