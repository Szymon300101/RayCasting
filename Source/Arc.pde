//70 linii
class Arc extends Shape    //klasa łuku jako obiektu materialnego na rysunku
{
  PVector c;            //środek
  float r, start, end;  //promień, kąt początku, kąt końca
  ArrayList<Line> hitbox=new ArrayList();  //linie służące do wykrywania kolizji
  
  Arc(float cx,float cy,float r,float start,float end,Material mat)
  {
    this.c=new PVector(cx,cy);
    this.r=r;
    this.start=start;
    this.end=end;
    material=mat;
    
    //generowanie zespołu linii przybliżających łuk
    //linie układają się w kratownicę, żeby było nieco dokładniej
    PVector x=PVector.fromAngle(start).setMag(r);
    PVector y=PVector.fromAngle(start+PI/PRECISION).setMag(r);
    for(float f=start+PI/PRECISION*2;f<=end;f+=PI/PRECISION)
    {
      PVector z=PVector.fromAngle(f).setMag(r);
      hitbox.add(new Line(PVector.add(x,c),PVector.add(z,c),material));
      x=y;
      y=z;
    }
    hitbox.add(new Line(PVector.add(x,c),PVector.add(PVector.fromAngle(end).setMag(r),c),material));
  }
  
  void show()
  {
    stroke(material.d_color);
    arc(c.x,c.y,r*2,r*2,start,end);  //rysowanie łuku (processing przyjmuje średnicę)
    
    //for(Line l:hitbox) l.show();
  }
  
  //normalna łuku, czyli praktycznie promień
  PVector normal(PVector point)
  {
    return PVector.sub(point,c).normalize();
  }
  
  //zwraca punkt przecięcia z promieniem (jeśli brak - null)
  PVector intersect(Ray ray)
  {
    //iteruje się przez wsystkie linie hitboxu i szuka przecięcia najbliższego źródła promienia
    PVector best=new PVector();
    boolean first=true;  //czy już został znaleziony jakiś promień
    for(int w=0;w<hitbox.size();w++)
    {
      PVector point=hitbox.get(w).intersect(ray); //zwraca przecięcie promienia z linią
      
      if(point!=null && (first || ray.pos.dist(best)>ray.pos.dist(point)))   //porównuje je z już znalezionymi punktami
      {                                                                          //aby znaleźć najbliższy
        best=point;
        first=false;
      }       
    }
    if(!first)    //jeżeli zostało znalexione jakieś przcięcie, znajdź dokładny punkt na okręgu (minimalizuje błąd wynikający z przybliżenia łuku)
     {
      float d=0;    //jeżeli promień pada od wewnątrz łuku, punkt przeba przesunąć nieco do środka, żeby ominąć linie hitboxu (będące wewnątrz łuku)
      if(PVector.add(ray.dir,this.normal(best)).magSq()>PVector.sub(ray.dir,this.normal(best)).magSq())
        d=r-(r*cos(PI/PRECISION));     //maksymalna odległość lini hitboxu od łuku
      best.sub(c);
      best=PVector.fromAngle(best.heading()).mult(r-d*1.1);  //ustalanie dokładnej pozycji na której znajduje się punkt przecięcia
      best.add(c);    //ustalenie bezwzględnej pozycji tego punktu
      return best;
     }else
        return null;
    }
}
