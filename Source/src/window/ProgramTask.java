package window;

import java.util.TimerTask;

import util.PVector;

public class ProgramTask extends TimerTask{
	AppFrame mFrame;
	public ProgramTask(AppFrame frame)
	{
		this.mFrame=frame;
	}
	
	@Override
	public void run()
	{
		//refreshing mouse position and mouseDragged event
		PVector prevPos=mFrame.mListener.getMousePos();
		mFrame.mListener.readMousePos();
		if(mFrame.mListener.getMousePos()!=prevPos && mFrame.mListener.isPressed())
		{
			mFrame.graphic.repaint();
		}
		
	}
}
