package com.mm.spray.provision.persistence

import slick.driver.H2Driver.api._
import com.github.tototoshi.slick.H2JodaSupport._
import com.mm.spray.provision.entities._
import com.mm.spray.provision.entities.ProvisionTypeEnum._

import org.joda.time.LocalDate

class Provisions(tag: Tag) extends Table[Provision](tag, "PROVISIONS") {
  
  implicit val myEnumMapper = MappedColumnType.base[ProvisionTypeEnum, Int](
    e => e.id,
    s => ProvisionTypeEnum.apply(s)
    )

  def questionId = column[Int]("questionId", O.PrimaryKey, O.AutoInc)
  def user = column[String]("id", O.Length(10))
  def description = column[String]("description", O.Length(20))
  def amount = column[Double]("amount", O.Length(50))
  def questionDate = column[LocalDate]("questionDate")
  def provisionType = column[ProvisionTypeEnum]("provisionType")

  def * = (questionId.?, user, description, amount, questionDate, provisionType) <>
    ((Provision.apply _).tupled, Provision.unapply _)

}

object Provisions {
  val provisions = TableQuery[Provisions]
}
