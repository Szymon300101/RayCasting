//444 linii
//nie skomentowanie bo mi się nie chce
import controlP5.*;

class GUI
{
  ControlP5 cp5;
  
  float tolerance;
  byte state=1;
  byte state_settings=0;
  byte state_materials=0;
  byte state_sources=0;
  float unit;
  boolean clicked=false;
  boolean needToLoadJSON=false;
  int actions=0;
  
  int sel_light=0;
  
  //pallete
  color col1=#85B7FC;  //menu bar
  color col2=#8BE8E8;  //button 1
  color col3=#34EBD6;  //button 2
  color col4=#7D8795;
  
  //controlP5 gross
  Textfield tf_ref;
  Textfield tf_perc;
  Textfield tf_min;
  ArrayList<Textfield> tf_mat_dif=new ArrayList();
  ArrayList<Textfield> tf_mat_ref=new ArrayList();
  ArrayList<Textfield> tf_src_rnum=new ArrayList();
  ArrayList<Textfield> tf_src_mpow=new ArrayList();
  
  GUI(PApplet applet)
  {
    unit=width/100;
    tolerance=0.6*unit;
    PFont font = createFont("Dialog.plain",unit);
    cp5 = new ControlP5(applet);
    tf_ref = cp5.addTextfield("reflections")
            .setPosition(11*unit,3*unit)
            .setSize(int(2*unit),int(1.5*unit))
            .setFont(font)
            .setLabel("")
            .hide();
    tf_perc = cp5.addTextfield("perc_light")
            .setPosition(11*unit,5*unit)
            .setSize(int(2*unit),int(1.5*unit))
            .setFont(font)
            .setLabel("")
            .hide();
    tf_min = cp5.addTextfield("min_power")
            .setPosition(11*unit,7*unit)
            .setSize(int(2*unit),int(1.5*unit))
            .setFont(font)
            .setLabel("")
            .hide();
  }
  
