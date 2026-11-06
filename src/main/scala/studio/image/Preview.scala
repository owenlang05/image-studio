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
    val french_flag = HumanCore.composeLayers(blue_base, List(white_layer, red_layer))

    val effected_layer = Layer("effects", pipeline(french_flag), diag)
    var new_french_flag = HumanCore.composeLayers(french_flag, List(effected_layer))

    val opp_effected_layer = Layer("I can't name things", pipeline_2(french_flag), opp_diag)
    new_french_flag = HumanCore.composeLayers(french_flag, List(opp_effected_layer))

    // german flag
    val german_red_base = Given.solid(Color(223, 0, 0))
    val german_black_base = Given.solid(Color(0, 0, 0))
    val german_yellow_base = Given.solid(Color(254, 206, 0))
    // german masks
    val horizontal_one = StudentWork.halfplane(0, 1, 0.33)
    val horizontal_two = StudentWork.halfplane(0, 1, 0.66)

    val german_black_layer = Layer("german flag black", german_black_base, horizontal_one)
    val german_red_layer = Layer("german red layer", german_red_base, horizontal_two)

    val german_flag = HumanCore.composeLayers(german_yellow_base, List(german_red_layer, german_black_layer))
    val mixed_flags = Layer("mixed flags", german_flag, opp_diag)
    val mixed_flag = HumanCore.composeLayers(french_flag, List(mixed_flags))
    Renderer.png(german_flag, 320, 200, "output/german_flag.png")
    Renderer.png(mixed_flag, 320, 200, "output/mixed_flag.png")
    Renderer.png(new_french_flag, 320, 200, "output/french_flag.png")
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

/** Tests for the GenAI extension (AiExtension.scala). Expected values below
  * were worked out by hand, not by running the code under test.
  * Run with: sbt "runMain studio.image.AiChecks"
  */
