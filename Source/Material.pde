//15 linii
class Material    //materiał przypisywany do każdego obiektu w projekcie
{
  float difusion,reflect;    //rozpraszanie światła przy odbiciu; ilość światła oddawana po odbiciu
  color d_color;    //kolor wyświetlnia
  String name;    //nazwa warstwy skojażonej z kolorem
  
  Material(String name,float difusion, float reflect, color col)
  {
    this.name=name;
    this.difusion=difusion;
    this.reflect=reflect;
    this.d_color=col;
  }
}
