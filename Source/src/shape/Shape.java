package shape;

import java.awt.Graphics2D;

import light.Ray;
import util.Material;
import util.PVector;

public abstract class Shape {
	public Material material;
	
	public void show(Graphics2D g2D)
	  {}
	  
	public PVector normal(PVector point)
	  {return null;}

  	public PVector intersect(Ray ray)
	  {return null;}
}
