package shape;

import java.awt.Graphics2D;
import java.util.ArrayList;

import light.Ray;
import util.Material;
import util.PVector;

public class Bezier extends Shape
{
	final double PRECISION=0.005;	//200 lines
	ArrayList<Line> hitbox=new ArrayList<Line>();  //linie służące do wykrywania kolizji

	PVector points[]=new PVector[4];
	
	public Bezier(PVector p[], Material mat)
	{
		super.material=mat;
		this.points=p;
//		this.points[0]=PVector.mult(p[0];
//		this.points[1]=PVector.mult(p[1];
//		this.points[2]=PVector.mult(p[2];
//		this.points[3]=PVector.mult(p[3],1000);
		
		double x1,x2,y1,y2;
		x1 = points[0].x;
		y1 = points[0].y;
		for(double t=0;t<=1+PRECISION;t+=PRECISION)
		{
			//use Berstein polynomials
			x2=(points[0].x+t*(-points[0].x*3+t*(3*points[0].x-
			points[0].x*t)))+t*(3*points[1].x+t*(-6*points[1].x+
			points[1].x*3*t))+t*t*(points[2].x*3-points[2].x*3*t)+
			points[3].x*t*t*t;
			y2=(points[0].y+t*(-points[0].y*3+t*(3*points[0].y-
			points[0].y*t)))+t*(3*points[1].y+t*(-6*points[1].y+
			points[1].y*3*t))+t*t*(points[2].y*3-points[2].y*3*t)+
			points[3].y*t*t*t;
			//add line
			hitbox.add(new Line(new PVector(x1,y1),new PVector(x2,y2),material));
			x1 = x2;
			y1 = y2;
		}
	}

	@Override
	public void show(Graphics2D g2D)
	{
		for(Line l:hitbox) l.show(g2D);
	}
	
	@Override
	public PVector normal(PVector point)
	{
		Line best=hitbox.get(0);
	    boolean first=true;
	    for(int w=1;w<hitbox.size();w++)
	    {
	      if(first || PVector.distSq(point,best.a)>PVector.distSq(point,hitbox.get(w).a))
	      {                                                                          
	        best=hitbox.get(w);
	        //thisLine=hitbox.get(w);
	        first=false;
	      }       
	    }
	    if(!first)
	    {
	      return best.normal(point);
	    }else
	      return new PVector(1,0);
	}
	
	@Override
	public PVector intersect(Ray ray)
	{
		//iteruje się przez wsystkie linie hitboxu i szuka przecięcia najbliższego źródła promienia
	    PVector best=new PVector();
	    boolean first=true;        //czy już został znaleziony jakiś promień
	    //Line thisLine=null;    //linia na której zostało znalezionoe przecięcie
	    for(int w=0;w<hitbox.size();w++)
	    {
	      PVector point=hitbox.get(w).intersect(ray); //zwraca przecięcie promienia z linią
	      
	      if(point!=null && (first || PVector.distSq(ray.pos,best)>PVector.distSq(ray.pos,point)))   //porównuje je z już znalezionymi punktami
	      {                                                                          //aby znaleźć najbliższy
	        best=point;
	        //thisLine=hitbox.get(w);
	        first=false;
	      }       
	    }
	    if(!first) //jeżeli zostało znalezione jakieś przcięcie, odsuń je trochę od krzywej, żeby nie zahaczało o hitbox
	    {
	      return best;
	    }else
	      return null;
	}
	
	

}