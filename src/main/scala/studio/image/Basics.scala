package studio.image

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// An image is a function, not an array. Coordinates are normalized to [0,1].
type Image = (Double, Double) => Color
type Mask = (Double, Double) => Boolean
type Effect = Image => Image

final case class Color(r: Int, g: Int, b: Int) {
  require(List(r, g, b).forall(c => c >= 0 && c <= 255))
}

object Color {
  def clip(n: Double): Int =
    math.round(math.max(0.0, math.min(255.0, n))).toInt

  // Provided color blending primitive. Alpha 0 keeps the bottom color.
  def blend(bottom: Color, top: Color, alpha: Double): Color = {
    require(alpha >= 0.0 && alpha <= 1.0)
    Color(
      clip(bottom.r * (1.0 - alpha) + top.r * alpha),
      clip(bottom.g * (1.0 - alpha) + top.g * alpha),
      clip(bottom.b * (1.0 - alpha) + top.b * alpha)
    )
  }
}

final case class Layer(
  name: String,
  image: Image,
  mask: Mask,
  opacity: Double = 1.0,
  enabled: Boolean = true
) {
  require(opacity >= 0.0 && opacity <= 1.0)
}

object Given {
  def solid(color: Color): Image = (_, _) => color
  def gradient(top: Color, bottom: Color): Image =
    (_, y) => Color.blend(top, bottom, math.max(0.0, math.min(1.0, y)))

  def circle(cx: Double, cy: Double, radius: Double): Mask = {
    require(radius >= 0.0)
    (x, y) => math.pow(x - cx, 2) + math.pow(y - cy, 2) <= radius * radius
  }

  val everywhere: Mask = (_, _) => true
  val nowhere: Mask = (_, _) => false
}

// File I/O and mutable pixel buffers exist ONLY at this rendering boundary.
object Renderer {
  def png(image: Image, width: Int, height: Int, filename: String): Unit = {
    require(width > 0 && height > 0)
    val out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    for (y <- 0 until height; x <- 0 until width) {
      val c = image((x + 0.5) / width, (y + 0.5) / height)
      out.setRGB(x, y, (c.r << 16) | (c.g << 8) | c.b)
    }
    val file = new File(filename)
    Option(file.getParentFile).foreach(_.mkdirs())
    ImageIO.write(out, "png", file)
  }
}
