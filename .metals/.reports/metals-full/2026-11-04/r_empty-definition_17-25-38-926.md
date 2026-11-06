error id: file://<WORKSPACE>/src/main/scala/studio/image/HumanCore.scala:
file://<WORKSPACE>/src/main/scala/studio/image/HumanCore.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -acc.
	 -acc#
	 -acc().
	 -scala/Predef.acc.
	 -scala/Predef.acc#
	 -scala/Predef.acc().
offset: 705
uri: file://<WORKSPACE>/src/main/scala/studio/image/HumanCore.scala
text:
```scala
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
      layers.filter(_ => _.enabled).foldLeft(background)(
        (acc, item: Layer) =>
          if item.mask(x, y) then
            // do stuff with layer
            Color.blend(@@acc(x, y), item.image(x, y), item.opacity)
          else
            // show past layers
            acc(x, y)
        )
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 