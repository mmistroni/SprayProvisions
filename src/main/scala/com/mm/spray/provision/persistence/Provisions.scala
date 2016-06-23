package com.mm.spray.provision.persistence

import slick.driver.H2Driver.api._
import com.github.tototoshi.slick.H2JodaSupport._
import com.mm.spray.provision.entities._
import com.mm.spray.provision.entities.ProvisionTypeEnum._

import org.joda.time.LocalDate

class Provisions(tag: Tag) extends Table[Provision](tag, "provision") {
  
  implicit val myEnumMapper = MappedColumnType.base[ProvisionTypeEnum, Int](
    e => e.id,
    s => ProvisionTypeEnum.apply(s)
    )

  def provisionId = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def user = column[String]("user", O.Length(10))
  def description = column[String]("description", O.Length(20))
  def amount = column[Double]("amount", O.Length(50))
  def provisionDate = column[LocalDate]("provisionDate")
  def provisionType = column[ProvisionTypeEnum]("provisionType")

  def * = (provisionId.?, user, description, amount, provisionDate, provisionType) <>
    ((Provision.apply _).tupled, Provision.unapply _)

}

object Provisions {
  val provisions = TableQuery[Provisions]
}
