package com.mm.spray.provision.persistence

import scala.language.postfixOps
//import slick.driver.H2Driver.api._
import com.github.tototoshi.slick.H2JodaSupport._
import scala.concurrent.Future
import com.mm.spray.provision.entities.ProvisionTypeEnum._
import org.joda.time.LocalDate
import com.mm.spray.provision.persistence.Provisions._
import com.mm.spray.provision.entities.Provision
import com.mm.spray.provision.entities.ProvisionTypeEnum
import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver
import slick.driver.MySQLDriver.backend
import slick.driver.MySQLDriver.api.MappedColumnType
import slick.driver.MySQLDriver.api._

class PersistenceService {
  import scala.concurrent.ExecutionContext.Implicits.global
  val config = ConfigFactory.load()
  //val driverClass = classForName("com.mysql.jdbc.Driver")
  Class.forName("com.mysql.jdbc.Driver")
  lazy val db = backend.Database.forConfig("db")
  
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

	def createProvision(input: Provision) = {
    println("Real db executing real things....")
    db.run(provisions += input) map { _ => input }
  }

  def findAllProvisions = {
    // limited to last 15
    db.run(provisions.take(15) result)
  }

  def findProvisionsByProvisionType(provType: ProvisionTypeEnum) = 
    db.run(provisions.filter { _.provisionType === provType } take(20) result)

  def findProvisionByDate(provisionDate:LocalDate):Future[Seq[Provision]]= {
    val query = provisions.filter(_.provisionDate >= provisionDate).take(20)
    db.run(query.result)
  }
  
  
  def findProvisionByProvisionId(id: Int): Future[Option[Provision]] = {
    val query = provisions.filter(_.provisionId === id)
    db.run(query.result.headOption)
  }

  def findProvisionById(id: String): Future[Option[Provision]] = {
    val query = provisions.filter(_.user === id)
    db.run(query.result.headOption)
  }

  def persistProvision(question: Provision) = db.run(provisions += question) map { _ => question }

  def deleteProvisionById(id: Int) = db.run(provisions.filter { _.provisionId === id } delete) map { _ > 0 }

  def deleteProvisionByProvisionId(id: Int) = db.run(provisions.filter { _.provisionId === id } delete) map { _ > 0 }

  def updateProvision(id: Int, desc: Option[String], amount: Option[Double]): Future[Option[Provision]] = db.run {
    val updateAndSelect = for {
      updatesCount <- provisions.filter(_.provisionId === id).map(q => (q.description, q.amount)).update(desc.get, amount.get)
      updatedCatOpt <- updatesCount match {
        case 0 => DBIO.successful(Option.empty[Provision])
        case _ => provisions.filter(_.provisionId === id).result.map(_.headOption)
      }
    } yield updatedCatOpt

    updateAndSelect.transactionally
  }

  
}