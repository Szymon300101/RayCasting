package util;
//240 linii
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFLineType;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFSpline;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.DXFArc;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.SplinePoint;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParserBuilder;

import shape.Arc;
import shape.Bezier;

public class DXFProcessor
{
  String path;
  DXFDocument doc;
  private List<DXFLayer> layers;
  
  //konstruktor wczytuje warstwy dokumetu do programu
  public DXFProcessor(String file)
  {
    this.path=file;
    this.setLayers(new ArrayList<>());
    Parser parser = ParserBuilder.createDefaultParser();
    try 
    {
      parser.parse(file, DXFParser.DEFAULT_ENCODING);
      this.doc = parser.getDocument();
      
      if(doc==null) return;
      Iterator<DXFLayer> Literator=doc.getDXFLayerIterator();
      while(Literator.hasNext())
        this.getLayers().add(Literator.next());
    } catch (ParseException e) 
    {
      e.printStackTrace();
    }
  }
  
  //zwraca granice dokumentu (obliczane przez bibliotekę)
  public Bounds getBounds()
  {
    if(doc!=null)
      return doc.getBounds();
    else
      return new Bounds(1,-1,1,-1);
  }
  
  //wypisanie informacji o warstwach i obiektach we wczytywanym pliku  
  public void getInfo()
  {   
    if(doc==null)
    {
      System.out.println("ERROR: no DXF file");
      return;
    }
      for(int i=0;i<getLayers().size();i++)
      {
        DXFLayer layer=getLayers().get(i);
        System.out.println("Layer: " + layer.getName());
        Iterator<String> ETiterator=layer.getDXFEntityTypeIterator(); //pobiera wszystkie typy obiektów występunjace na warstwie
        while(ETiterator.hasNext())
        {
          String EntityType=ETiterator.next();
          List<DXFEntity> entities = layer.getDXFEntities(EntityType);
          if(entities!=null)
          {
          System.out.print("      " + entities.size());    //wypisuje ile ich jest
          System.out.println(" --> " + EntityType);
          }
        }
      }
  }
  
  //wczytywanie linii (zwraca DXFLine nie Line)
  public ArrayList<DXFLine> getLines(DXFLayer layer) 
  {
    ArrayList<DXFLine> lines = new ArrayList<>();
    
    //polilinie
    List<DXFPolyline> plines = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_POLYLINE);
    if(plines!=null)
      for(int i=0;i<plines.size();i++)
      {
        for(int v=0;v<plines.get(i).getVertexCount();v++)
        {
          if(plines.get(i).getVertex(v).getBulge()==0)  //jeżeli odcinek nie jest łukiem, zamienia go na linię
          {
            DXFLine line=new DXFLine();
            line.setStartPoint(plines.get(i).getVertex(v).getPoint());
            if(v+1<plines.get(i).getVertexCount())
              line.setEndPoint(plines.get(i).getVertex(v+1).getPoint());
            else if(plines.get(i).isClosed())
              line.setEndPoint(plines.get(i).getVertex(0).getPoint());    //jeżeli polilinia jest zamknięta, zamyka ją
            else break;
            lines.add(line);
          }
        }
      }
      
