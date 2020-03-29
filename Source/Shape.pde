//16 linii
class Shape  //nadklasa wszystkich kształtów, dzięki niej można iterować się przez krztałty, niezależnie od tego jaką są figurą
{
  Material material;  //każdy krztałt ma materiał
  
  //3 klasy które każdy krztałt nadpisuje
  
  void show()
  {}
  
  PVector normal(PVector point)
  {return null;}

  PVector intersect(Ray ray)
  {return null;}
}
