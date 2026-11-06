package studio.image

/** GenAI-assisted extension (Share-Out #4, Part 4: Human-AI Teaming).
  *
  * Everything in this file was proposed by GenAI and is kept separate from
  * HumanCore. It provides four families of artistic effects:
  *
  *   1. vignette  - darkens the image toward its edges (color effect)
  *   2. pixelate  - snaps every sample to the center of a grid cell (coordinate effect)
  *   3. warp      - generic coordinate warp, with two concrete warps:
  *                  wave (sinusoidal horizontal shift) and swirl (rotation that
  *                  fades out with distance from a center)
  *   4. blur      - box blur that averages a samples x samples grid of nearby points
  *
  * Every effect is a pure Image => Image function: nothing is sampled or
  * stored until Renderer.png asks for a pixel.
  *
  * Conventions (inherited from Basics.scala):
  *   - coordinates are normalized, x and y in [0,1], y grows DOWNWARD
  *   - effects never mutate; they return a new function
  */
object AiExtension {

  private val black = Color(0, 0, 0)

  private def clamp01(t: Double): Double = math.max(0.0, math.min(1.0, t))

  // ---------------------------------------------------------------------------
  // 1. Vignette
  // ---------------------------------------------------------------------------

  /** Darkens pixels by their distance from (cx, cy).
    *   distance <= inner : unchanged
    *   distance >= outer : blended toward black by `strength`
    *   in between        : linear ramp between the two
    * Implemented with the provided Color.blend so alpha conventions match.
    */
  def vignette(inner: Double, outer: Double, strength: Double,
               cx: Double = 0.5, cy: Double = 0.5): Effect = {
    require(inner >= 0.0 && outer > inner, "need 0 <= inner < outer")
    require(strength >= 0.0 && strength <= 1.0, "strength must be in [0,1]")
    (img: Image) =>
      (x, y) =>
        val d = math.hypot(x - cx, y - cy)
        val t = clamp01((d - inner) / (outer - inner))
        Color.blend(img(x, y), black, strength * t)
  }

  // ---------------------------------------------------------------------------
  // 2. Pixelation
  // ---------------------------------------------------------------------------

  /** Center of the grid cell that v falls in, for a grid of n cells over [0,1].
    * The grid continues past [0,1] (floor works for any real v), so pixelate
    * still behaves sensibly after coordinate effects like mirrorHorizontal.
    */
  def cellCenter(v: Double, n: Int): Double =
    (math.floor(v * n) + 0.5) / n

  /** Every point in a cell takes the color at that cell's center. */
  def pixelate(columns: Int, rows: Int): Effect = {
    require(columns > 0 && rows > 0, "need at least one column and row")
    (img: Image) =>
      (x, y) => img(cellCenter(x, columns), cellCenter(y, rows))
  }

  // ---------------------------------------------------------------------------
  // 3. Coordinate warps
  // ---------------------------------------------------------------------------

  /** General coordinate warp: output pixel (x,y) shows the input at f(x,y).
    * Note this is a PULL (inverse) mapping: f says where to look, not where
    * a pixel moves to.
    */
  def warp(f: (Double, Double) => (Double, Double)): Effect =
    (img: Image) =>
      (x, y) =>
        val (u, v) = f(x, y)
        img(u, v)

  /** Shifts each row horizontally by amplitude * sin(2*pi*frequency*y).
    * frequency = number of full waves from top (y=0) to bottom (y=1).
    */
  def wave(amplitude: Double, frequency: Double): Effect =
    warp((x, y) => (x + amplitude * math.sin(2 * math.Pi * frequency * y), y))

  /** Rotates points around (cx, cy). A point at the center is rotated by
    * `angle` radians; the rotation shrinks linearly to 0 at `radius`, and
    * points at or beyond `radius` are left alone.
    */
  def swirl(cx: Double, cy: Double, radius: Double, angle: Double): Effect = {
    require(radius > 0.0, "radius must be positive")
    warp { (x, y) =>
      val dx = x - cx
      val dy = y - cy
      val d = math.hypot(dx, dy)
      if d >= radius then (x, y)
      else
        val theta = angle * (1.0 - d / radius)
        val c = math.cos(theta)
        val s = math.sin(theta)
        (cx + dx * c - dy * s, cy + dx * s + dy * c)
    }
  }

  // ---------------------------------------------------------------------------
  // 4. Blur
  // ---------------------------------------------------------------------------

  /** Channel-wise mean of a non-empty list of colors, using a fold. */
  def average(colors: List[Color]): Color = {
    require(colors.nonEmpty, "cannot average zero colors")
    val (r, g, b) = colors.foldLeft((0.0, 0.0, 0.0)) {
      case ((r, g, b), c) => (r + c.r, g + c.g, b + c.b)
    }
    val n = colors.size.toDouble
    Color(Color.clip(r / n), Color.clip(g / n), Color.clip(b / n))
  }

  /** `samples` evenly spaced offsets that split [-radius, radius] into
    * equal slices and take the middle of each slice.
    *   samples = 1 -> List(0)
    *   samples = 2 -> List(-radius/2, radius/2)
    */
  def kernelOffsets(radius: Double, samples: Int): List[Double] =
    (0 until samples).toList.map(i => ((i + 0.5) / samples) * 2 * radius - radius)