  void show()
  {
    PFont font = createFont("Dialog.plain",1*unit);
    textFont(font);
    switch(state)
    {
      case 0:  //menu hidden;
        if(mouseY<tolerance) state=3;
        if(mousePressed)
          if(lights.size()>sel_light)
            lights.get(sel_light).setPos(new PVector((mouseX/scale-transX),(mouseY/scale-transY)));
          else sel_light=0;
      break;
      
      case 1:    //file selection
        if(reader!=null) state=2;
        noStroke();
        fill(col1);
        rect(0,0,width,3*unit);
        if(button(35*unit,unit/2,10*unit,2*unit,"LOAD FILE"))
          selectInput("Select a file to open:", "loadFile");
        if(button(55*unit,unit/2,15*unit,2*unit,"LOAD FILE + data"))
        {
          selectInput("Select a file to open:", "loadFile");
          needToLoadJSON=true;
        }
        fill(255);
        textAlign(LEFT,CENTER);
        text("h - HELP",88*unit,1*unit);
      break;
      
      case 2:
      state=3;
      if(needToLoadJSON) loadJSON();
        for(int i=0;i<reader.layers.size();i++)
          tf_mat_dif.add(cp5.addTextfield(reader.layers.get(i).getName() + " dif")
                        .setPosition(50*unit,(4+6*i)*unit)
                        .setSize(int(2*unit),int(1.5*unit))
                        .setFont(font)
                        .setLabel("")
                        .hide());
        for(int i=0;i<reader.layers.size();i++)
          tf_mat_ref.add(cp5.addTextfield(reader.layers.get(i).getName() + " ref")
                        .setPosition(50*unit,(6+6*i)*unit)
                        .setSize(int(2*unit),int(1.5*unit))
                        .setFont(font)
                        .setLabel("")
                        .hide());
        for(int i=0;i<lights.size();i++)
        {
          tf_src_rnum.add(cp5.addTextfield("r_num" + millis()+i)
                        .setPosition(78*unit,(8.5+8*i)*unit)
                        .setSize(int(2*unit),int(1.5*unit))
                        .setFont(font)
                        .setLabel("")
                        .hide());
         tf_src_mpow.add(cp5.addTextfield("mpow" + millis()+i)
                        .setPosition(78*unit,(10.5+8*i)*unit)
                        .setSize(int(2*unit),int(1.5*unit))
                        .setFont(font)
                        .setLabel("")
                        .hide());
        }
  
      break;
      
      case 3:
        state=4;
        if(state_settings==2)
        {
          tf_ref.show();
          tf_ref.setText(str(REFLECTIONS));
          tf_perc.show();
          tf_perc.setText(str(PERC_LIGHT));
          tf_min.show();
          tf_min.setText(str(MIN_POWER));
        }
        if(state_materials==2)
        {
          for(int i=0;i<reader.layers.size();i++)
          {
            tf_mat_dif.get(i).show();
            tf_mat_dif.get(i).setText(str(materials.get(i).difusion));
            tf_mat_ref.get(i).show();
            tf_mat_ref.get(i).setText(str(materials.get(i).reflect));
          }
        }
        if(state_sources==2)
        {
          for(int i=0;i<lights.size();i++)
          {
            tf_src_rnum.get(i).show();
            tf_src_rnum.get(i).setText(str(lights.get(i).r_num));
            tf_src_mpow.get(i).show();
            tf_src_mpow.get(i).setText(str(lights.get(i).max_power));
          }
        }
      break;
      case 4:    //main menu
        if(mouseY>2*unit+tolerance) state=5;
        noStroke();
        fill(col1);
        rect(0,0,width,2*unit);
          switch(state_settings)
          {
            case 0:
              if(button(1*unit,0.2*unit,10*unit,1.6*unit,"Settings")) state_settings=1;
            break;
            
            case 1:
              state_settings=2;
              tf_ref.show();
              tf_ref.setText(str(REFLECTIONS));
              tf_perc.show();
              tf_perc.setText(str(PERC_LIGHT));
              tf_min.show();
              tf_min.setText(str(MIN_POWER));
            break;
            case 2:
               if(button(2*unit,0.2*unit,10*unit,1.6*unit,"Hide")) state_settings=3;
               fill(col4);
               if(area(0.5*unit,2*unit,13*unit,20*unit)) state=4;
               fill(255);
               textAlign(LEFT,TOP);
               text("Reflections:",unit,3*unit);
               REFLECTIONS=inputI(tf_ref.getText(),1,200);
               text("Brightness:",unit,5*unit);
               PERC_LIGHT=inputF(tf_perc.getText(),0,10);
               text("Min. Ray Pow.:",unit,7*unit);
               MIN_POWER=inputF(tf_min.getText(),0.01,1);
            break;
            case 3:
              state_settings=0;
              tf_ref.hide();
              tf_perc.hide();
              tf_min.hide();
            break;
          }
          switch(state_materials)
          {
            case 0:
              if(button(38*unit,0.2*unit,14*unit,1.6*unit,"Materials")) state_materials=1;
            break;
            
            case 1:
              state_materials=2;
              for(int i=0;i<reader.layers.size();i++)
              {
                tf_mat_dif.get(i).show();
                tf_mat_dif.get(i).setText(str(materials.get(i).difusion));
                tf_mat_ref.get(i).show();
                tf_mat_ref.get(i).setText(str(materials.get(i).reflect));
              } 
            break;
            case 2:
               int mat_num=reader.layers.size();
               if(button(38*unit,0.2*unit,14*unit,1.6*unit,"Hide")) state_materials=3;
               fill(col4);
               if(area(36.5*unit,2*unit,17*unit,6*mat_num*unit)) state=4;
               fill(255);
               textAlign(LEFT,TOP);
               for(int i=0;i<mat_num;i++)
               {
                 text(reader.layers.get(i).getName(),37*unit,(2.25+6*i)*unit);
                 text("- Diffusion:",37*unit,(4+6*i)*unit);
                 materials.get(i).difusion=inputF(tf_mat_dif.get(i).getText(),0,1);
                 text("- Reflectivity:",37*unit,(6+6*i)*unit);
                 materials.get(i).reflect=inputF(tf_mat_ref.get(i).getText(),0,10);
               }
               
            break; 
            case 3:
              state_materials=0;
              for(int i=0;i<reader.layers.size();i++)
                tf_mat_dif.get(i).hide();
              for(int i=0;i<reader.layers.size();i++)
                tf_mat_ref.get(i).hide();
            break;
          }
          
          switch(state_sources)
          {
            case 0:
              if(button(68*unit,0.2*unit,14*unit,1.6*unit,"Lights")) state_sources=1;
            break;
            
            case 1:
              state_sources=2;
              for(int i=0;i<lights.size();i++)
              {
                tf_src_rnum.get(i).show();
                tf_src_rnum.get(i).setText(str(lights.get(i).r_num));
                tf_src_mpow.get(i).show();
                tf_src_mpow.get(i).setText(str(lights.get(i).max_power));
              } 
            break;
            case 2:
               //int src_num=reader.layers.size();
               if(button(68*unit,0.2*unit,14*unit,1.6*unit,"Hide")) state_sources=3;
               fill(col4);
               if(area(66.5*unit,2*unit,17*unit,(3+8*lights.size())*unit)) state=4;
               if(button(68*unit,2.5*unit,5*unit,1.4*unit,"Add Dot"))
               {
                 tf_src_rnum.add(cp5.addTextfield("r_num" + millis())
                          .setPosition(78*unit,(8.5+8*lights.size())*unit)
                          .setSize(int(2*unit),int(1.5*unit))
                          .setFont(font)
                          .setLabel(""));
                 tf_src_mpow.add(cp5.addTextfield("mpow" + millis())
                          .setPosition(78*unit,(10.5+8*lights.size())*unit)
                          .setSize(int(2*unit),int(1.5*unit))
                          .setFont(font)
                          .setLabel(""));
                 lights.add((Source) new DotSource(START_RAYS));
                 tf_src_rnum.get(lights.size()-1).setText(str(lights.get(lights.size()-1).r_num));
                 tf_src_mpow.get(lights.size()-1).setText(str(lights.get(lights.size()-1).max_power));
               }
               if(button(77*unit,2.5*unit,5*unit,1.4*unit,"Add Sun"))
               {
                 tf_src_rnum.add(cp5.addTextfield("r_num" + millis())
                          .setPosition(78*unit,(8.5+8*lights.size())*unit)
                          .setSize(int(2*unit),int(1.5*unit))
                          .setFont(font)
                          .setLabel(""));
                  tf_src_mpow.add(cp5.addTextfield("mpow" + millis())
                          .setPosition(78*unit,(10.5+8*lights.size())*unit)
                          .setSize(int(2*unit),int(1.5*unit))
                          .setFont(font)
                          .setLabel(""));
                 lights.add((Source) new SunSource(START_RAYS));
                 tf_src_rnum.get(lights.size()-1).setText(str(lights.get(lights.size()-1).r_num));
                 tf_src_mpow.get(lights.size()-1).setText(str(lights.get(lights.size()-1).max_power));
               }
               fill(255);
               textAlign(LEFT,TOP);
               for(int i=0;i<lights.size();i++)
               {
                 fill(col4);
                 if(button(74.5*unit,(6+8*i)*unit,3*unit,1.5*unit,"Select"))
                     sel_light=i;
                 fill(col2);
                 if(sel_light==i) ellipse(67.2*unit,(6.5+8*i)*unit,0.6*unit,0.6*unit);
                 fill(255);
                 textFont(font);
                 text(lights.get(i).type,68.5*unit,(6.5+8*i)*unit);
                 if(button(78*unit,(6+8*i)*unit,2*unit,2*unit,"Del"))
                 {
                   tf_src_rnum.get(i).hide();
                   tf_src_mpow.get(i).hide();
                   tf_src_rnum.remove(i);
                   tf_src_mpow.remove(i);
                   lights.remove(i);
                   for(int r=i;r<lights.size();r++)
                   {
                     tf_src_rnum.get(r).setPosition(78*unit,(8.5+8*r)*unit);
                     tf_src_mpow.get(r).setPosition(78*unit,(10.5+8*r)*unit);
                   }
                 }
                 textAlign(LEFT,TOP);
                 text("- Rays:",67*unit,(8.5+8*i)*unit);
                 text("- Start power:",67*unit,(10.5+8*i)*unit);
                 if(button(80.5*unit,(9.25+8*i)*unit,2*unit,1.5*unit,"Ok"))
                 {
                   if(lights.get(i).type=="Dot")
                     lights.set(i,new DotSource(inputI(tf_src_rnum.get(i).getText(),1,10000),inputF(tf_src_mpow.get(i).getText(),0,1)));
                   else if(lights.get(i).type=="Sun")
                     lights.set(i,new SunSource(inputI(tf_src_rnum.get(i).getText(),1,10000),inputF(tf_src_mpow.get(i).getText(),0,1)));
                 }
               }
               
            break; 
            case 3:
              state_sources=0;
              for(int i=0;i<lights.size();i++)
              {
                tf_src_rnum.get(i).hide();
                tf_src_mpow.get(i).hide();
              } 
            break;
          }
        fill(255);
        textAlign(LEFT,CENTER);
        text("h - HELP",88*unit,1*unit);
        text(str(framerate) + "fps",98*unit,1*unit);
      break;
      case 5: 
        state=0;
        tf_ref.hide();
        tf_perc.hide();
        tf_min.hide();
        if(state_materials==2)
        {
          for(int i=0;i<reader.layers.size();i++)
            tf_mat_dif.get(i).hide();
          for(int i=0;i<reader.layers.size();i++)
            tf_mat_ref.get(i).hide();
        }
        if(state_sources==2)
        {
          for(int i=0;i<lights.size();i++)
          {
            tf_src_rnum.get(i).hide();
            tf_src_mpow.get(i).hide();
          } 
        }
      break;
    }
    clicked=false;
  }
  
