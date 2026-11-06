# Track A: Functional Image Studio (Scala 3)

Run `sbt "runMain studio.image.Preview"` to create the provided starter gradient.
Run `sbt "runMain studio.image.Checks"` to run tests (initially fails: HUMAN-ONLY TODO).
Run `sbt "runMain studio.image.AiChecks"` to run AI Tests.
Run `sbt "runMain studio.image.AiGallery"` to run AI art renders.

- `Basics.scala` contains types, one gradient generator, a mask, color blending,
  and a PNG renderer. Leave this foundation intact unless you justify changes.
- `StudentWork.scala` has independent work for map/filter, effects, and functions as data.
- `HumanCore.composeLayers` is **human-only**. Do not submit its implementation or
  its tests to GenAI; implement and debug it from your group's own reasoning.
- `AiExtension.artisticEffect` is the **required GenAI-assisted component**. Record
  representative prompts and proposed code, your own tests, and corrections.
- `Preview.scala` demonstrates baseline I/O and leaves the original art to you.

