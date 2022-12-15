package shape;

import java.awt.Graphics2D;

import org.kabeja.dxf.DXFLine;

import light.Ray;
import util.*;

public class Line extends Shape{
	
	PVector a,b;
	
	public Line(PVector a,PVector b,Material mat)
	  {
	    this.a=a;
	    this.b=b;
	    super.material=mat;
	  }
	public Line(DXFLine line,Material mat)  //konstruktor konewrtujący z DXFLine
	  {
	    this.a=new PVector(line.getStartPoint().getX(),line.getStartPoint().getY());
	    this.b=new PVector(line.getEndPoint().getX(),line.getEndPoint().getY());
	    super.material=mat;
	  }
	public Line(){}
	
	@Override
	public void show(Graphics2D g2D)
	{
		g2D.setColor(material.d_color);
		g2D.drawLine((int) (a.x*1000),(int) (a.y*1000),(int) (b.x*1000),(int) (b.y*1000));
//		System.out.print(a.x);
//		System.out.print(" ");
//		System.out.println(a.y);
	}
	
	//normalna linii
	@Override
  public PVector normal(PVector point)
  {
    PVector line_dir=new PVector(this.b.x-this.a.x,this.b.y-this.a.y);  //kierunek linii
    line_dir.normalize();
    return line_dir.rotate(Math.PI/2);
  }
  
  //funkcja przecięcia linii z prominiem. Jest także wykożystywana w łuku i bezierze
  //zaczerpnięta od Daniela Shiffmana ("2D raytracing")
	@Override
  public PVector intersect(Ray ray)
  {
    final double x4 = ray.pos.x + ray.dir.x;
    final double y4 = ray.pos.y + ray.dir.y;

      final double den = (a.x - b.x) * (ray.pos.y - y4) - (a.y - b.y) * (ray.pos.x - x4);
      if (den == 0) {
        return null;
      }

      //sprawdzanie czy punkt przecięcia istnieje
      final double t = ((a.x - ray.pos.x) * (ray.pos.y - y4) - (a.y - ray.pos.y) * (ray.pos.x - x4)) / den;
      final double u = -((a.x - b.x) * (a.y - ray.pos.y) - (a.y - b.y) * (a.x - ray.pos.x)) / den;

      //znajdowanie go
      if (t > 0 && t < 1 && u > 0) {
        PVector pt = new PVector(0,0);
        pt.x = a.x + t * (b.x - a.x);
        pt.y = a.y + t * (b.y - a.y);
        
        if((pt.x!=this.a.x || pt.y!=this.a.y) && (pt.x!=this.b.x || pt.y!=this.b.y))
        {
    		//System.out.println(this.material.name);
    		return pt;
        }
      } 
      
      return null;
  }
	  
}