object AiChecks {
  def main(args: Array[String]): Unit = {
    import AiExtension.*

    val black = Color(0, 0, 0)
    val white = Color(255, 255, 255)
    val red = Color(200, 30, 40)
    val solidRed = Given.solid(red)
    val solidWhite = Given.solid(white)
    // left half black, right half white: a hard vertical edge at x = 0.5
    val split: Image = (x, _) => if x < 0.5 then black else white

    // ---------------- Vignette ----------------
    // inner = 0.125, outer = 0.375, strength = 0.5 (all exact in binary)
    val v = vignette(0.125, 0.375, 0.5)
    assert(v(solidWhite)(0.5, 0.5) == white, "vignette: center is untouched")
    // (0.75, 0.5): d = 0.25, t = (0.25 - 0.125) / 0.25 = 0.5, alpha = 0.5 * 0.5 = 0.25
    // each channel = 255 * (1 - 0.25) = 191.25 -> 191
    assert(v(solidWhite)(0.75, 0.5) == Color(191, 191, 191), "vignette: hand-computed ramp midpoint")
    // corner: d = 0.707 > outer, alpha = 0.5 -> 127.5 -> rounds to 128
    assert(v(solidWhite)(0.0, 0.0) == Color(128, 128, 128), "vignette: full strength past outer")
    assert(sampledEqual(vignette(0.1, 0.5, 0.0)(solidRed), solidRed), "vignette: strength 0 is identity")

    // Mutation detection: the same hand-computed oracle must REJECT a vignette
    // whose one-line mutation swaps the blend arguments (black on bottom).
    def vignetteOracle(vig: (Double, Double, Double) => Effect): Boolean = {
      val e = vig(0.125, 0.375, 0.5)(solidWhite)
      e(0.5, 0.5) == white && e(0.75, 0.5) == Color(191, 191, 191)
    }
    val mutantVignette: (Double, Double, Double) => Effect =
      (inner, outer, strength) => img => (x, y) =>
        val d = math.hypot(x - 0.5, y - 0.5)
        val t = math.max(0.0, math.min(1.0, (d - inner) / (outer - inner)))
        Color.blend(black, img(x, y), strength * t)   // MUTATION: arguments swapped
    assert(vignetteOracle((i, o, s) => vignette(i, o, s)), "oracle accepts the real vignette")
    assert(!vignetteOracle(mutantVignette), "oracle must detect the swapped-blend mutation")

    // ---------------- Pixelate ----------------
    // 4 x 4 grid. (0.3, 0.9): x cell = floor(1.2) = 1 -> center 0.375 -> red 200*0.375 = 75
    //                         y cell = floor(3.6) = 3 -> center 0.875 -> green 200*0.875 = 175
    val p = pixelate(4, 4)
    assert(p(coordinateRamp)(0.3, 0.9) == Color(75, 175, 0), "pixelate: hand-computed cell center")
    assert(p(coordinateRamp)(0.26, 0.26) == p(coordinateRamp)(0.49, 0.49), "pixelate: same cell, same color")
    assert(p(coordinateRamp)(0.24, 0.3) != p(coordinateRamp)(0.26, 0.3), "pixelate: neighboring cells differ")
    // function equality is undecidable, so check idempotence on sampled points
    assert(sampledEqual(p(p(coordinateRamp)), p(coordinateRamp)), "pixelate: applying twice == once (sampled)")
    assert(!sampledEqual(p(coordinateRamp), coordinateRamp), "pixelate: actually changes a ramp")

    // ---------------- Wave warp ----------------
    // y = 0: sin(0) = 0, no shift. y = 0.25 with frequency 1: sin(pi/2) = 1, shift by amplitude.
    val wv = wave(0.1, 1.0)
    assert(wv(coordinateRamp)(0.5, 0.0) == coordinateRamp(0.5, 0.0), "wave: no shift where sin = 0")
    // (0.5, 0.25) samples (0.6, 0.25): red = 200*0.6 = 120, green = 200*0.25 = 50
    assert(wv(coordinateRamp)(0.5, 0.25) == Color(120, 50, 0), "wave: hand-computed shift at the crest")
    assert(sampledEqual(wave(0.0, 3.0)(coordinateRamp), coordinateRamp), "wave: amplitude 0 is identity")

    // ---------------- Swirl warp ----------------
    // center (0.5,0.5), radius 0.5, angle pi. Point (0.75, 0.5): d = 0.25, theta = pi * (1 - 0.5) = pi/2
    // rotate (0.25, 0) by 90 deg -> (0, 0.25) -> samples (0.5, 0.75) -> Color(100, 150, 0)
    val sw = swirl(0.5, 0.5, 0.5, math.Pi)
    assert(sw(coordinateRamp)(0.75, 0.5) == Color(100, 150, 0), "swirl: hand-computed quarter turn")
    assert(sw(coordinateRamp)(0.5, 0.5) == coordinateRamp(0.5, 0.5), "swirl: center is a fixed point")
    assert(sw(coordinateRamp)(0.05, 0.05) == coordinateRamp(0.05, 0.05), "swirl: outside radius unchanged")
    assert(sampledEqual(swirl(0.5, 0.5, 0.4, 0.0)(coordinateRamp), coordinateRamp), "swirl: angle 0 is identity")

    // ---------------- Blur ----------------
    // samples = 2, radius = 0.1 -> offsets -0.05 and +0.05.
    // At x = 0.5 on the split image: 0.45 is black, 0.55 is white; 2 black + 2 white samples.
    // mean = (0 + 0 + 255 + 255) / 4 = 127.5 -> rounds to 128
    val b = blur(0.1, 2)
    assert(b(split)(0.5, 0.5) == Color(128, 128, 128), "blur: hand-computed average across an edge")
    assert(b(split)(0.2, 0.5) == black, "blur: far from the edge stays black")
    assert(sampledEqual(blur(0.05, 3)(solidRed), solidRed), "blur: a solid color is unchanged")
    assert(sampledEqual(blur(0.2, 1)(split), split), "blur: one sample is identity")
    assert(average(List(Color(10, 20, 30), Color(30, 40, 50))) == Color(20, 30, 40), "average: hand-computed")

    // ---------------- Composition order ----------------
    // pixelate then wave != wave then pixelate (sampled), and the pipeline follows list order.
    val pw = StudentWork.pipeline(List(pixelate(8, 8), wave(0.05, 2)))
    val wp = StudentWork.pipeline(List(wave(0.05, 2), pixelate(8, 8)))
    assert(sampledEqual(pw(coordinateRamp), wave(0.05, 2)(pixelate(8, 8)(coordinateRamp))), "pipeline: list order")
    assert(!sampledEqual(pw(coordinateRamp), wp(coordinateRamp)), "effect order matters")
    // after wave-then-pixelate, the output is constant on each cell
    assert(wp(coordinateRamp)(0.01, 0.01) == wp(coordinateRamp)(0.12, 0.12), "pixelate last: blocky output")

    // ---------------- Interaction with the layer compositor ----------------
    // An effect applied to a layer's image only shows where the layer's mask applies.
    val layer = Layer("vignetted", v(solidWhite), StudentWork.halfplane(1, 0, 0.5)) // x < 0.5
    val scene = HumanCore.composeLayers(solidRed, List(layer))
    assert(scene(0.25, 0.5) == v(solidWhite)(0.25, 0.5), "effected layer shows inside its mask")
    assert(scene(0.75, 0.5) == red, "effected layer does not leak outside its mask")

    println("All AiExtension checks passed.")
  }
}
