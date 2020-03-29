//88 linii

//ładowanie ustawień z pliku JSON
public void loadJSON()
{
  String filename=reader.path.substring(0,reader.path.length()-4);  //ścieżka pliku odcztywana jest ze ścieżki pliku DXF
  File f=new File(filename + "_data.json");  //plik z ustawieniami ma zakończenie _data.json
  if(!f.exists())
  {
    println("Loading error: no data file");
    return;
  }
  JSONArray allData=loadJSONArray(f.getPath());
  JSONArray jLayers=allData.getJSONArray(0);
  JSONArray jLights=allData.getJSONArray(1);
  JSONObject jSettings=allData.getJSONObject(2);
  
  if(jLayers.size()!=materials.size())
  {
    println("Loading error: layer missmatch.");
    println(jLayers.toString());
    return;
  }
  
  for(int i=0;i<jLayers.size();i++)
  {
    JSONObject mat=jLayers.getJSONObject(i);
    materials.get(i).difusion=mat.getFloat("difusion");
    materials.get(i).reflect=mat.getFloat("reflect");
  }
  
  lights=new ArrayList();
  for(int i=0;i<jLights.size();i++)
  {
    JSONObject source=jLights.getJSONObject(i);
    String type=source.getString("type");
    int r_num=source.getInt("r_num");
    float max_power=source.getFloat("max_power");
    if(type.equals("Dot"))
      lights.add(new DotSource(r_num,max_power));
    else if(type.equals("Sun"))
      lights.add(new SunSource(r_num,max_power));
  }
  
  MIN_POWER=jSettings.getFloat("min_power");
  PERC_LIGHT=jSettings.getFloat("perc_light");
  REFLECTIONS=jSettings.getInt("reflections");
}

//zapisywanie ustawień do pliku (warstwy, źródła światła i ustawienia globalne)
public void saveJSON()
{
  String filename=reader.path.substring(0,reader.path.length()-4);
  JSONArray jLayers=new JSONArray();
  JSONArray jLights=new JSONArray();
  JSONObject jSettings=new JSONObject();
  
  for(int i=0;i<materials.size();i++)
  {
    JSONObject mat=new JSONObject();
    mat.setInt("id",i);
    mat.setString("name",materials.get(i).name);
    mat.setFloat("difusion",materials.get(i).difusion);
    mat.setFloat("reflect",materials.get(i).reflect);
    jLayers.setJSONObject(i,mat);
  }
  
  for(int i=0;i<lights.size();i++)
  {
    JSONObject light=new JSONObject();
    light.setInt("id",i);
    light.setString("type",lights.get(i).type);
    light.setInt("r_num",lights.get(i).r_num);
    light.setFloat("max_power",lights.get(i).max_power);
    jLights.setJSONObject(i,light);
  }
  
  jSettings.setFloat("min_power",MIN_POWER);
  jSettings.setFloat("perc_light",PERC_LIGHT);
  jSettings.setFloat("reflections",REFLECTIONS);
  
  JSONArray allData=new JSONArray();
  allData.setJSONArray(0,jLayers);
  allData.setJSONArray(1,jLights);
  allData.setJSONObject(2,jSettings);
  
  saveJSONArray(allData,filename + "_data.json");
  println("SAVED");
}
