package light;

import java.awt.Graphics2D;
import java.util.ArrayList;

import shape.*;
import util.*;
import window.AppFrame;


public abstract class Source {
	
	public AppFrame mFrame;
	String type;
	public int r_num;
	int start_num=1;
	ArrayList<Ray> rays;
	float max_power=1;
	
	public Source(AppFrame frame,int r_num)
	{
		this.mFrame=frame;
		this.r_num=r_num;
	}
	
	public void show(Graphics2D g2D)
	{
		if(this.rays!=null)
	      for(int i=0;i<r_num;i++)
	      {
	        rays.get(i).number=start_num;
	        rays.get(i).show(g2D);
	      }
	}
	
	public void setPos(PVector pos) {}
	public PVector getPos() {return null;}
	
	public void setDir(double dir) {}
	public double getDir() {return 0;}
	
}
