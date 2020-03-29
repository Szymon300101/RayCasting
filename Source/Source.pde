//103 linii
class Source  //nadklasa źródeł światła
{
  String type;  //jakiego rodzaju jest to źródło (Dot,Sun)
  int r_num;        //ilość promieni
  int start_num=1;  //od jakiego numeru zaczynają promienie (0 jeśli pierwsze pokolenie ma ignorować pierwszą ścianę)
  ArrayList<Ray> rays;  //lista promieni wychodzących ze żródła
  float max_power=1;    //energia startowa źródła
  
  //każde źródło jest wyświetlane tak samo
  void show(ArrayList<Shape> walls)
  {
    if(rays!=null)
      for(int i=0;i<r_num;i++)
      {
        rays.get(i).number=start_num;
        rays.get(i).show(walls);
      }
  }
  
  //ustawia pozycję źródła
  void setPos(PVector pos)
  {}
}

//Źródło punktowe
class DotSource extends Source
{
  PVector pos;
  
  DotSource(int num, float max)
  {
    type="Dot";
    r_num=num;
    max_power=max;
    this.pos=new PVector(0,0);
    rays=new ArrayList();
    for(int i=0;i<r_num;i++) rays.add(new Ray(pos,PVector.fromAngle(TWO_PI/r_num*i+1),max_power));  //inicjalizacja promieni
  }
  DotSource(int num)  //konstruktor z domyśną wartością mocy
  {
    this(num,1);
  }
 
  //zmienia pozycję, oraz pozycję wszystkich promieni
  void setPos(PVector pos)
  {
    this.pos=pos;
    for(int i=0;i<r_num;i++)
      rays.get(i).setPos(pos);
  }
}

//źródło typu słońce
//jest ono linią, z której padają równoległe promienie (prostopadłe do linii)
//linia obraca się wokół środka ekranu, tuż poza jego obrębem
class SunSource extends Source
{
  PVector base;  //wektor 'wodzący' linię promieni. między środkiem ekranu z środkiem lini
  float dir;     //kierunek z którego padają promienie
  
  SunSource(int num,float max)
  {
    type="Sun";
    r_num=num;
    max_power=max;
    start_num=0;
    dir=0;
    this.base=PVector.fromAngle(dir);
    this.base.mult(sqrt(width*width+height*height)/2/scale);  //ustalanie promienia wodzącego na połowę przekątnej ekranu
    PVector increment=base.copy().rotate(PI/2).div(r_num/2);  //odlegość między promieniami (cały szereg ma długość przekatnej - 2*base)
    rays=new ArrayList();
    for(int i=0;i<r_num/2;i++)   //pierwsza połowa szeregu
      rays.add(new Ray(PVector.add(PVector.add(center,base),PVector.mult(increment,i)),base.copy().normalize().mult(-1),max_power));
    increment.mult(-1);
    for(int i=r_num/2;i<r_num;i++)   //druga połowa szeregu
      rays.add(new Ray(PVector.add(PVector.add(center,base),PVector.mult(increment,i-r_num/2+1)),base.copy().normalize().mult(-1),max_power));
  }
  SunSource(int num) //konstruktor z domyśną wartością mocy
  {
    this(num,1);
  }
  
  //obracanie żródła tak by podany punkt był na wektorze 'base' (dzięki temu można obracać myszką)
  void setPos(PVector point)
  {
    dir=PVector.sub(point,center).heading();
    base.rotate(dir-base.heading());  //obracanie bazy
    
    //ustalanie nowych kierunków i pozycji wszystkich promieni (zasadniczo budowanie źródła od nowa)
    PVector increment=base.copy().rotate(PI/2).div(r_num/2);
    for(int i=0;i<r_num/2;i++) 
    {
      rays.get(i).setPos(PVector.add(PVector.add(center,base),PVector.mult(increment,i)));
      rays.get(i).setDir(base.copy().normalize().mult(-1).heading());
    }
    increment.mult(-1);
    for(int i=r_num/2;i<r_num;i++)
    {
      rays.get(i).setPos(PVector.add(PVector.add(center,base),PVector.mult(increment,i-r_num/2+1)));
      rays.get(i).setDir(base.copy().normalize().mult(-1).heading());
    }
  }
}
