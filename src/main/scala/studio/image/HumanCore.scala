package studio.image

object HumanCore {
  /** HUMAN-ONLY. No GenAI for design, implementation, testing, or debugging.
    * Later enabled layers are above earlier enabled layers.
    * A layer affects only the pixels in its mask.
    * Opacity 0 leaves lower layers unchanged; opacity 1 replaces them.
    * Use filter + foldLeft (not mutation or loops) in the core implementation.
    * Do not sample and store an image grid here.
    */
  def composeLayers(background: Image, layers: List[Layer]): Image = 
    (x, y) =>
      layers.filter(x => x.enabled).foldLeft(background(x, y))(
        (acc: Color, item: Layer) =>
          if item.mask(x, y) then
            // do stuff with layer
            Color.blend(acc, item.image(x, y), item.opacity)
          else
            // show past layers
            acc
        )
}
