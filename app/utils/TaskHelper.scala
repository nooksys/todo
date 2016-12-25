package utils

import org.apache.commons.lang3.RandomStringUtils
import java.util.UUID
import models.Task
import scala.util.Random

object TaskHelper {

  def createRandomTask = Task(
    UUID.randomUUID().toString,
    LoremIpsum.words(10),
    LoremIpsum.paragraphs(5),
    Random.nextBoolean())
}