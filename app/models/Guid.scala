package models

import java.util.UUID

object Guid {
  def next = UUID.randomUUID().toString
}
