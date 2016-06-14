package com.mm.spray.provision.persistence

import scala.language.postfixOps
import slick.driver.H2Driver.api._
import com.github.tototoshi.slick.H2JodaSupport._
import scala.concurrent.Future
import com.mm.spray.provision.entities.ProvisionTypeEnum._
import org.joda.time.LocalDate
import com.mm.spray.provision.persistence.Provisions._
import com.mm.spray.provision.entities.Provision
import com.mm.spray.provision.entities.ProvisionTypeEnum

class PersistenceService {
  import scala.concurrent.ExecutionContext.Implicits.global
  lazy val db = Database.forConfig("db")

  implicit val myEnumMapper = MappedColumnType.base[ProvisionTypeEnum, Int](
    e => e.id,
    s => ProvisionTypeEnum.apply(s))

  def createSchema() = db.run(
    DBIO.seq((
      provisions.schema).create))

  def truncate() = db.run(
    DBIO.seq(
      provisions.delete))

  def createDataset() = db.run(
    DBIO.seq(
      provisions ++= Seq(
        Provision(None, "978-1783281411", "Learning Concurrent Programming in Scala", 0.5, new LocalDate(2014, 11, 25), COUNCIL),
        Provision(None, "978-1783283637", "Scala for Java Developers", 0.6, new LocalDate(2014, 6, 11), PHONE))))

  def createQuestion(input: Provision) = {
    println("Real db executing real things....")
    db.run(provisions += input) map { _ => input }
  }

  def findAllQuestions = {
    db.run(provisions.result)
  }

  def findQuestionsByProvisionType(provType: ProvisionTypeEnum) = db.run(provisions.filter { _.provisionType === provType } result)

  def findQuestionByQuestionId(id: Int): Future[Option[Provision]] = {
    val query = provisions.filter(_.questionId === id)
    db.run(query.result.headOption)
  }

  def findQuestionById(id: String): Future[Option[Provision]] = {
    val query = provisions.filter(_.user === id)
    db.run(query.result.headOption)
  }

  def persistQuestion(question: Provision) = db.run(provisions += question) map { _ => question }

  def deleteQuestionById(id: Int) = db.run(provisions.filter { _.questionId === id } delete) map { _ > 0 }

  def deleteQuestionByQuestionId(id: Int) = db.run(provisions.filter { _.questionId === id } delete) map { _ > 0 }

  def updateQuestion(id: Int, desc: Option[String], amount: Option[Double]): Future[Option[Provision]] = db.run {
    val updateAndSelect = for {
      updatesCount <- provisions.filter(_.questionId === id).map(q => (q.description, q.amount)).update(desc.get, amount.get)
      updatedCatOpt <- updatesCount match {
        case 0 => DBIO.successful(Option.empty[Provision])
        case _ => provisions.filter(_.questionId === id).result.map(_.headOption)
      }
    } yield updatedCatOpt

    updateAndSelect.transactionally
  }

  /**
   * http://stackoverflow.com/questions/33139758/slick-update-return-the-updated-object/36846346
   * db.run {
   * val updateAndSelect = for {
   * updatesCount <- cats.filter(_.id === id).update(cat)
   * updatedCatOpt <- updatesCount match {
   * case 0 => DBIO.successful(Option.empty[Cat])
   * case _ => cats.filter(_.id === id).result.map(_.headOption)
   * }
   * } yield updatedCatOpt
   *
   * updateAndSelect.transactionally
   * }
   *
   *
   * {
   * val query = for (question <- questions.filter { _.id === id }) yield (question.title, question.text)
   * val res = db.run {
   * val updateCount = query.update(ttl.get, txt.get)) map { _ > 0 }
   * updateCount match {
   * case false => DBIO.successful(Option.empty[Question])
   * case true => findQuestionByQuestionId(id)
   * }
   * }
   * res
   * }
   */

}