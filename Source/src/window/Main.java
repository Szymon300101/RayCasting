package window;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
	
	public static void main(String[] args)
	{
		AppFrame frame=new AppFrame();
		
		TimerTask task=new ProgramTask(frame);
		Timer timer=new Timer(true);
		timer.scheduleAtFixedRate(task, 0, 10);
	}
}
