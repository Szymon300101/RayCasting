package window;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.*;

import util.PVector;

public class MouseListener extends MouseAdapter{
	
	AppFrame mFrame;
	private PVector mPos;
	private boolean pressed;
	
	public MouseListener(AppFrame frame)
	{
		this.mFrame=frame;
		mPos=new PVector(0,0); 
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		this.pressed=true;
//		getMousePos().set(e.getX()-8,e.getY()-30);
//		System.out.println("Mouse x: " + getMousePos().x);
//        System.out.println("Mouse y: " + getMousePos().y);
        
        
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		this.pressed=false;
	}

	public PVector getMousePos() {
		return this.mPos;
	}
	public PVector getScaledMousePos() {
		double x=(mPos.x-mFrame.graphic.getLocation().x-mFrame.getLocation().x-6)/mFrame.graphic.scale-mFrame.graphic.transX;
		double y=(mPos.y-mFrame.graphic.getLocation().y-mFrame.getLocation().y-30)/(-mFrame.graphic.scale);
				y-=(mFrame.graphic.transY-mFrame.graphic.getHeight()/mFrame.graphic.scale);
		return new PVector(x,y).div(1000);
    		
	}
	
	public void readMousePos() {
		Point m=MouseInfo.getPointerInfo().getLocation();
		this.mPos=new PVector(m.x,m.y);
	}
	
	public boolean isPressed()
	{
		return this.pressed;
	}

}
