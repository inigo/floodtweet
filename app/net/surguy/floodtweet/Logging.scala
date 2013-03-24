package net.surguy.floodtweet

import org.slf4j.LoggerFactory

/**
 * A mix-in trait to add SLF4J logging to a Scala class.
 */
trait Logging {
  protected lazy val log = LoggerFactory.getLogger(this.getClass)
}

