package shape;

import java.awt.Graphics2D;
import java.util.ArrayList;

import light.Ray;
import util.Material;
import util.PVector;

public class Arc extends Shape{
	
	 PVector c;            //środek
	 double r, start, angle;  //promień, kąt początku, kąt końca
	 
	 public Arc(double cx,double cy,double r,double start,double angle,Material mat)
	  {
	    this.c=new PVector(cx,cy);
	    this.r=r;
	    this.start=start;
	    this.angle=angle;
	    material=mat;
	    System.out.println(this.angle);
	  }
	 
	 @Override
	 public void show(Graphics2D g2D)
		{
			g2D.setColor(material.d_color);
			g2D.drawArc((int) ((c.x-r)*1000),(int) ((c.y-r)*1000),(int) (r*2000),(int) (r*2000),(int) start,(int) angle);
		}
	 
	 @Override
	 public PVector normal(PVector point)
	 {
		 return PVector.sub(point,c).normalize();
	 }
	 
	 @Override
	 public PVector intersect(Ray ray)
	 {
		//punkty wyznaczające promień
		final PVector Pa=ray.pos.copy();
		final PVector Pb=PVector.add(Pa, ray.dir);
		//równanie promienia
		final double A=-(Pb.y-Pa.y);
		final double B=Pb.x-Pa.x;
		final double C=-A*Pa.x-B*Pa.y;
		
		final double dist=Math.abs(A*this.c.x + B*this.c.y + C)/Math.sqrt(A*A + B*B);
		
		if(dist > (this.r)) return null; 
		
		double side_angle;
		if(PVector.distSq(PVector.add(this.c, ray.dir.copy().rotate(Math.PI/2).mult(dist)),ray.pos) >
			PVector.distSq(PVector.add(this.c, ray.dir.copy().rotate(-Math.PI/2).mult(dist)),ray.pos))
				side_angle=-Math.PI/2;
		else
				side_angle=Math.PI/2;
		
		final double delta_angle=Math.acos(dist/this.r);
		final double base_angle=ray.dir.heading()+side_angle;
		final double a0=base_angle-delta_angle;
		final double a1=base_angle+delta_angle;
		final boolean ok_a0=onAngle(a0);
		final boolean ok_a1=onAngle(a1);
		final PVector v0=new PVector(a0).mult(this.r).add(this.c);
		final PVector v1=new PVector(a1).mult(this.r).add(this.c);
		
		if(ok_a0 && !ok_a1)
		{
			if(Math.abs(PVector.sub(v0, ray.pos).heading()-ray.dir.heading())>Math.PI/2)
				return null;
			else
				return v0;
		}else if(!ok_a0 && ok_a1)
		{
			if(Math.abs(PVector.sub(v1, ray.pos).heading()-ray.dir.heading())>Math.PI/2)
				return null;
			else
				return v1;
		}else if(ok_a0 && ok_a1)
		{
			if(PVector.distSq(v0, ray.pos) < PVector.distSq(v1, ray.pos))
				{
					if(Math.abs(PVector.sub(v0, ray.pos).heading()-ray.dir.heading())>Math.PI/2)
					{
						if(Math.abs(PVector.sub(v1, ray.pos).heading()-ray.dir.heading())<Math.PI/2)
							return v1;
						else
							return null;
					}
					else
						return v0;
				}
			else
				{
					if(Math.abs(PVector.sub(v1, ray.pos).heading()-ray.dir.heading())>Math.PI/2)
					{
						if(Math.abs(PVector.sub(v0, ray.pos).heading()-ray.dir.heading())<Math.PI/2)
							return v0;
						else
							return null;
					}
					else
						return v1;
				}
		}
		
		return null;
	 }
	 
	 private boolean onAngle(double angle)
	 {
		 if(angle<0) angle+=Math.PI*2;
		 if(angle>Math.PI*2) angle-=Math.PI*2;
		 angle=360-(angle/Math.PI*180);
		 double low=this.start;
		 double high;
		 if(this.angle>0)
		 {
			 high=low+this.angle;
			 if(high>360)
				 high-=360;
		 }else
		 {
			 high=low;
			 low=low+this.angle;
			 if(low<0)
				 low+=360;
		 }
		 
		 if(low<high)
		 {
			 if(angle>=low && angle<=high)
				 return true;
		 }else
		 {
			 if(!(angle>=high && angle<=low))
				 return true;
		 }
		 return false;
	 }
	 
}