package light;

import java.awt.Graphics2D;
import java.util.ArrayList;

import util.CColor;
import util.PVector;
import window.AppFrame;

public class DotSource extends Source{
	
	PVector pos;
	
	public DotSource(AppFrame frame,int r_num) 
	{
		super(frame,r_num);
		this.pos=frame.graphic.center;
//		System.out.print(pos.x);
//		System.out.print("-");
//		System.out.println(pos.y);
		rays=new ArrayList<Ray>();
		//rays.add(new Ray(mFrame,pos,new PVector(0),max_power));
	    for(int i=0;i<r_num;i++) rays.add(new Ray(mFrame,pos,new PVector(Math.PI*2/r_num*i+1),max_power));  //inicjalizacja promieni
	}
	  
	  public DotSource ExtractSettings()
	  {
		  DotSource newSource = new DotSource(mFrame, this.r_num);
		  newSource.setPos(this.pos);
		  newSource.max_power = this.max_power;
		  newSource.mFrame = null;
		  
		  return newSource;
	  }
	
//	@Override
//	public void show(Graphics2D g2D)
//	{
//		PVector pnt;
//		g2D.setColor(CColor.gray(255));
//		for(int i=0;i<r_num;i++)
//		{
//			pnt=PVector.add(this.pos, PVector.mult(new PVector(Math.PI*2/r_num*i),5));
//			g2D.drawLine((int)(this.pos.x*1000),(int)(this.pos.y*1000),(int)(pnt.x*1000),(int)(pnt.y*1000));
//		}
//		
//	}
	
	@Override
	public void setPos(PVector pos)
	{
		this.pos=pos;
		for(int i=0;i<r_num;i++)
	      rays.get(i).setPos(pos);
	}
	@Override
	public PVector getPos()
	{
		return this.pos;
	}

}
