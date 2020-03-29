//66 linii
class Line extends Shape    //klasa linii, przewijająca się bardzo często w całym programie
{
  PVector a,b;  //końce linii
  
  Line(PVector a,PVector b,Material mat)
  {
    this.a=a;
    this.b=b;
    material=mat;
  }
  Line(DXFLine line,Material mat)  //konstruktor konewrtujący z DXFLine
  {
    this.a=new PVector((float)line.getStartPoint().getX(),(float)line.getStartPoint().getY());
    this.b=new PVector((float)line.getEndPoint().getX(),(float)line.getEndPoint().getY());
    material=mat;
  }
  
  void show()
  {
    stroke(material.d_color);
    line(a.x,a.y,b.x,b.y);
  }
  
  //normalna linii
  PVector normal(PVector point)
  {
    PVector line_dir=new PVector(this.b.x-this.a.x,this.b.y-this.a.y);  //kierunek linii
    line_dir.normalize();
    return line_dir.rotate(PI/2);
  }
  
  //funkcja przecięcia linii z prominiem. Jest także wykożystywana w łuku i bezierze
  //zaczerpnięta od Daniela Shiffmana ("2D raytracing")
  PVector intersect(Ray ray)
  {
    final float x1 = this.a.x; 
    final float y1 = this.a.y;
    final float x2 = this.b.x;
    final float y2 = this.b.y;
    final float x3 = ray.pos.x;
    final float y3 = ray.pos.y;
    final float x4 = ray.pos.x + ray.dir.x;
    final float y4 = ray.pos.y + ray.dir.y;

      final float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
      if (den == 0) {
        return null;
      }

      //sprawdzanie czy punkt przecięcia istnieje
      final float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
      final float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

      //znajdowanie go
      if (t > 0 && t < 1 && u > 0) {
        PVector pt = new PVector();
        pt.x = x1 + t * (x2 - x1);
        pt.y = y1 + t * (y2 - y1);
        
        if((pt.x!=this.a.x || pt.y!=this.a.y) && (pt.x!=this.b.x || pt.y!=this.b.y))
        return pt;
      } 
      
      return null;
  }
}