  boolean area(float x,float y, float w , float h)
  {
    rect(x,y,w,h);
    if(mouseX+tolerance>x && mouseX-tolerance<x+w && mouseY+tolerance>y && mouseY-tolerance<y+h)
      return true;
    else
      return false;
  }
  
  boolean button(float x,float y, float w , float h, String name)
  {
    PFont font = createFont("DialogInput.bold",min(h*3/4,w/(name.length()*3/4)));
    textFont(font);
    noStroke();
    fill(col2);
    rect(x,y,w,h);
    textAlign(CENTER,CENTER);
    fill(255);
    text(name,x+w/2,y+h*35/100);
    if(mouseX+tolerance>x && mouseX-tolerance<x+w && mouseY+tolerance>y && mouseY-tolerance<y+h)
     {
       fill(#34EBD6);
       noStroke();
        rect(x,y,w,h);
        textAlign(CENTER,CENTER);
        fill(255);
        text(name,x+w/2,y+h*40/100);
      if(clicked)
      {
        clicked=false;
        int m=millis();
        while(millis()-m<200);
        return true;
      }
      else return false;
    } else return false;
  }
  
  float inputF(String data,float min,float max)
  {
    if(data.equals("")) return min;
    return min(max,max(min,float(data)));
  }
  int inputI(String data,int min,int max)
  {
    if(data.equals("")) return min;
    return min(max,max(min,int(data)));
  }
  
}

void mouseClicked() 
{
  gui.clicked=true;
  gui.actions++;
}

void keyPressed()
{
  //println(keyCode);
  switch(keyCode)
  {
    //case 38: max/=0.1; break; //v
    //case 40: max*=0.1; break; //^
    case 72: link("https://user.infa8lo.pl/3c1/nowacki/private/help.html"); break; //h
    //case 68: Ca+=0.001; break; //d
    //case 87: thread("saveJSON"); break; //s
    //case 83: Cb+=0.001; break; //w
    //case 91: col/=0.4; break; //]
    //case 93: col*=0.4; break; //[
    //case 10: saveFrame(); break; //ENTER
    case 32: lock=!lock; //SPACE
  }
  gui.actions++;
}
