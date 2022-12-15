package serialization;

import java.util.ArrayList;

import org.kabeja.dxf.Bounds;

import light.DotSource;
import light.SunSource;
import shape.Arc;
import shape.Bezier;
import shape.Line;
import shape.Shape;
import util.Material;
import util.PVector;
import window.AppFrame;
import window.settings.Settings;

public class AppStateModel {

	public ArrayList<Line> wallsLines=new ArrayList<Line>();
	public ArrayList<Arc> wallsArcs=new ArrayList<Arc>();
	public ArrayList<Bezier> wallsBeziers=new ArrayList<Bezier>();
	public Bounds dxfBounds;
	public double PERC_LIGHT = 2;
	public double MIN_POWER = 0.01;
	public int REFLECTIONS = 30;
	
	//source
	public int r_num;
	public String sourceType;
	public PVector dotPos;
 	public double sunDir;
	
	public AppStateModel(AppFrame frame)
	{
		for (Shape shape : frame.walls) {
			if(shape instanceof Line)
				wallsLines.add((Line)shape);
			else if(shape instanceof Arc)
				wallsArcs.add((Arc)shape);
			else if(shape instanceof Bezier)
				wallsBeziers.add((Bezier)shape);
		}
		
		dxfBounds = frame.graphic.dxfBounds;
		
		for (int i = wallsLines.size()-1; i > 0; i--) {
			if(wallsLines.get(i).material.name.equals("_border_"))
				wallsLines.remove(i);
		}
		
		PERC_LIGHT = Settings.PERC_LIGHT;
		MIN_POWER = Settings.MIN_POWER;
		REFLECTIONS = Settings.REFLECTIONS;
		
		if(frame.sources.get(0) instanceof SunSource)
		{
			r_num = frame.sources.get(0).r_num;
			sunDir = frame.sources.get(0).getDir();
			sourceType = "sun";
		}
		else if(frame.sources.get(0) instanceof DotSource)
		{
			r_num = frame.sources.get(0).r_num;
			dotPos = frame.sources.get(0).getPos();
			sourceType = "dot";
		}
		
	}
	
	public void InsertToFrame(AppFrame frame)
	{
		ArrayList<Material> materials=new ArrayList<Material>();
		
		
		frame.walls.addAll(wallsLines);
		frame.walls.addAll(wallsArcs);
		frame.walls.addAll(wallsBeziers);
	    frame.graphic.getScale(dxfBounds);
    	for (Shape shape : frame.walls)
    	{
			if(!materials.stream().anyMatch(o -> shape.material.name.equals(o.name)))
			{
				Material mat = new Material(shape.material.name, shape.material.difusion, shape.material.reflect, shape.material.d_color);
				mat.isMesuring = shape.material.isMesuring;
				shape.material = mat;
				materials.add(mat);
			}else
			{
				for (Material mat : materials) {
				    if (mat.name.equals(shape.material.name)) {
				        shape.material = mat;
				    }
				}
			}
		}
    	frame.materials = materials;
	    frame.walls.addAll(frame.graphic.makeBorders());
	    
	    if(sourceType.equals("sun"))
	    {
	    	SunSource source = new SunSource(frame, r_num, 1);
	    	source.setDir(sunDir);
	    	frame.sources.add(source);
	    }
	    else
	    {
	    	DotSource source = new DotSource(frame, r_num);
	    	source.setPos(dotPos);
	    	frame.sources.add(source);
	    }
	    
	    Settings.PERC_LIGHT = PERC_LIGHT;
	    Settings.MIN_POWER = MIN_POWER;
	    Settings.REFLECTIONS = REFLECTIONS;
	}
}
