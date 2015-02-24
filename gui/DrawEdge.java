package gui;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class DrawEdge extends JPanel implements Serializable{
	/**
	 * Autogenerated serialVerisionUID
	 */
	private static final long serialVersionUID = 4652664078087285326L;
	private static final int LENGTH = 2;
	int origoX,width,origoY,height;
	boolean growingY;
	private AssociationObject from,to;
	
	int x1North,x2North,y1North,y2North,x1South,x2South,y1South,y2South,x1West,x2West,y1West,y2West,x1East,x2East,y1East,y2East;
	double x1,x2,y1,y2;
	public DrawEdge(AssociationObject from, AssociationObject to) {
		this.from = from;
		this.to = to;
		
		calculateLine(from,to);
		
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		repaint();
	}
	private void calculateLine(AssociationObject from, AssociationObject to) {
		int x1 = from.getOrigoX();
		int y1 = from.getOrigoY();
		int x2 = to.getOrigoX();
		int y2 = to.getOrigoY();
		if (x1 < x2) {
			this.origoX = x1;
			this.width = x2-x1;
		}
		else {
			this.origoX = x2;
			this.width = x1-x2;
		}
		if (y1 < y2) {
			this.origoY = y1;
			this.height = y2-y1;
		}
		else {
			this.origoY = y2;
			this.height = y1-y2;
		}
		if((x1 < x2 && y1 < y2) || (x1 > x2 && y1 > y2))
			this.growingY = true;
		else
			this.growingY = false;
		setBounds(origoX-LENGTH,origoY-LENGTH,width+LENGTH*2,height+LENGTH*2);
	}
	public AssociationObject getFrom() {
		return this.from;
	}
	public AssociationObject getTo() {
		return this.to;
	}
	public void update() {
		calculateLine(from,to);
		repaint();
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		if(growingY) {
			g.drawLine(LENGTH, LENGTH, getWidth()-LENGTH, getHeight()-LENGTH);
		}
		else
			g.drawLine(LENGTH, getHeight()-LENGTH, getWidth()-LENGTH, LENGTH);
	}
	@Override
	public boolean contains(int x, int y){
		// Uses the funtion y = kx + m, and therafter checks if mouse is within this +-LENGTH pixels
		// Observe, if the x2-x1 is 0 then k will go -> infinity, therefor catch this case with if.
		if ((getWidth()-LENGTH)-LENGTH == 0){
			if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight())
				return true;
			else
				return false;
		}
		double k = ((double)(getHeight()-LENGTH)-LENGTH)/((getWidth()-LENGTH)-LENGTH);
		double m = LENGTH-LENGTH*k;
		if (!growingY) {
			k = (LENGTH-(double)(getHeight()-LENGTH))/((getWidth()-LENGTH)-LENGTH);
			m = (getHeight()-LENGTH)-LENGTH*k;
		}
		
		if (!(x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()))
			return false;
		
		boolean northBoundary, southBoundary, westBoundary, eastBoundary;
		if(!growingY) {
			northBoundary = (y+LENGTH <= k*(x-LENGTH)+m && y+LENGTH >= k*(x+LENGTH)+m);
			southBoundary = (y-LENGTH <= k*(x-LENGTH)+m && y-LENGTH >= k*(x+LENGTH)+m);
		}
		else {
			northBoundary = (y+LENGTH >= k*(x-LENGTH)+m && y+LENGTH <= k*(x+LENGTH)+m);
			southBoundary = (y-LENGTH >= k*(x-LENGTH)+m && y-LENGTH <= k*(x+LENGTH)+m);
		}
		westBoundary = (y-LENGTH <= k*(x-LENGTH)+m && y+LENGTH >= k*(x-LENGTH)+m);
		eastBoundary = (y-LENGTH <= k*(x+LENGTH)+m && y+LENGTH >= k*(x+LENGTH)+m);
		
		return northBoundary || southBoundary || westBoundary || eastBoundary;
	}
}