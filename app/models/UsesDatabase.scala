package models

import slick.session.Database
import play.api.db.DB

/**
 * Provides a database reference.
 *
 * @author Inigo Surguy
 */
trait UsesDatabase {
  import play.api.Play.current
  def database = Database.forDataSource(DB.getDataSource())
}
