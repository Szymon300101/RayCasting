//240 linii
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import processing.*;
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
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParserBuilder;
import processing.core.*;

public class DXFProcessor
{
  String path;
  DXFDocument doc;
  List<DXFLayer> layers;
  
  //konstruktor wczytuje warstwy dokumetu do programu
  DXFProcessor(String file)
  {
    this.path=file;
    this.layers=new ArrayList<>();
    Parser parser = ParserBuilder.createDefaultParser();
    try 
    {
      parser.parse(file, DXFParser.DEFAULT_ENCODING);
      this.doc = parser.getDocument();
      
      if(doc==null) return;
      Iterator<DXFLayer> Literator=doc.getDXFLayerIterator();
      while(Literator.hasNext())
        this.layers.add(Literator.next());
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
      for(int i=0;i<layers.size();i++)
      {
        DXFLayer layer=layers.get(i);
        System.out.println("Layer: " + layer.getName());
        Iterator<String> ETiterator=layer.getDXFEntityTypeIterator(); //pobiera wszystkie typy obiektów występunjace na warstwie
        while(ETiterator.hasNext())
        {
          String EntityType=ETiterator.next();
          List entities = layer.getDXFEntities(EntityType);
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
  
  //wczytywanie łuków (zwraca DXFArc nie Arc)
  public ArrayList<DXFArc> getArcs(DXFLayer layer) 
  {
    ArrayList<DXFArc> arcs = new ArrayList<>();
    
    //łuki będące częścią polilinii
    List<DXFPolyline> plines = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_POLYLINE);
    if(plines!=null)
      for(int i=0;i<plines.size();i++)
      {
        for(int v=0;v<plines.get(i).getVertexCount();v++)
        {
          if(plines.get(i).getVertex(v).getBulge()!=0)      //jeżeli odcinek polilini jest łukiem, zamienia go na łuk
          {
            DXFArc arc=new DXFArc();
            
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
            double r=Math.abs(h / 2 + Math.pow(len, 2) / (8 * h));
            arc.setRadius(r);
            
            //znajdowanie środka i kątów łuku
            PVector a=new PVector((float)v1.getX(),(float)v1.getY());  //początek łuku (jako PVector)
            PVector b=new PVector((float)v2.getX(),(float)v2.getY());  //koniec łuku  (jako PVector)
            PVector dist=PVector.sub(b,a);  //wektor odpowiadający odległości międzyy punktami
            double a1,a2;  //kąty początka i końca łuku 
            if(v1.getBulge()<0)  //jeżeli łuk kręci w lewo (środek jest po lewej)
            {
              dist.rotate(-(float)Math.acos((len/2)/r));
              dist.setMag((float)r);
              PVector s=PVector.add(a,dist);
              arc.setCenterPoint(new Point(s.x,s.y,0));
              a1=PVector.sub(s,a).heading();
              a2=PVector.sub(s,b).heading();
            }
            else         //jeżeli łuk kręci w prawo (środek jest po prawej)
            {
              dist.rotate((float)Math.acos((len/2)/r));
              dist.setMag((float)r);
              PVector s=PVector.add(a,dist);
              arc.setCenterPoint(new Point(s.x,s.y,0));
              a1=PVector.sub(a,s).heading();
              a2=PVector.sub(b,s).heading();
            }
            
            //w processingu początek musi być większym kątem niż koniec
            if(a1<a2)
            {
              arc.setStartAngle(a1);
              arc.setEndAngle(a2);
            }else
            {
              arc.setStartAngle(a2+Math.PI);
              arc.setEndAngle(a1+Math.PI);
            }
            
            arcs.add(arc);
          }
        }
      }
      
    //dodawanie okręgów jako łuków (bo czemu nie)  
    List<DXFCircle> circles = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE);
    if(circles!=null)
      for(int i=0;i<circles.size();i++)
      {
        DXFCircle circle=circles.get(i);
        DXFArc arc=new DXFArc();
        arc.setCenterPoint(circle.getCenterPoint());
        arc.setRadius(circle.getRadius());
        arc.setStartAngle(Math.PI*2);
        arc.setEndAngle(0);
        arcs.add(arc);
      }
    
    //dodawanie samodzilnych łuków
    List<DXFArc> sArcs = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_ARC);
    if(sArcs!=null)
      for(int i=0;i<sArcs.size();i++)
      {
        arcs.add(sArcs.get(i));
        double a1=sArcs.get(i).getStartAngle()*Math.PI/180;  //ich kąty trzeba przekonwertować z stopni na radiany
        double a2=sArcs.get(i).getEndAngle()*Math.PI/180;
        
        //w processingu początek musi być większym kątem niż koniec
        if(a1<a2)
        {
          arcs.get(arcs.size()-1).setStartAngle(a1);
          arcs.get(arcs.size()-1).setEndAngle(a2);
        }else
        {
          arcs.get(arcs.size()-1).setStartAngle(a2+Math.PI);
          arcs.get(arcs.size()-1).setEndAngle(a1+Math.PI);
        }
      }
    
    return arcs;
  }
  
  //wczytywanie krzywych (ich rozbijaniem zajmuje się funkcja splitSpline()
  public ArrayList<DXFSpline> getSplines(DXFLayer layer) 
  {
     ArrayList<DXFSpline> splines = new ArrayList<>();
     List<DXFSpline> sp = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_SPLINE);
     if(sp!=null)
       splines.addAll(sp);
     return splines;
  }
}
