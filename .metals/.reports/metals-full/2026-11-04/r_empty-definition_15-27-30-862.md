error id: file://<WORKSPACE>/src/main/scala/studio/image/Preview.scala:javax/swing/Renderer#
file://<WORKSPACE>/src/main/scala/studio/image/Preview.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -javax/swing/Renderer.
	 -javax/swing/Renderer#
	 -javax/swing/Renderer().
	 -Renderer.
	 -Renderer#
	 -Renderer().
	 -scala/Predef.Renderer.
	 -scala/Predef.Renderer#
	 -scala/Predef.Renderer().
offset: 41
uri: file://<WORKSPACE>/src/main/scala/studio/image/Preview.scala
text:
```scala
package studio.image

import javax.swing.@@Renderer

object Preview {
  def main(args: Array[String]): Unit = {
    val blue = Color(40, 70, 145)
    val pale = Color(245, 186, 133)
    val background = Given.gradient(blue, pale)
    val invert = StudentWork.invertColors(background)
    val checkerboard = StudentWork.checkerboard(8, 8, blue, pale)
    Renderer.png(checkerboard, 320, 200, "output/starter-gradient.png")
    // Renderer.png(StudentWork.brighten(50)(checkerboard), 320, 200, "output/test.png")
    println("Saved output/starter-gradient.png")
    println("Next: implement StudentWork and HumanCore; build TWO original artworks.")

    // TODO after completing HumanCore: design at least five original layers,
    // use your pipeline, and render the composition and a meaningful variation.
    // e.g. val scene = HumanCore.composeLayers(background, yourLayers)
    //      Renderer.png(scene, 640, 400, "output/scene.png")
  }
}

object Checks {
  def main(args: Array[String]): Unit = {
    val black = Color(0, 0, 0)
    val white = Color(255, 255, 255)
    val base = Given.solid(black)
    val top = Given.solid(white)
    // This test should FAIL before the HUMAN-ONLY implementation is complete.
    val opaque = Layer("top", top, Given.everywhere, 1.0)
    assert(HumanCore.composeLayers(base, List(opaque))(0.1, 0.2) == white,
      "opaque top layer should cover the background")
    val invisible = opaque.copy(opacity = 0.0)
    assert(HumanCore.composeLayers(base, List(invisible))(0.1, 0.2) == black)

    // TODO: At least 6 MORE tests across generators, masking, composition,
    // order, alpha boundaries, and function equality vs sampled equality.
    // Include an independently hand-computed blend and mutation detection.
    println("Starter tests passed; add your original tests before submitting.")
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 