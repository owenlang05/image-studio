package studio.image


object Preview {
  def main(args: Array[String]): Unit = {
    val blue = Color(40, 70, 145)
    val pale = Color(245, 186, 133)
    val background = Given.gradient(blue, pale)
    Renderer.png(background, 320, 200, "output/starter-gradient.png")
    println("Saved output/starter-gradient.png")
    println("Next: implement StudentWork and HumanCore; build TWO original artworks.")

    // TODO after completing HumanCore: design at least five original layers,
    // use your pipeline, and render the composition and a meaningful variation.
    // e.g. val scene = HumanCore.composeLayers(background, yourLayers)
    //      Renderer.png(scene, 640, 400, "output/scene.png")
    // france flag
    // masks
    val line_one = StudentWork.halfplane(1, 0, 0.66)
    val line_two = StudentWork.halfplane(1, 0, 0.33)
    val diag = StudentWork.halfplane(1, 1, 1)
    val opp_diag = StudentWork.halfplane(1, -1, 0)
    val mirrorHorizontal = StudentWork.mirrorHorizontal
    // base colors
    val red_base = Given.solid(Color(223, 34, 45))
    val white_base = Given.solid(Color(255, 255, 255))
    val blue_base = Given.solid(Color(8, 101, 175))
    //effects
    val invert = StudentWork.invertColors
    val brighten = StudentWork.brighten(50)
    val not_brighten = StudentWork.darken(25)
    // layers
    val red_layer = Layer("red layer", red_base, line_two)
    val white_layer = Layer("white layer", white_base, line_one)

    val pipeline = StudentWork.pipeline(List(invert, brighten, invert))
    val pipeline_2 = StudentWork.pipeline(List(invert, not_brighten))
    var french_flag = HumanCore.composeLayers(blue_base, List(white_layer, red_layer))

    val effected_layer = Layer("effects", pipeline(french_flag), diag)
    french_flag = HumanCore.composeLayers(french_flag, List(effected_layer))

    val opp_effected_layer = Layer("I can't name things", pipeline_2(french_flag), opp_diag)
    french_flag = HumanCore.composeLayers(french_flag, List(opp_effected_layer))
    Renderer.png(french_flag, 320, 200, "output/french_flag.png")
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
    val blue = Color(0, 0, 255)
    val orange = Color(255, 127, 0)
    val background = Given.solid(blue)
    val checkerboard = StudentWork.checkerboard(8, 8, blue, orange)
    //effects
    val invert = StudentWork.invertColors
    val brighten = StudentWork.brighten(50)
    val mirrorHorizontal = StudentWork.mirrorHorizontal
    val pipeline = StudentWork.pipeline(List(invert, mirrorHorizontal, brighten))
    val brightenMax = StudentWork.brighten(255)
    //masks
    val halfplane = StudentWork.halfplane(1, 1, 1)
    val left = Layer("left", top, halfplane, 1.0)
    
    
    //order
    assert(brighten(mirrorHorizontal(invert(checkerboard)))(0.0, 0.0) == pipeline(checkerboard)(0.0, 0.0),
    "pipeline should match list order")
        assert(invert(brighten(mirrorHorizontal(checkerboard)))(0.0, 0.0) != pipeline(checkerboard)(0.0, 0.0),
    "function order matters")
    assert(brightenMax(checkerboard)(0.0, 0.0) == white)
    assert(HumanCore.composeLayers(checkerboard, List(left))(0.0, 0.0) == white)
    assert(Color.blend(blue, orange, 0) == blue, "Alpha level 0 must keep bottom color")
    assert(Color.blend(blue, orange, 1) == orange, "Alpha level 1 must keep top color")
    assert(Color.blend(blue, orange, 0.5) == Color(128, 64, 128))
  }
}
