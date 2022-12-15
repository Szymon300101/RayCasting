package window;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.kabeja.dxf.Bounds;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLJPanel;

import light.Source;
import shape.Shape;
import shape.Bezier;
import shape.Line;
import util.CColor;
import util.Material;
import util.PVector;


public class GraphicPanel extends GLJPanel{

	private static final long serialVersionUID = 1L;
	
	AppFrame mFrame;
	
	public Bounds dxfBounds;
	public double scale=1;      //skala
	public double transX=0;     //przesunięcie od 0 w X
	public double transY=0;     //przesunięcie od 0 w Y
	public double strW=1;       //grubość lini
	public PVector center;     //środek ekranu
	public int width, height;
	public double dxfMaxDim;
	
	
	public GraphicPanel(AppFrame frame)
	{
		this.mFrame=frame;
		GLCapabilities caps = new GLCapabilities(null);
	    caps.setHardwareAccelerated(true);
	    this.setRequestedGLCapabilities(caps);
		this.setLayout(new BorderLayout());
		this.setBackground(CColor.gray(0));
	}
	public void paintComponent(Graphics g)
	{
		if(mFrame.sources.size()>0 && mFrame.isMousePosValid)
			mFrame.sources.get(0).setPos(mFrame.mListener.getScaledMousePos());
		mFrame.isMousePosValid = true;
		mFrame.sumMesuredPower=0;
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setColor(CColor.gray(0));
		g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(mFrame.fileOpen) 
		{
			g2D.scale(scale,-scale);
			g2D.translate(transX,transY-this.getHeight()/scale);
			g2D.setStroke(new BasicStroke((float) strW));
			
			//mFrame.tSource.show(g2D);
			for(Shape w:mFrame.walls)    //wyświetlanie obiektów
		      w.show(g2D);
			
			for(Source s:mFrame.sources)
				s.show(g2D);
		}
		g2D.setColor(CColor.gray(255));
	}
	
	public void getScale(Bounds b)
	{
		dxfBounds = b;
		width=this.getWidth();
		height=this.getHeight();
		if((b.getMaximumX()-b.getMinimumX())/width>(b.getMaximumY()-b.getMinimumY())/height)
		  {
		    double b_width=b.getMaximumX()-b.getMinimumX();
		    scale=(width/b_width)*0.9;
		    transX=-b.getMinimumX()+(b_width*0.0495);
		    transY=-b.getMinimumY()+(b_width*0.0495);
		    strW=1/((width/b_width)*0.9);
		  }else
		  {
		    double b_height=b.getMaximumY()-b.getMinimumY();
		    scale=(height/b_height)*0.9;
		    transX=-b.getMinimumX()+(b_height*0.0495);
		    transY=-b.getMinimumY()+(b_height*0.0495);
		    strW=1/((height/b_height)*0.9);
		  }
		  center=new PVector(width/scale/2-transX,height/scale/2-transY);
		  scale/=1000;
		  transX*=1000;
		  transY*=1000;
		  dxfMaxDim = Math.max(b.getMaximumX()-b.getMinimumX(), b.getMaximumY()-b.getMinimumY());
	}
	
	//generowanie obramowania ekranu w postaci linii
	public ArrayList<Line> makeBorders()
	{
	  ArrayList<Line> border=new ArrayList<Line>();
	  Material black=new Material("_border_",1,0,CColor.gray(0)); //idealnie pochłaniający materiał
	  
	  //ustalenie współrzędnych rogów ekranu
	  double left=(-this.transX-this.strW)/1000;
	  double right=(-this.transX+(this.getWidth()/scale)+this.strW)/1000;
	  double top=(-this.transY-this.strW)/1000;
	  double bottom=(-this.transY+(this.getHeight()/scale)+this.strW)/1000;
	  
	  //tworzenie linii
	  border.add(new Line(new PVector(left,top),new PVector(right,top),black));
	  border.add(new Line(new PVector(right,top),new PVector(right,bottom),black));
	  border.add(new Line(new PVector(right,bottom),new PVector(left,bottom),black));
	  border.add(new Line(new PVector(left,bottom),new PVector(left,top),black));
	  
	  return border; 
	}
}
