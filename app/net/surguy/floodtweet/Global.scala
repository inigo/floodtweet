package net.surguy.floodtweet

import play.api.GlobalSettings
import org.quartz._
import com.typesafe.config.ConfigFactory
import org.quartz.impl.StdSchedulerFactory
import play.api

/**
 * Application config - sets up the schedules.
 */
object Global extends GlobalSettings with Logging {
  // This cannot be an immutable val, because it cannot be restarted after it is stopped, which happens when developing
  var scheduler: Scheduler = null

  override def onStart(app: api.Application) {
    log.info("Starting application")
    val conf = ConfigFactory.load()

    // Set up the scheduler, using Quartz cron expressions
    scheduler = StdSchedulerFactory.getDefaultScheduler
    scheduler.start()
    schedule(classOf[ScraperTrigger], conf.getString("scraper.schedule"))
  }

  private def schedule(clazz: Class[_ <: Job], cronExpression: String) {
    log.debug("Scheduling job "+clazz+" to run at "+cronExpression)
    val job = JobBuilder.newJob(clazz).build()
    val trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build()
    scheduler.scheduleJob(job, trigger)
  }

  override def onStop(app: api.Application) {
    super.onStop(app)
    log.info("Shutting down application")
  }
}
