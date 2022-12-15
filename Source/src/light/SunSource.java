package light;

import java.awt.Frame;
import java.util.ArrayList;

import util.PVector;
import window.AppFrame;

public class SunSource extends Source {
	PVector base;  //wektor 'wodzący' linię promieni. między środkiem ekranu z środkiem lini
	  float dir;     //kierunek z którego padają promienie
	  
//	  public SunSource(AppFrame frame, int num,float max)
//	  {
//		  this(frame, num, max, 0);
//	  }
	  
	  public SunSource(AppFrame frame, int num,float max)
	  {
		super(frame, num);
	    type="Sun";
	    r_num=num;
	    max_power=max;
	    start_num=0;
	    dir=0;
	    this.base=new PVector(dir);
	    int width = frame.graphic.width;
	    int height = frame.graphic.height;
	    this.base.mult(Math.sqrt(width*width+height*height)/2);  //ustalanie promienia wodzącego na połowę przekątnej ekranu
	    PVector increment=base.copy().rotate(Math.PI/2).normalize().mult(1.0*width/r_num/2);  //odlegość między promieniami (cały szereg ma długość przekatnej - 2*base)
	    rays=new ArrayList<Ray>();
	    for(int i=0;i<r_num/2;i++)   //pierwsza połowa szeregu
	      rays.add(new Ray(frame,PVector.add(PVector.add(frame.graphic.center,base),PVector.mult(increment,i)),base.copy().normalize().mult(-1),max_power));
	    increment.mult(-1);
	    for(int i=r_num/2;i<r_num;i++)   //druga połowa szeregu
	      rays.add(new Ray(frame,PVector.add(PVector.add(frame.graphic.center,base),PVector.mult(increment,i-r_num/2+1)),base.copy().normalize().mult(-1),max_power));
	  }
	  
	  public SunSource ExtractSettings()
	  {
		  SunSource newSource = new SunSource(mFrame, this.r_num, this.max_power);
		  newSource.setDir(this.dir);
		  newSource.mFrame = null;
		  newSource.rays = null;
		  return newSource;
	  }

	  //obracanie żródła tak by podany punkt był na wektorze 'base' (dzięki temu można obracać myszką)
	  @Override
	  public void setPos(PVector point)
	  {
	    dir=(float)PVector.sub(point,mFrame.graphic.center).heading();
	    setDir(dir);
	  }

		public void setDir(double dir) {
			base.rotate(dir-base.heading());  //obracanie bazy
		    
		    //ustalanie nowych kierunków i pozycji wszystkich promieni (zasadniczo budowanie źródła od nowa)
		    PVector increment=base.copy().rotate(Math.PI/2).normalize().mult(1.0*mFrame.graphic.dxfMaxDim/r_num*2);
		    for(int i=0;i<r_num/2;i++) 
		    {
		      rays.get(i).setPos(PVector.add(PVector.add(mFrame.graphic.center,base),PVector.mult(increment,i)));
		      rays.get(i).setDir((float)base.copy().normalize().mult(-1).heading());
		    }
		    increment.mult(-1);
		    for(int i=r_num/2;i<r_num;i++)
		    {
		      rays.get(i).setPos(PVector.add(PVector.add(mFrame.graphic.center,base),PVector.mult(increment,i-r_num/2+1)));
		      rays.get(i).setDir((float)base.copy().normalize().mult(-1).heading());
		    }
		}

		@Override
	  public double getDir()
	  {
	    return Math.toDegrees(dir);
	  }
}
