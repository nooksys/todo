package utils

import org.apache.commons.lang3.RandomStringUtils
import java.util.UUID
import models.Task
import scala.util.Random

object TaskHelper {

  def createRandomTask = Task(UUID.randomUUID().toString, RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(100), Random.nextBoolean())
}