package com.mm.spray.provision.services

import com.mm.spray.provision.entities.{Provision, ProvisionUpdate}
import scala.concurrent.{ExecutionContext, Future}
import org.joda.time.LocalDate
import com.mm.spray.provision.serializers._
import com.mm.spray.provision.entities.ProvisionTypeEnum._

class ProvisionService(implicit val executionContext:ExecutionContext) {
  var provisions = Vector.empty[Provision]
  
  def createQuestion(question: Provision): Future[Option[String]] = Future {
    provisions.find(_.user == question.user) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        provisions = provisions :+ question
        Some(question.user)
    }
  }

  def getQuestionById(id:Int): Future[Option[Provision]] = Future {
    provisions.find(_.questionId == Some(id))
  }
  
  def getQuestion(id: String): Future[Option[Provision]] = Future {
    provisions.find(_.user == id)
  }

  def getAllQuestions:Future[Seq[Provision]] = Future {
    provisions
  }
  
  def getQuestionsByProvisionType(provisionType:ProvisionTypeEnum):Future[Seq[Provision]] = Future{
    provisions.filter(_.provisionType == provisionType)
  }
  
  def updateQuestion(id: String, update: ProvisionUpdate): Future[Option[Provision]] = {
    
    def updateEntity(question: Provision): Provision = {
      val description = update.description.getOrElse(question.description)
      val amount = update.amount.getOrElse(question.amount)
      Provision(None, id, description, amount, question.questionDate, question.provisionType)
    }

    getQuestion(id).flatMap { maybeQuestion =>
      maybeQuestion match {
        case None => Future { None } // No question found, nothing to update
        case Some(question) =>
          val updatedQuestion = updateEntity(question)
          deleteQuestion(id).flatMap { _ =>
            val q = createQuestion(updatedQuestion).map(_ => Some(updatedQuestion))
            println("reTURNING CREATED QUESTION;" + q)
            q
          }
      }
    }
  }

  def deleteQuestion(id: String): Future[Boolean] = Future {
    provisions.filterNot(_.user == id).size == 0
  }


}