  /** Box blur: averages a samples x samples grid of points within `radius`
    * of (x, y). Cost: samples^2 calls to the inner image per output pixel.
    */
  def blur(radius: Double, samples: Int): Effect = {
    require(radius >= 0.0, "radius must be non-negative")
    require(samples >= 1, "need at least one sample")
    val offsets = kernelOffsets(radius, samples)
    val kernel = for (dx <- offsets; dy <- offsets) yield (dx, dy)
    (img: Image) =>
      (x, y) => average(kernel.map((dx, dy) => img(x + dx, y + dy)))
  }

  // ---------------------------------------------------------------------------
  // Required entry point from the starter code
  // ---------------------------------------------------------------------------

  /** The single "artistic effect" the starter asks for: a soft vignette. */
  def artisticEffect: Effect = vignette(inner = 0.3, outer = 0.75, strength = 0.85)

  // ---------------------------------------------------------------------------
  // Helpers used by the tests (AiChecks) and the gallery (AiGallery)
  // ---------------------------------------------------------------------------

  /** Functions cannot be compared directly, so compare them on an n x n grid
    * of pixel-center sample points (the same points Renderer.png uses).
    */
  def sampledEqual(a: Image, b: Image, n: Int = 32): Boolean =
    (0 until n).forall { j =>
      (0 until n).forall { i =>
        val x = (i + 0.5) / n
        val y = (j + 0.5) / n
        a(x, y) == b(x, y)
      }
    }

  /** A coordinate ramp whose color reveals where it was sampled:
    * red = round(200 * x), green = round(200 * y). Handy for warp tests.
    */
  val coordinateRamp: Image =
    (x, y) => Color(Color.clip(200 * x), Color.clip(200 * y), 0)

  /** Before/after layout: left half shows `left`, right half shows `right`,
    * each squeezed into its half. Render at twice the normal width.
    */
  def beside(left: Image, right: Image): Image =
    (x, y) => if x < 0.5 then left(2 * x, y) else right(2 * x - 1, y)
}

/** Renders example artwork and before/after comparisons for every AI effect.
  * Run with: sbt "runMain studio.image.AiGallery"
  */
object AiGallery {
  import AiExtension.*

  // ----- Artwork 1: "Dusk Lake" built from five+ ordered layers -----

  private val skyTop    = Color(28, 32, 84)
  private val skyBottom = Color(250, 160, 110)
  private val sunColor  = Color(255, 214, 120)
  private val farHill   = Color(96, 58, 110)
  private val nearHill  = Color(46, 28, 62)
  private val lake      = Color(36, 48, 96)
  private val glint     = Color(255, 236, 190)

  private val sky: Image = Given.gradient(skyTop, skyBottom)

  /** Points below a sine-shaped horizon. */
  private def below(base: Double, amp: Double, freq: Double, phase: Double): Mask =
    (x, y) => y > base + amp * math.sin(2 * math.Pi * freq * x + phase)

  private val lakeMask: Mask = StudentWork.halfplane(0, -1, -0.78) // y > 0.78

  val duskLayers: List[Layer] = List(
    Layer("sun", Given.solid(sunColor), Given.circle(0.68, 0.52, 0.14)),
    Layer("far hills", Given.solid(farHill), below(0.60, 0.04, 1.5, 0.0)),
    Layer("near hills", Given.solid(nearHill), below(0.70, 0.05, 2.3, 1.2)),
    Layer("lake", Given.solid(lake), lakeMask),
    Layer("sun reflection", Given.solid(sunColor),
      StudentWork.intersect(lakeMask, StudentWork.rectangle(0.58, 0.78, 1.0, 0.0)), 0.35),
    Layer("ripples", Given.solid(glint),
      StudentWork.intersect(lakeMask, StudentWork.stripes((_, y) => y, 0.015)), 0.18)
  )

  val dusk: Image = HumanCore.composeLayers(sky, duskLayers)

  // ----- Artwork 2: "Night Arcade" - same layers, different data + effects -----

  private val nightLayers: List[Layer] =
    duskLayers
      .filter(_.name != "sun reflection")
      .map(l => if l.name == "sun" then l.copy(image = Given.solid(Color(235, 240, 255))) else l)

  private val nightSky: Image = Given.gradient(Color(6, 8, 30), Color(40, 50, 110))

  val night: Image = HumanCore.composeLayers(nightSky, nightLayers)

  def main(args: Array[String]): Unit = {
    val w = 400
    val h = 250
    val dir = "output/ai"

    def save(name: String, img: Image, width: Int = w, height: Int = h): Unit = {
      Renderer.png(img, width, height, s"$dir/$name.png")
      println(s"Saved $dir/$name.png")
    }

    def beforeAfter(name: String, effect: Effect): Unit =
      save(s"before-after-$name", beside(dusk, effect(dusk)), 2 * w, h)

    save("dusk-original", dusk)

    beforeAfter("vignette", vignette(0.3, 0.75, 0.85))
    beforeAfter("pixelate", pixelate(40, 25))
    beforeAfter("wave", wave(0.03, 4))
    beforeAfter("swirl", swirl(0.68, 0.52, 0.35, math.Pi))
    beforeAfter("blur", blur(0.012, 5))

    // Composite artwork 1: a dreamy, rippled version of the scene.
    val dreamy = StudentWork.pipeline(List(wave(0.012, 6), blur(0.006, 3), vignette(0.35, 0.8, 0.7)))
    save("artwork-dusk-dream", dreamy(dusk))

    // Composite artwork 2: retro pixel night, from different layer data and effects.
    val arcade = StudentWork.pipeline(List(swirl(0.68, 0.52, 0.25, 1.5), pixelate(64, 40), vignette(0.25, 0.7, 0.9)))
    save("artwork-night-arcade", arcade(night))
  }
}
