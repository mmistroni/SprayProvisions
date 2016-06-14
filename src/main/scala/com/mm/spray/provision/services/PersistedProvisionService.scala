package com.mm.spray.provision.services

import com.mm.spray.provision.entities.{Provision, ProvisionUpdate}
import com.mm.spray.provision.entities.ProvisionTypeEnum._
import scala.concurrent.{ExecutionContext, Future}
import org.joda.time.LocalDate
import com.mm.spray.provision.serializers._

import scala.concurrent.ExecutionContext.Implicits.global
import com.mm.spray.provision.persistence.PersistenceService
  

class PersistedProvisionService(persistenceService:PersistenceService)  extends ProvisionService {
  
  
  val db = persistenceService
  
  override def createQuestion(question: Provision): Future[Option[String]] =  {
    try {
      db.createQuestion(question).map(q => Some(q.user))
    } catch {
      case e:Exception => println(e.toString());throw e
    }
    
  }
  
  override def updateQuestion(id: String, update: ProvisionUpdate): Future[Option[Provision]] = {
    println("UPdating for Id:" + id + "updateQst:" + update)
    db.updateQuestion(id.toInt, update.description, update.amount)
  }
  
  override def deleteQuestion(id: String): Future[Boolean] =  {
    db.deleteQuestionById(id.toInt)
  }
  
  override def getQuestion(id: String): Future[Option[Provision]] =  {
    db.findQuestionById(id)
  }
  
  override def getQuestionById(id: Int): Future[Option[Provision]] =  {
    db.findQuestionByQuestionId(id)
  
  }
  
  override def getAllQuestions:Future[Seq[Provision]] =  {
    db.findAllQuestions
  }

  override def getQuestionsByProvisionType(provisionType:ProvisionTypeEnum):Future[Seq[Provision]] = {
    db.findQuestionsByProvisionType(provisionType)
  }
  
  
  
  
  /**
  
  
  
  def createQuestion(question: Question): Future[Option[String]] = Future {
    questions.find(_.id == question.id) match {
      case Some(q) => None // Conflict! id is already taken
      case None =>
        questions = questions :+ question
        Some(question.id)
    }
  }

  
  
  def getQuestion(id: String): Future[Option[Question]] = Future {
    questions.find(_.id == id)
  }

  def getAllQuestions:Future[Seq[Question]] = Future {
    questions
  }
  
  def updateQuestion(id: String, update: QuestionUpdate): Future[Option[Question]] = {
    
    def updateEntity(question: Question): Question = {
      val title = update.title.getOrElse(question.title)
      val text = update.text.getOrElse(question.text)
      Question(None, id, title, text, question.questionDate)
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

  def deleteQuestion(id: String): Future[Unit] = Future {
    questions = questions.filterNot(_.id == id)
  }
  **/

}
