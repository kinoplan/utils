package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import reactivemongo.api.bson._

final case class Printer(ident: String = "  ", maxLength: Int) {

  def print(doc: BSONDocument, initialCapacity: Int = 16): String = {
    val sb = new StringBuilder(initialCapacity)
    unsafePrint(
      doc,
      name =>
        if (name.isEmpty) ""
        else s"'$name': ",
      sb
    )
    sb.result()
  }

  private def append(sb: StringBuilder, s: String): Boolean =
    if (sb.length + s.length > maxLength) {
      val remainingLength = maxLength - sb.length
      if (remainingLength > 0) sb.append(s.take(remainingLength))
      true
    } else {
      sb.append(s)
      false
    }

  private def unsafePrint(doc: BSONDocument, f: String => String, sb: StringBuilder): Boolean =
    if (doc.elements.isEmpty) append(sb, "{}")
    else {
      if (append(sb, "{\n")) return true
      doc
        .elements
        .foreach {
          case BSONElement((name, value)) =>
            if (unsafePrint(name, value, 1, ident, f, sb)) return true
            if (append(sb, ",\n")) return true
          case _ => ()
        }
      sb.delete(sb.size - 2, sb.size)
      append(sb, "\n}")
    }

  private def unsafePrint(
    name: String,
    value: BSONValue,
    deep: Int,
    currentIdent: String,
    f: String => String,
    sb: StringBuilder
  ): Boolean = {
    if (append(sb, currentIdent)) return true
    if (append(sb, f(name))) return true

    value match {
      case BSONDouble(d) => append(sb, d.toString)
      case s: BSONString => append(sb, BSONString.pretty(s))
      case BSONArray(vs) =>
        if (vs.isEmpty) append(sb, "[]")
        else {
          if (append(sb, "[\n")) return true
          vs.foreach { v =>
            if (unsafePrint("", v, deep + 1, currentIdent + ident, f, sb)) return true
            if (append(sb, ",\n")) return true
          }
          sb.delete(sb.size - 2, sb.size)
          append(sb, s"\n$currentIdent]")
        }
      case BSONDocument(elements) =>
        if (elements.isEmpty) append(sb, "{}")
        else {
          if (append(sb, "{\n")) return true
          elements.foreach {
            case BSONElement((name, value)) =>
              if (unsafePrint(name, value, deep + 1, currentIdent + ident, f, sb)) return true
              if (append(sb, ",\n")) return true
            case _ => ()
          }
          sb.delete(sb.size - 2, sb.size)
          append(sb, s"\n$currentIdent}")
        }
      case binary: BSONBinary       => append(sb, BSONBinary.pretty(binary))
      case BSONUndefined            => append(sb, BSONUndefined.pretty)
      case d: BSONObjectID          => append(sb, BSONObjectID.pretty(d))
      case BSONBoolean(boolean)     => append(sb, boolean.toString)
      case time: BSONDateTime       => append(sb, BSONDateTime.pretty(time))
      case BSONNull                 => append(sb, BSONNull.pretty)
      case regex: BSONRegex         => append(sb, BSONRegex.pretty(regex))
      case BSONSymbol(symbol)       => append(sb, symbol)
      case BSONInteger(i)           => append(sb, i.toString)
      case timestamp: BSONTimestamp => append(sb, BSONTimestamp.pretty(timestamp))
      case l: BSONLong              => append(sb, BSONLong.pretty(l))
      case decimal: BSONDecimal     => append(sb, BSONDecimal.pretty(decimal))
      case BSONMinKey               => append(sb, BSONMinKey.pretty)
      case BSONMaxKey               => append(sb, BSONMaxKey.pretty)
      case _                        => append(sb, value.toString)
    }

  }

}

object Printer {
  def withLimit(maxLength: Int): Printer = Printer(maxLength = maxLength)
}
