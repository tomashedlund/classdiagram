package gui;
import java.io.Serializable;
import java.util.*;

import javax.swing.JPanel;

class SaveObjects implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 152021376391330002L;
	private JPanel display;
	private Set<ClassTable> classList;
	private Set<DrawEdge> edgeList;
	private Set<AssociationLabel> labelList = new HashSet<AssociationLabel>();
	SaveObjects(JPanel display, Set<ClassTable> classList, Set<DrawEdge> edgeList, Set<AssociationLabel> labelList){
		this.display = display;
		this.classList = classList;
		this.edgeList = edgeList;
		this.labelList = labelList;
	}
	public JPanel getDisplay() {
		return this.display;
	}
	public Set<ClassTable> getClassList() {
		return this.classList;
	}
	public Set<DrawEdge> getEdgeList() {
		return this.edgeList;
	}
	public Set<AssociationLabel> getLabelList() {
		return this.labelList;
	}
}