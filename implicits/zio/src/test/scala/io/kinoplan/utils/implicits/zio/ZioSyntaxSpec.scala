package io.kinoplan.utils.implicits.zio

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class ZioSyntaxSpec extends AsyncFlatSpec with Matchers with ZioSyntaxSpecExtensions
