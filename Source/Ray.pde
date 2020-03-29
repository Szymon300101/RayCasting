//140 linii
class Ray //klasa promienia światła, zawierająca obliczenia odbić i rozproszeń
{
  PVector pos;  //pozycja źródła promienia
  PVector dir;  //kierunek padania  
  float power=1;  //energia (0 do 1)
  int number=1; //numer pokolenia do którego należy promień (1-wychodzący ze źródła; 0-omija pierwszą przeszkodę, pozwala wpaść słońcu przez granicę ekranu)
  
  PVector cPoint;  //punkt zderzenia promienia z przeszkodą
  Shape cShape;    //obiekt z którym promień się zderzył
  
  Ray(PVector pos,PVector dir)
  {
    this.pos=pos;
    this.dir=dir;
  }
  Ray(PVector pos,PVector dir,float power)
  {
    this(pos,dir);
    this.power=power;
  }
  Ray(PVector pos,PVector dir,float power,int num)
  {
    this(pos,dir,power);
    this.number=num;
  }
  
 void setDir(float angle)
 {
   this.dir=PVector.fromAngle(angle);
 }
 
 void setPos(PVector pos)
 {
   this.pos=pos;
 }
 
 //rekurencyjna funkcja odpowiadająca za wyświetlanie promienia i tworzenie jego dzieci
 void show(ArrayList<Shape> walls)
 {
   if(this.power==0 || this.number>10) return;   //jeżeli promień zgasł, lub jest zbyt głęboko zagnieżdzony, znika
   
   this.collide(walls);                                //wyszukiwanie punktu kolizji
   if(this.cPoint==null || this.cShape==null) return;  //jeżeli nie ma kolizji, nie można nic narysować
   
   
   stroke(255,255*pow(this.power,1/PERC_LIGHT));    //ustalanie jasności (alpha) na podstawie energii i parametru jasności
   line(this.pos.x,this.pos.y,cPoint.x,cPoint.y);
   
   //jeżeli promień ma jeszcze wystarczającą energię, tworzy następne pokolenie
   if(this.power>MIN_POWER && !lock)
   {
     ArrayList<Ray> next_gen=this.reflect(cPoint,cShape);  //uzyskanie listy odbić
     for(Ray r:next_gen)
     {
       r.show(walls);    //rekurencyjne wywołanie dla każdego odbicia
     }
     next_gen=null;
   }
 }
 
 //funkcja znajdująca pierwsze przecięcie (kolizję) promienia z czymkolwiek z listy 'walls'
 void collide(ArrayList<Shape> walls)
 {
   //alforytm prawie ten sam co w łuku i bezierze, znajduje punkt najbliższy źródła promienia
   cPoint=new PVector();
   boolean first=true;
   PVector secPoint=new PVector();  //jako że promień może mieć number==0, algortm szuka też drugiego najbliższego przecięcia 
   Shape secShape=new Shape();      //i obiektu z którym ono nastąpiło
   boolean second=true;             //czy wogóle nastąpiło drogie przecięcie? (true - nie nastąpiło)
    for(int w=0;w<walls.size();w++)
    {
      PVector point=walls.get(w).intersect(this); //zwraca przecięcie promienia z czymkolwiek
      
      if(point!=null && (first || this.pos.dist(cPoint)>this.pos.dist(point)))   //porównuje je z już znalezionymi punktami
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
    float difVal=1-shape.material.difusion;    //parametr dyfuzji jest odwrócony, bo przyjęło się że [0] to odbicie, [1] to rozproszenie
    if(difVal<1)  //jeżeli światło się rozprasza
    {
      //robi ładną gwiazdkę, którj krztałt jest zależny od dyfuzji
      final int middle=REFLECTIONS/2;
      final float angle=PI/(REFLECTIONS+1);
      float[] values=new float[REFLECTIONS];
      float sum=0;
      for(int i=0;i<REFLECTIONS;i++)
      {
          values[i]=0;
          values[i]=cos(abs((i+1)*angle-PI/2));
          float rel_angle=abs((i+1)*angle-(PI/2-(normal.heading()-ref_dir.heading())));
          if(rel_angle<PI/2 && difVal>0.1)
            values[i]+=pow(cos(rel_angle),floor(difVal*10))*(pow(difVal,3)*10);
          sum+=values[i];
      }
      for(int i=0;i<REFLECTIONS;i++) values[i]/=sum;  //normalizuje energie promieni, żeby ich suma była równa 1
      for(int i=0;i<REFLECTIONS;i++)
      {
        if(values[i]>0.001)  //jeżeli promień ma znaczącą enegię, dodaje go
          rays.add(new Ray(point,PVector.fromAngle((i+1)*angle+(normal.heading()-PI/2)),this.power*shape.material.reflect*values[i],this.number+1));
      }
    }else
      rays.add(new Ray(point,ref_dir,this.power*shape.material.reflect,this.number+1));  //odbicie lustrzne (jeden promień)
      
    return rays;
 }
}