    //pojedyńcze linie  
    List<DXFLine> slines = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);
    if(slines!=null)
      for(int i=0;i<slines.size();i++)
        lines.add(slines.get(i));
    
    return lines;
  }
  
  //wczytywanie łuków
  public ArrayList<Arc> getArcs(DXFLayer layer, Material mat) 
  {
    ArrayList<Arc> arcs = new ArrayList<>();
    
    //łuki będące częścią polilinii
    List<DXFPolyline> plines = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_POLYLINE);
    if(plines!=null)
      for(int i=0;i<plines.size();i++)
      {
        for(int v=0;v<plines.get(i).getVertexCount();v++)
        {
          if(plines.get(i).getVertex(v).getBulge()!=0)      //jeżeli odcinek polilini jest łukiem, zamienia go na łuk
          {
        	  double cx;
        	  double cy;
        	  double r;
        	  double start;
        	  double angle;
            
            DXFVertex v1=plines.get(i).getVertex(v);  //początek łuku
            DXFVertex v2;                             //koniec łuku
            if(v+1<plines.get(i).getVertexCount())
              v2=plines.get(i).getVertex(v+1);
            else if(plines.get(i).isClosed())
              v2=plines.get(i).getVertex(0);
            else break;
            
            //obliczanie promienia łuku
            double len=Math.sqrt(Math.pow(v2.getX() - v1.getX(), 2) + Math.pow(v2.getY() - v1.getY(), 2));  //odległosć między punktami
            double h = v1.getBulge() * len / 2;
            r=Math.abs(h / 2 + Math.pow(len, 2) / (8 * h));
            
            //znajdowanie środka i kątów łuku
            PVector a=new PVector( v1.getX(), v1.getY());  //początek łuku (jako PVector)
            PVector b=new PVector( v2.getX(), v2.getY());  //koniec łuku  (jako PVector)
            PVector dist=PVector.sub(b,a);  //wektor odpowiadający odległości międzyy punktami
            
            if(v1.getBulge()<0)  //jeżeli łuk kręci w lewo (środek jest po lewej)
            {
              dist.rotate(- Math.acos((len/2)/r));
              dist.setMag( r);
              PVector s=PVector.add(a,dist);
              cx=s.x;
              cy=s.y;
              start=PVector.sub(a,s).heading();
              angle=PVector.sub(b,s).heading()-start;
            }
            else         //jeżeli łuk kręci w prawo (środek jest po prawej)
            {
              dist.rotate( Math.acos((len/2)/r));
              dist.setMag( r);
              PVector s=PVector.add(a,dist);
              cx=s.x;
              cy=s.y;
              start=PVector.sub(a,s).heading();
              angle=PVector.sub(b,s).heading()-start;
            }
            //System.out.println(v1.getBulge());
            
            if(Math.abs(v1.getBulge())<1.1)
            {
	          	if(Math.abs(angle)>Math.PI)
	          	{
	          		if(angle>0)
	          			angle=-((Math.PI*2)-angle);
	          		else
	          			angle=((Math.PI*2)+angle);
	          	}
            }else
            {
          	  	if(Math.abs(angle)<Math.PI)
            	{
            		if(angle>0)
            			angle=-((Math.PI*2)-angle);
            		else
            			angle=((Math.PI*2)+angle);
            	}
            }

            start=-start/Math.PI*180;
            angle=-angle/Math.PI*180;

            arcs.add(new Arc(cx,cy,r,start,angle,mat));
          }
        }
      }
      
    //dodawanie okręgów jako łuków (bo czemu nie)  
    List<DXFCircle> circles = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE);
    if(circles!=null)
      for(int i=0;i<circles.size();i++)
      {
        DXFCircle circle=circles.get(i);
        double cx=circle.getCenterPoint().getX();
		double cy=circle.getCenterPoint().getY();
		double r=circle.getRadius();
		double start=0;
		double angle=360;
        arcs.add(new Arc(cx,cy,r,start,angle,mat));
      }
    
    //dodawanie samodzilnych łuków
    List<DXFArc> sArcs = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_ARC);
    if(sArcs!=null)
      for(int i=0;i<sArcs.size();i++)
      {
    	  System.out.println("GotArc");
	    double cx=sArcs.get(i).getCenterPoint().getX();
  		double cy=sArcs.get(i).getCenterPoint().getY();
  		double r=sArcs.get(i).getRadius();
  		double start=sArcs.get(i).getStartAngle();
  		double angle=sArcs.get(i).getTotalAngle();
  		

        start=360-start;
        angle=-angle;
  	
          arcs.add(new Arc(cx,cy,r,start,angle,mat));
      }
    
    return arcs;
  }
  
  //wczytywanie krzywych
  public ArrayList<Bezier> getBeziers(DXFLayer layer,Material mat) 
  {
     ArrayList<Bezier> beziers = new ArrayList<Bezier>();
     List<DXFSpline> splines = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_SPLINE);
     if(splines!=null)
    	 for(DXFSpline s: splines)
		 	beziers.addAll(splitSpline(s,mat));
     return beziers;
  }

	public List<DXFLayer> getLayers() {
		return layers;
	}
	
	public void setLayers(List<DXFLayer> layers) {
		this.layers = layers;
	}
	
	//funkcja rozbijająca B-spline 3 stopnia na krzywe beziera
	//jest moją autorską implementacją algorytmu Boehma (bardzo łopatologiczną)
	//http://web.archive.org/web/20120227050519/http://tom.cs.byu.edu/~455/bs.pdf
	ArrayList<Bezier> splitSpline(DXFSpline spline,Material mat)
	{
	  ArrayList<Bezier> beziers=new ArrayList<Bezier>();
	  
	  ArrayList<PVector> points = new ArrayList<PVector>();    //punkty kontrolne krzywej
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
	  
	  System.out.println("Degree: " + spline.getDegree());
	  System.out.println("Knots: " + knots.length);
	  System.out.println("Points: " + points.size());
	  
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
	  {
		  PVector arr[]= {anchors[i],controls[2*i],controls[2*i+1],anchors[i+1]};
		  beziers.add(new Bezier(arr,mat));
	  }
	  return beziers;
	}
}
