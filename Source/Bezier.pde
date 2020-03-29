//205 linii
class Bezier extends Shape    //bezier będąćy efektem rozbicia krzywej (B-spline)
{
  PVector[] anchors;    //punkty kotwiczne (początek i koniec krztałtu)
  PVector[] controls;   //punkty kontrolne (2)
  ArrayList<Line> hitbox=new ArrayList();  //zbiór linii przybliżających krzywą.
                                           //urzywany do sprawdzania przecięć i znajdowania normalnej krzywej
  
  Bezier(PVector an1,PVector an2,PVector con1,PVector con2,Material mat)
  {
    anchors=new PVector[2];
    anchors[0]=an1;
    anchors[1]=an2;
    controls=new PVector[2];
    controls[0]=con1;
    controls[1]=con2;
    material=mat;
    
    //generowanie przybliżenia beziera (analogicznie jak w kole, tylko bez kratownicy)
    PVector x=this.findPoint(0);
    for(int i=1;i<=PRECISION;i++)
    {
      PVector y=this.findPoint(1.0*i/PRECISION);
      hitbox.add(new Line(x,y,material));
      x=y;
    }
  }
  
  //funkcja zwracająca koordynaty punktu w zadanym miejscu beziera (t ma wartości 0 do 1)
  private PVector findPoint(float t)
  {
    float x=bezierPoint(anchors[0].x,controls[0].x,controls[1].x,anchors[1].x,t);
    float y=bezierPoint(anchors[0].y,controls[0].y,controls[1].y,anchors[1].y,t);
    return new PVector(x,y);
  }
  
  //funkcja odwrotna do powyższej, znacznie bardziej skomplikowana, bo processing nie daje narzędzi
  //UWAGA: złożona obliczeniowo
  private float findT(PVector point)
  {
    //funkcja iteruje się po całej długości beziera, szukając podanego punktu
    double min_dist=1;
    float t=1;
    for(int i=0;i<PRECISION;i++)
    {
      PVector cp=this.findPoint(1.0*i/PRECISION);
      double dist=PVector.sub(point,cp).magSq();  //oblicza odległość(^2) szukanego punktu od aktualnego
      if(dist<min_dist) 
      {
        min_dist=dist;
        t=1.0*i/PRECISION;
      }
    }
    return t;  //zwraca najbliższe przybliżenie jakie udało jej się znaleźć
  }
  
  void show()
  {
    stroke(material.d_color);
    bezier(anchors[0].x,anchors[0].y,controls[0].x,controls[0].y,controls[1].x,controls[1].y,anchors[1].x,anchors[1].y);
    
    //for(Line l:hitbox) l.show();
  }
  
  //normalna beziera
  PVector normal(PVector point)
  {
    //teoretycznie processing daje do tego funkcję, ale najpierw trzeba znaleźć t
    float t=this.findT(point);
    float tx = bezierTangent(anchors[0].x,controls[0].x,controls[1].x,anchors[1].x,t);
    float ty = bezierTangent(anchors[0].y,controls[0].y,controls[1].y,anchors[1].y,t);
    PVector normal=PVector.fromAngle(atan2(ty, tx)+HALF_PI);
    return normal;
  }
  
  //znajdowanie przecięcia promienia z bezierem. jak w łuku, szuka przecięć wśród lini hitboxu
  //algorytm jest bardzo uproszczony i możnaby znacznie zwiększyć dokładność, ale jest i tak zadziwiająco dobrze
  PVector intersect(Ray ray)
  {
    //iteruje się przez wsystkie linie hitboxu i szuka przecięcia najbliższego źródła promienia
    PVector best=new PVector();
    boolean first=true;        //czy już został znaleziony jakiś promień
    Line thisLine=null;    //linia na której zostało znalezionoe przecięcie
    for(int w=0;w<hitbox.size();w++)
    {
      PVector point=hitbox.get(w).intersect(ray); //zwraca przecięcie promienia z linią
      
      if(point!=null && (first || ray.pos.dist(best)>ray.pos.dist(point)))   //porównuje je z już znalezionymi punktami
      {                                                                          //aby znaleźć najbliższy
        best=point;
        thisLine=hitbox.get(w);
        first=false;
      }       
    }
    if(!first) //jeżeli zostało znalezione jakieś przcięcie, odsuń je trochę od krzywej, żeby nie zahaczało o hitbox
    {
      float d=0.01;    //arbitralna odległość o którą odsuwany jest punkt. Nie jest widzialna gołym okiem, a działa
      PVector normal=thisLine.normal(best);
      if(PVector.add(ray.dir,normal).magSq()>PVector.sub(ray.dir,normal).magSq())
        normal.mult(-1);
      normal.mult(d);
      best.add(normal);
      return best;
    }else
      return null;
  }
}


