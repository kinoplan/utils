package io.circe

abstract class NonEmptySequenceDecoder[A, C[_], S](implicit
  decoder: Decoder[A]
) extends NonEmptySeqDecoder[A, C, S](decoder)
