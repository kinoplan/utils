package io.kinoplan.utils.reactivemongo.kamon.instrumentation

import reactivemongo.api.bson._

// Each print method returns true once maxLength is reached, which stops printing
final case class Printer(ident: String = "  ", maxLength: Int) {

  def print(doc: BSONDocument, initialCapacity: Int = 16): String = {
    val sb = new StringBuilder(initialCapacity)
    printDocument(doc.elements, "", sb)
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

  private def printAll[A](
    items: Iterable[A],
    open: String,
    close: String,
    currentIdent: String,
    sb: StringBuilder
  )(printItem: A => Boolean): Boolean =
    if (items.isEmpty) append(sb, open + close)
    else append(sb, open + "\n") || items.exists(item => printItem(item) || append(sb, ",\n")) || {
      sb.delete(sb.size - 2, sb.size)
      append(sb, s"\n$currentIdent$close")
    }

  private def printDocument(
    elements: Iterable[BSONElement],
    currentIdent: String,
    sb: StringBuilder
  ): Boolean = printAll(elements, "{", "}", currentIdent, sb)(element =>
    printValue(element.name, element.value, currentIdent + ident, sb)
  )

  private def printValue(
    name: String,
    value: BSONValue,
    currentIdent: String,
    sb: StringBuilder
  ): Boolean = append(sb, currentIdent) ||
    append(
      sb,
      if (name.isEmpty) ""
      else s"'$name': "
    ) || (
      value match {
        case BSONDouble(d)     => append(sb, d.toString)
        case s: BSONString     => append(sb, BSONString.pretty(s))
        case BSONArray(values) =>
          printAll(values, "[", "]", currentIdent, sb)(printValue("", _, currentIdent + ident, sb))
        case BSONDocument(elements)   => printDocument(elements, currentIdent, sb)
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
    )

}

object Printer {
  def withLimit(maxLength: Int): Printer = Printer(maxLength = maxLength)
}
