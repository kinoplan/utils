package io.kinoplan.utils.reactivemongo.base

import reactivemongo.api.bson.BSONDocument

trait BsonDocumentSyntax {

  implicit class BSONDocumentOps(document: BSONDocument) {

    def commentQuery(implicit
      enclosing: sourcecode.Enclosing
    ): BSONDocument = document ++ BSONDocument("$comment" -> QueryComment.make)

  }

}
