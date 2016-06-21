package com.mm.spray.provision.services

import com.mm.spray.provision.entities.{Provision, ProvisionUpdate}
import scala.concurrent.{ExecutionContext, Future}
import org.joda.time.LocalDate
import com.mm.spray.provision.serializers._
import com.mm.spray.provision.entities.ProvisionTypeEnum._

class ProvisionService(implicit val executionContext:ExecutionContext) {
  var provisions = Vector.empty[Provision]
  
  def createProvision(provision: Provision): Future[Option[String]] = Future {
    provisions.find(_.user == provision.user) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        provisions = provisions :+ provision
        Some(provision.user)
    }
  }

  def getProvisionById(id:Int): Future[Option[Provision]] = Future {
    provisions.find(_.questionId == Some(id))
  }
  
  def getProvision(user: String): Future[Option[Provision]] = Future {
    provisions.find(_.user == user)
  }

  def getAllProvisions:Future[Seq[Provision]] = Future {
    provisions
  }
  
  def getProvisionsByProvisionType(provisionType:ProvisionTypeEnum): Future[Seq[Provision]] = Future{
    provisions.filter(_.provisionType == provisionType)
  }
  
  def getProvisionsByProvisionDate(provisionDate:LocalDate): Future[Seq[Provision]] = Future{
    provisions.filter(_.provisionDate.isAfter(provisionDate))
  }
  
  
  def updateProvision(id: String, update: ProvisionUpdate): Future[Option[Provision]] = {
    
    def updateEntity(question: Provision): Provision = {
      val description = update.description.getOrElse(question.description)
      val amount = update.amount.getOrElse(question.amount)
      Provision(None, id, description, amount, question.provisionDate, question.provisionType)
    }

    getProvision(id).flatMap { maybeQuestion =>
      maybeQuestion match {
        case None => Future { None } // No question found, nothing to update
        case Some(question) =>
          val updatedQuestion = updateEntity(question)
          deleteProvision(id).flatMap { _ =>
            val q = createProvision(updatedQuestion).map(_ => Some(updatedQuestion))
            println("reTURNING CREATED QUESTION;" + q)
            q
          }
      }
    }
  }

  def deleteProvision(id: String): Future[Boolean] = Future {
    provisions.filterNot(_.user == id).size == 0
  }


}
