//170 linii
import org.kabeja.dxf.DXFSpline;
import org.kabeja.dxf.helpers.SplinePoint;
import java.util.List;
import java.util.Iterator;

DXFProcessor reader;    //dekoder plików DXF
GUI gui;
ArrayList<Shape> walls=new ArrayList();          //wszystkie obiekty pobrane z pliku DXF
ArrayList<Source> lights=new ArrayList();        //źródła światła dodawane przez urzytkownika
ArrayList<Material> materials = new ArrayList();    //materiały przypisane warstwom pliku DXF

//główne parametry programu
float MIN_POWER = 0.2;        //energia promienia przy której gaśnie
float PERC_LIGHT = 2;         //parametr definiujący jak energia promienia przekłada się na jasność (e^(1/PERC_LIGHT)=j)
int REFLECTIONS = 10;         //liczba promieni wychodzących z jednego, rozproszonego na powieszchni
final int START_RAYS = 80;    //domyślna ilośc promieni wychodzących ze źródła światła
final int PRECISION = 100;    //dokładnośc z jaką liczone są odbicia od łuków i krzywych (łuk dzielony jest na PRECISION prostych)

//parametry określające skalę dokumentu
float scale=1;      //skala
float transX=0;     //przesunięcie od 0 w X
float transY=0;     //przesunięcie od 0 w Y
float strW=1;       //grubość lini
PVector center;     //środek ekranu

//działanie programu
int framerate;
boolean lock=false;  //jeżeli true - promienie nie odbijają się (program działa szybciej). przełączne spacją


void setup()
{
  fullScreen(P2D);
  frameRate(1000);
  stroke(255);
  background(0);
  noFill();
  gui=new GUI(this);
}

void draw()
{
  long framestart=millis();
  background(0);
  noFill();
  pushMatrix();
  if(reader!=null)    //jeżeli został wczytany plik
  {
    setScale();           //zastosowywanie skali dokumentu
    
    for(Source s:lights)  //wyświetlnie promieni światła (obliczanie wszystkich odbić) 
      s.show(walls);
      
    for(Shape w:walls)    //wyświetlanie obiektów
      w.show();
      
    
    if(gui.actions>=6)    //zapis ustawień co 6 działań urzytkownika (naciśnięcie klawisza klawiatury lub przycisku myszki)
    {
      thread("saveJSON");
      gui.actions=0;
    }
  }
  popMatrix();
  framerate=int(1000/( millis()-framestart+1));    //obliczanie wartości FPS
  gui.show();      //obsługa interfejsu urzytkownika
}

//funkcja wczytująca dane z pliku DXF i inicjalizująca główne funkcje programu (wywoływana z GUI)
void loadFile(File selection)
{
  if(selection==null) return;
  reader=new DXFProcessor(selection.getPath());  //wczytywanie pliku do dekodera
  //reader.getInfo();  //wypisanie informacji o warstwach i obiektach we wczytywanym pliku  
  getScale(reader.getBounds());            //ustalenie parametrów skali w oparciu o obiekty znajdujące się w pliku
  
  //wczytywanie wszystkich obiektów z poszczególnych warstw
  for(DXFLayer layer: reader.layers)
  {
    //tworzenie materiału dla warstwy
    Material layer_mat=new Material(layer.getName(),1,1,255);
    materials.add(layer_mat);
    
    //wczytywanie linii
    ArrayList<DXFLine> lines=reader.getLines(layer);
    if(lines!=null)
    for(int i=0;i<lines.size();i++)
      walls.add((Shape) new Line(lines.get(i),layer_mat));
      
    //wczytywanie łuków  
    ArrayList<DXFArc> arcs=reader.getArcs(layer);
    if(arcs!=null)
    for(DXFArc a: arcs)
    {
      double x=a.getCenterPoint().getX();
      double y=a.getCenterPoint().getY();
      Arc arc=new Arc((float)x,(float)y,(float)a.getRadius(),(float)a.getStartAngle(),(float)a.getEndAngle(),layer_mat);
      walls.add((Shape) arc);
    }
    
    //wczytywanie krzywych (B-spline)
    ArrayList<DXFSpline> splines=reader.getSplines(layer);
    if(splines!=null)
    for(int i=0;i<splines.size();i++)
    {
      ArrayList<Bezier> beziers=splitSpline(splines.get(i),layer_mat);
      if(beziers!=null)
      for(Bezier b: beziers)
        walls.add((Shape) b);
    }
  }
  
  //tworzenie obramowania ekranu z lini, aby promienie miały na czym się zatrzymywać
  ArrayList<Line> borders= makeBorders();
  for(Line l:borders)
    walls.add((Shape) l);
}


//ustalenie skali i przesunięcia w oparciu o granice pliku DXF
void getScale(Bounds b)
{
  //bardzo brzydkie i skomplikowane obliczenia mające na celu ładnie ułożyć plik na ekranie niezależnie od jego wielkości
  if((b.getMaximumX()-b.getMinimumX())/width>(b.getMaximumY()-b.getMinimumY())/height)
  {
    double b_width=b.getMaximumX()-b.getMinimumX();
    scale=(float)(width/b_width)*0.9;
    transX=(float)(-b.getMinimumX()+b_width*0.0495);
    transY=(float)(-b.getMinimumY()+b_width*0.0495);
    strW=(float)(1/((width/b_width)*0.9));
  }else
  {
    double b_height=b.getMaximumY()-b.getMinimumY();
    scale=(float)(height/b_height)*0.9;
    transX=(float)(-b.getMinimumX()+b_height*0.0495);
    transY=(float)(-b.getMinimumY()+b_height*0.0495);
    strW=(float)(1/((height/b_height)*0.9));
  }
  center=new PVector(width/scale/2-transX,height/scale/2-transY);
}

//zastosowywanie wcześniej obliczonej skali i przekształceń
void setScale()
{
  scale(scale);
  translate(transX,transY);
  strokeWeight(strW);
}

//generowanie obramowania ekranu w postaci linii
ArrayList<Line> makeBorders()
{
  ArrayList<Line> border=new ArrayList();
  Material black=new Material("black",1,0,0); //idealnie pochłaniający materiał
  
  //ustalenie współrzędnych rogów ekranu
  setScale();
  float left=-screenX(0,0)/scale;
  float right=(width-screenX(0,0))/scale;
  float top=-screenY(0,0)/scale;
  float bottom=(height-screenY(0,0))/scale;
  
  //tworzenie linii
  border.add(new Line(new PVector(left,top),new PVector(right,top),black));
  border.add(new Line(new PVector(right,top),new PVector(right,bottom),black));
  border.add(new Line(new PVector(right,bottom),new PVector(left,bottom),black));
  border.add(new Line(new PVector(left,bottom),new PVector(left,top),black));
  
  return border; 
}