//funkcja rozbijająca B-spline 3 stopnia na krzywe beziera
//jest moją autorską implementacją algorytmu Boehma (bardzo łopatologiczną)
//http://web.archive.org/web/20120227050519/http://tom.cs.byu.edu/~455/bs.pdf
ArrayList<Bezier> splitSpline(DXFSpline spline,Material mat)
{
  ArrayList<Bezier> beziers=new ArrayList();
  
  ArrayList<PVector> points = new ArrayList();    //punkty kontrolne krzywej
  double[][] pointC;  //wartości polarne punktów kontrolnych krzywej
  double[] knots;      //węzły krzywej
  PVector[] anchors;    //kotwice bezierów
  double[][] anchorC;    //wartości polarne kotwic
  PVector[] controls;    //punkty kontrolne bezierów
  double[][] controlC;    //wartości polarne punktów kontrolnych
  
  //odczyt punktów kontrolnych z krzywej
  knots=spline.getKnots();
  Iterator<SplinePoint> Piterator=spline.getSplinePointIterator();
  while(Piterator.hasNext())
  {
    SplinePoint SPoint=Piterator.next();
    points.add(new PVector((float)SPoint.getX(),(float)SPoint.getY()));
  }
  
  //println("Degree: " + spline.getDegree());
  //println("Knots: " + spline.getKnots().length);
  //println("Points: " + points.size());
  
  //inicjalizacja tablic
  pointC=new double[points.size()][];
  for(int i=0;i<points.size();i++) pointC[i]=new double[3];
  anchors=new PVector[knots.length-6];
  anchorC=new double[anchors.length][];
  for(int i=0;i<anchors.length;i++) anchorC[i]=new double[3];
  controls=new PVector[(knots.length-6)*2-2];
  controlC=new double[controls.length][];
  for(int i=0;i<controls.length;i++) controlC[i]=new double[3];
  
  //przypisywanie wartości polarnych punktom kontrolnym krzywej
  for(int i=0;i<knots.length-6+2;i++)
    for(int k=0;k<3;k++)
      pointC[i][k]=knots[i+k];
      
  //przypisywanie wartości polarnych punktom kontrolnym i kotwicom bezierów 
  for(int i=0;i<anchors.length-1;i++)
  {                                //dla fragmentu krzywej w przedziale [1,2]
    controlC[2*i][0]=knots[i+3];     //[1,1,2]
    controlC[2*i][1]=knots[i+3];
    controlC[2*i][2]=knots[i+4];
    controlC[2*i+1][0]=knots[i+3];   //[1,2,2]
    controlC[2*i+1][1]=knots[i+4];
    controlC[2*i+1][2]=knots[i+4];
    anchorC[i][0]=knots[i+3];        //[1,1,1]
    anchorC[i][1]=knots[i+3];
    anchorC[i][2]=knots[i+3];
  }
  
  ////obliczanie współrzędnych punktów kontrolnych bezierów
  for(int i=0;i<anchors.length-1;i++)
  {
    float a,b,mult;
    a=(float)pointC[i+2][0];
    b=(float)pointC[i+3][2];
    
    mult=((float)controlC[2*i][0]-a)/(b-a);
    controls[2*i]=PVector.add(points.get(i+1),PVector.mult(PVector.sub(points.get(i+2),points.get(i+1)),mult));
    
    mult=((float)controlC[2*i+1][2]-a)/(b-a);
    controls[2*i+1]=PVector.add(points.get(i+1),PVector.mult(PVector.sub(points.get(i+2),points.get(i+1)),mult));
  }
 
  //obliczanie współrzędnych kotwic bezierów
  if(points.size()>anchors.length+2)  //jeśli w definicji krzywej (wśród jej punktów) są kotwice, zadanie jest banalne
    for(int i=0;i<knots.length-6;i++)
      anchors[i]=points.get((knots.length-6)+2+i);
  else
  {
    //w przeciwnym wypadku trzeba je znaleźć, tak jak punkty kontrolne
    anchors[0]=points.get(0);
    for(int i=1;i<anchors.length-1;i++)
    {
      float a,b,mult;
      a=(float)controlC[2*i-1][0];
      b=(float)controlC[2*i][2];
      
      mult=((float)anchorC[i][0]-a)/(b-a);
      anchors[i]=PVector.add(controls[2*i-1],PVector.mult(PVector.sub(controls[2*i],controls[2*i-1]),mult));
    }
    anchors[anchors.length-1]=points.get(points.size()-1);
  }
  
  //generowanie bezierów w oparciu o już posiadane punkty
  for(int i=0;i<anchors.length-1;i++)
    beziers.add(new Bezier(anchors[i],anchors[i+1],controls[2*i],controls[2*i+1],mat));
  return beziers;
}
