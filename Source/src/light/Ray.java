package light;

import java.awt.Graphics2D;
import java.util.ArrayList;

import shape.Line;
import shape.Shape;
import util.*;
import window.AppFrame;
import window.settings.Settings;

public class Ray {
	private AppFrame mFrame;
	
	public PVector pos;  //pozycja źródła promienia
	public PVector dir;  //kierunek padania  
	float power=1;  //energia (0 do 1)
	int number=1; //numer pokolenia do którego należy promień (1-wychodzący ze źródła; 0-omija pierwszą przeszkodę, pozwala wpaść słońcu przez granicę ekranu)
	  
	PVector cPoint;  //punkt zderzenia promienia z przeszkodą
	Shape cShape;    //obiekt z którym promień się zderzył
	
	  Ray(AppFrame frame,PVector pos,PVector dir)
	  {
	    this.pos=pos;
	    this.dir=dir;
	    this.mFrame=frame;
	  }
	  Ray(AppFrame frame,PVector pos,PVector dir,float power)
	  {
	    this(frame,pos,dir);
	    this.power=power;
	  }
	  Ray(AppFrame frame,PVector pos,PVector dir,float power,int num)
	  {
	    this(frame,pos,dir,power);
	    this.number=num;
	  }
	  
	 void setDir(float angle)
	 {
	   this.dir=new PVector(angle);
	 }
	 
	 void setPos(PVector pos)
	 {
	   this.pos=pos;
	 }
	 
	 void show(Graphics2D g2D)
	 {
		 if(this.power==0 || this.number>10) return;   //jeżeli promień zgasł, lub jest zbyt głęboko zagnieżdzony, znika
		   
		   this.collide(mFrame.walls);                                //wyszukiwanie punktu kolizji
		   if(this.cPoint==null || this.cShape==null) return;  //jeżeli nie ma kolizji, nie można nic narysować
		   
		   if(this.cShape.material.isMesuring)
			   mFrame.incrementmesurement(this.power);
		   
		   g2D.setColor(CColor.gray(255,(int)(255*Math.pow(this.power,1/Settings.PERC_LIGHT))));    //ustalanie jasności (alpha) na podstawie energii i parametru jasności
		   g2D.drawLine((int) (this.pos.x*1000),(int) (this.pos.y*1000),(int) (cPoint.x*1000),(int) (cPoint.y*1000));
		   
		   //jeżeli promień ma jeszcze wystarczającą energię, tworzy następne pokolenie
		   if(this.power>Settings.MIN_POWER)
		   {
		     ArrayList<Ray> next_gen=this.reflect(cPoint,cShape);  //uzyskanie listy odbić
		     for(Ray r:next_gen)
		     {
		       r.show(g2D);    //rekurencyjne wywołanie dla każdego odbicia
		     }
		     next_gen=null;
		   }
	 }
	 
	//funkcja znajdująca pierwsze przecięcie (kolizję) promienia z czymkolwiek z listy 'walls'
	 void collide(ArrayList<Shape> walls)
	 {
	   //algorytm prawie ten sam co w łuku i bezierze, znajduje punkt najbliższy źródła promienia
	   cPoint=new PVector();
	   boolean first=true;
	   PVector secPoint=new PVector();  //jako że promień może mieć number==0, algortm szuka też drugiego najbliższego przecięcia 
	   Shape secShape=(Shape) new Line();      //i obiektu z którym ono nastąpiło
	   boolean second=true;             //czy wogóle nastąpiło drogie przecięcie? (true - nie nastąpiło)
	    for(int w=0;w<walls.size();w++)
	    {
	      PVector point=walls.get(w).intersect(this); //zwraca przecięcie promienia z czymkolwiek
	      
	      if(point!=null && (first || PVector.distSq(this.pos,cPoint)>PVector.distSq(this.pos,point)))   //porównuje je z już znalezionymi punktami
	      {                                                                          //aby znaleźć najbliższy (i drugi najbliższy)
	         if(!first) 
	         {
	           secPoint=cPoint;
	           secShape=cShape;
	           second=false;
	         }
	          cPoint=point;
	          cShape=walls.get(w);
	          first=false;
	      }       
	    }
	    if(first) cPoint=null;
	    
	    if(this.number==0)  //jeżeli number==0, zamiast najbliższego punktu, urzyj drugiego najbliższego. 
	      if(!second)        //urzywane gdy promień słoneczny musi przebić się przez obramowanie ekranu, i dopiero potem kolidować
	      {
	        cPoint=secPoint;
	        cShape=secShape;
	      }else 
	        cPoint=null;
	 }

	 //funkcja obliczająca co wyjdzie ze zderzenia promienia z obiektem
	 ArrayList<Ray> reflect(PVector point,Shape shape)
	 {
	   ArrayList<Ray> rays=new ArrayList();
	   PVector normal=shape.normal(point);
	   if(PVector.add(this.dir,normal).magSq()>PVector.sub(this.dir,normal).magSq()) //wektor normalny musi być skierowany w strone źródła promienia 
	      normal.mult(-1);
	    point.add(PVector.mult(normal,0.001));  //nieznaczne odsunięcie punktu kolizji od linii, żeby uniknąć zakłuceń (jak w krzywych)
	    //line(point.x,point.y,PVector.add(point,normal).x,PVector.add(point,normal).y);  //wyświetlanie wektora normalnego
	    PVector ref_dir=this.dir.copy().mult(-1);
	    ref_dir=ref_dir.rotate(2*(normal.heading()-ref_dir.heading()));    //kierunek odbicia referencyjnego
	    
	    //układanie odbić
	    double difVal=1-shape.material.difusion;    //parametr dyfuzji jest odwrócony, bo przyjęło się że [0] to odbicie, [1] to rozproszenie
	    if(difVal<1)  //jeżeli światło się rozprasza
	    {
	      //robi ładną gwiazdkę, którj krztałt jest zależny od dyfuzji
	      final int middle=Settings.REFLECTIONS/2;
	      final double angle=Math.PI/(Settings.REFLECTIONS+1);
	      double[] values=new double[Settings.REFLECTIONS];
	      double sum=0;
	      for(int i=0;i<Settings.REFLECTIONS;i++)
	      {
	          values[i]=0;
	          values[i]=Math.cos(Math.abs((i+1)*angle-Math.PI/2));
	          double rel_angle=Math.abs((i+1)*angle-(Math.PI/2-(normal.heading()-ref_dir.heading())));
	          if(rel_angle<Math.PI/2 && difVal>0.1)
	            values[i]+=Math.pow(Math.cos(rel_angle),Math.floor(difVal*100))*(Math.pow(difVal,1)*10);
	          if(rel_angle > Math.PI*(1-difVal))
	        	  values[i]=0;
	            //values[i]+=pow(cos(rel_angle),floor(difVal*10))*(pow(difVal,3)*10);
	          sum+=values[i];
	      }
	      for(int i=0;i<Settings.REFLECTIONS;i++) values[i]/=sum;  //normalizuje energie promieni, żeby ich suma była równa 1
	      for(int i=0;i<Settings.REFLECTIONS;i++)
	      {
	        if(values[i]>0.001)  //jeżeli promień ma znaczącą enegię, dodaje go
	          rays.add(new Ray(mFrame, point,new PVector(((i+1)*angle-Math.PI/2)+(normal.heading() + Math.random()*0.02)),(float) (this.power*shape.material.reflect*values[i]),this.number+1));
	      }
	    }else
	      rays.add(new Ray(mFrame, point,ref_dir,(float) (this.power*shape.material.reflect),this.number+1));  //odbicie lustrzne (jeden promień)
	      
	    return rays;
	 }
}
