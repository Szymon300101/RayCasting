package util;
public class PVector {
	public double x;
	public double y;
	
	public PVector(double x,double y)	//normal constructor
	{
		this.x=x;
		this.y=y;
	}
	
	public PVector(double angle)		//angle constructor (normalized)
	{
		this.x=Math.cos(angle);
		this.y=Math.sin(angle);
	}
	
	public PVector() 
	{
		this.x=0;
		this.y=0;
	}
	
	public int intX()
	{
		return (int)this.x;
	}
	
	public int intY()
	{
		return (int)this.y;
	}
	
	public void set(double x,double y)
	{
		this.x=x;
		this.y=y;
	}
	
	public double mag()					//magnitude of a vector
	{
		return Math.sqrt(this.magSq());
	}
	
	public double magSq()				//squared magnitude of a vector
	{
		return this.x*this.x + this.y*this.y;
	}
	
	public PVector setMag(double mag)
	{
		this.normalize();
		this.mult(mag);
		return this;
	}
	
	public static double dist(PVector a,PVector b)
	{
		return Math.sqrt(distSq(a,b));
	}
	
	public static double distSq(PVector a,PVector b)
	{
		return PVector.sub(a, b).magSq();
	}
	
	public static PVector add(PVector a,PVector b)
	{
		return new PVector(a.x+b.x, a.y+b.y);
	}	
	public PVector add(PVector v)
	{
		this.x+=v.x;
		this.y+=v.y;
		return this;
	}
	
	public static PVector sub(PVector a,PVector b)
	{
		return new PVector(a.x-b.x, a.y-b.y);
	}
	public void sub(PVector v)
	{
		this.x-=v.x;
		this.y-=v.y;
	}
	
	public static PVector mult(PVector v,double a)
	{
		return new PVector(v.x*a, v.y*a);
	}
	public PVector mult(double a)
	{
		this.x*=a;
		this.y*=a;
		return this;
	}
	
	public static PVector div(PVector v,double a)
	{
		return new PVector(v.x/a, v.y/a);
	}
	public PVector div(double a)
	{
		this.x/=a;
		this.y/=a;
		return this;
	}

	public double heading()				//heading of a vector
	{
		return Math.atan2(this.y, this.x);
	}
	
	public PVector normalize()				//normalize a vector to length of 1
	{
		PVector v=new PVector(this.heading());
		this.x=v.x;
		this.y=v.y;
		return this;
	}
	
	public PVector rotate(double angle)	//rotate a vector
	{
		PVector v=new PVector(this.heading()+angle);
		v.mult(this.mag());
		this.x=v.x;
		this.y=v.y;
		return this;
	}
	
	public PVector copy()	//rotate a vector
	{
		return new PVector(this.x,this.y);
	}
	
}
