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

  def circle(pos_x: Double, pos_y: Double, radius: Double, a: Color, b: Color): Image =
    (x, y) =>
      val squared = math.pow(pos_x - x, 2) + math.pow(pos_y - y, 2)
      if squared <= radius * radius then
        a
      else
        b
      
  def rectangle(left: Double, right: Double, top: Double, bottom: Double): Mask =
    (x, y) => 
      if x >= left && x <= right then
        if y <= top && y >= bottom then
          true
        else 
          false
      else
        false
  def halfplane(a: Double, b: Double, c: Double): Mask = 
    (x, y) =>
      if a * x + b * y < c then 
        true
      else 
        false
  def stripes(f: (a: Double, b: Double) => Double, width: Double): Mask = 
    (x, y) =>
      val res = f(x, y)
      if math.floor(res / width) % 2 == 0 then
        true
      else
        false

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
    
  def brighten(amount: Int): Effect = 
    (img) =>
      (x, y) =>
        val c = img(x, y)
        val r = math.min(c.r + amount, 255)
        val g = math.min(c.g + amount, 255)
        val b = math.min(c.b + amount, 255)
        Color(r, g, b)

  def darken(amount: Int): Effect =
    (img) => 
      (x, y) => 
        val c = img(x, y)
        val r = math.max(c.r - amount, 0)
        val g = math.max(c.g - amount, 0)
        val b = math.max(c.b - amount, 0)
        Color(r, g, b)
  // TODO: Compose effects IN LIST ORDER; e.g. List(f,g)(img) == g(f(img)).
  def pipeline(effects: List[Effect]): Effect = 
    (img: Image) =>  
        effects.foldLeft(img)( 
          (acc: Image, item: Effect) => item(acc)
        )
}