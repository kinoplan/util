package io.kinoplan.utils.zio.redisson.utils

import scala.language.implicitConversions

final private[redisson] class IdentityOps[T](private val entity: T) extends AnyVal {

  @inline
  def applyOption[A](value: Option[A])(f: (T, A) => T): T = value
    .map(v => f(entity, v))
    .getOrElse(entity)

}

private[redisson] trait IdentitySyntax {
  implicit final def syntaxIdentityOps[T](entity: T): IdentityOps[T] = new IdentityOps(entity)
}

private[redisson] object IdentitySyntax extends IdentitySyntax
