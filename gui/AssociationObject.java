package gui;
import java.util.Set;

public interface AssociationObject {
	public int getOrigoX();
    public int getOrigoY();
    public void addEdge(DrawEdge de);
    public void removeEdge(DrawEdge de);
    public Set<DrawEdge> getEdges();

}
