error id: file://<WORKSPACE>/src/main/scala/studio/image/StudentWork.scala:
file://<WORKSPACE>/src/main/scala/studio/image/StudentWork.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1126
uri: file://<WORKSPACE>/src/main/scala/studio/image/StudentWork.scala
text:
```scala
package studio.image

/** Implement these independently or with documented resources as allowed.
  * Exception: HumanCore.composeLayers is HUMAN-ONLY (no GenAI).
  */
object StudentWork {
  // TODO: Return one of two colors in an alternating grid.
  def checkerboard(columns: Int, rows: Int,
                   a: Color, b: Color): Image = 
    (x: Double, y: Double) => 
      val x_row = math.ceil(x * rows).toInt
      val y_row = math.ceil(y * columns).toInt
      if (x_row + y_row) % 2 == 0 then
        a
      else
        b

  // TODO: Function-valued sets. Supply at least two mask combinations.
  def union(a: Mask, b: Mask): Mask = 
    (x: Double, y: Double) => 
      a(x, y) || b(x, y)
  def intersect(a: Mask, b: Mask): Mask = 
    (x: Double, y: Double) =>
      a(x, y) && b(x, y)

  // TODO: Implement THREE pure Image => Image effects.
  def mirrorHorizontal: Effect = 
    (img: Image) => (x, y) =>
      img(-x, y) 
  def invertColors: Effect = 
    (img) => 
      (x, y) =>
        val color = img(x, y)
        Color(255 - color.r, 255 - color.g, 255 - color.b)
    
  def brighten(amount: Int): Effec@@t = 
    

  // TODO: Compose effects IN LIST ORDER; e.g. List(f,g)(img) == g(f(img)).
  def pipeline(effects: List[Effect]): Effect = ???
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 