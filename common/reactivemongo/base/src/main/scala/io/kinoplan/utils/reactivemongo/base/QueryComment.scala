package io.kinoplan.utils.reactivemongo.base

private[utils] object QueryComment {

  /** @param enclosing
    *   the name of the nearest enclosing definition: val, class, whatever, prefixed by the names of
    *   all enclosing classs, traits, objects or packages, defs, vals, vars or lazy vals
    * @return
    *   A constructed string with call site info
    */
  def make(implicit
    enclosing: sourcecode.Enclosing
  ): String = s"requested by $enclosing"

}
