package com.mm.spray.provision.services

import scala.concurrent.Future

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest
import com.mm.spray.provision.entities._
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.joda.time.LocalDate
import com.mm.spray.provision.serializers.JsonSupport
import com.mm.spray.provision.persistence.PersistenceService
import org.scalatest.concurrent.ScalaFutures._
import com.mm.spray.provision.entities.ProvisionTypeEnum._


class PersistedProvisionServiceSpec extends FreeSpec with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Promise
  import scala.concurrent.Future
  val mockDb = Mockito.mock(classOf[PersistenceService])

  def createDate(dt: java.util.Date): org.joda.time.LocalDate = {
    new LocalDate(dt)
  }

  val questionService = new PersistedProvisionService(mockDb)

  "The PersistedProvisionService" - {
    "when calling createQuestion it should call questionService.creteQuestion" - {
      "should return the created Question" in {
        val testQuestion = Provision(None, "1", "title", 1.0, createDate(new java.util.Date()), COUNCIL)
        Mockito.when(mockDb.createQuestion(testQuestion)).thenReturn(Future[Provision] { testQuestion })
        val future = questionService.createQuestion(testQuestion)
        whenReady(future) {
          res => res should equal(Some(testQuestion.user))
        }
        Mockito.verify(mockDb).createQuestion(testQuestion)
      }
    }
    "when calling updateQuestion it should call questionService.updatedQuestion" - {
      "should return the updatedQuestion Question" in {
        val questionId = 11
        val testQuestion = Provision(Some(questionId), "2", "title", 2.0, createDate(new java.util.Date()), COUNCIL)
        val updatedQuestion = ProvisionUpdate(Some("updatetitle"), Some(2.1))
        val returnedQuestion = testQuestion.copy(description = updatedQuestion.description.get, amount = updatedQuestion.amount.get)
        Mockito.when(mockDb.updateQuestion(questionId, updatedQuestion.description, updatedQuestion.amount))
          .thenReturn(Future[Option[Provision]] { Some(returnedQuestion) })

        val future = questionService.updateQuestion(questionId.toString(), updatedQuestion)
        whenReady(future) {
          res => res should equal(Some(returnedQuestion))
        }
        Mockito.verify(mockDb).updateQuestion(questionId, updatedQuestion.description, updatedQuestion.amount)
      }
    }
    
    "when calling DELETE questions for aN existing question with JsonPayLoad" - {
      "should return false when delete operation returns false" in {
        val id = 21
        val testQuestion = Provision(Some(id), "test", "title", 4.0, createDate(new java.util.Date()), COUNCIL)
        val futureResult = false
        Mockito.when(mockDb.deleteQuestionById(testQuestion.questionId.get)).thenReturn(Future[Boolean] { futureResult })

        val future = questionService.deleteQuestion(id.toString)
        whenReady(future) {
          res =>
            res should equal(futureResult)
            Mockito.verify(mockDb).deleteQuestionById(id)
        }
      }
    }
    
    "when calling DELETE questions for aN existing question with JsonPayLoad" - {
      "should return true when delete operation returns true" in {
        val id = 22
        val testQuestion = Provision(Some(id), "11", "title", 1.0, createDate(new java.util.Date()), COUNCIL)
        val futureResult = true
        Mockito.when(mockDb.deleteQuestionById(testQuestion.questionId.get)).thenReturn(Future[Boolean] { futureResult })

        val future = questionService.deleteQuestion(id.toString())
        whenReady(future) {
          res =>
            res should equal(futureResult)
            Mockito.verify(mockDb).deleteQuestionById(id)
        }
      }
    }
    
    "when calling GET questions for an existing question with JsonPayLoad" - {
      "should return the stored Question" in {
        val testQuestion = Provision(None, "test", "title",5.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(mockDb.findQuestionById(testQuestion.user)).thenReturn(Future[Option[Provision]]{Some(testQuestion)})
        val future = questionService.getQuestion(testQuestion.user)
        whenReady(future) {
          res =>
            res.get should equal(testQuestion)
            Mockito.verify(mockDb).findQuestionById(testQuestion.user)
        }
      }
    }
    
    "when calling GET questions for a non existing question with JsonPayLoad" - {
      "should return None" in {
        val testQuestion = Provision(None, "test3", "title", 6.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(mockDb.findQuestionById(testQuestion.user)).thenReturn(Future[Option[Provision]]{None})
        val future = questionService.getQuestion(testQuestion.user)
        whenReady(future) {
          res =>
            res should equal(None)
            Mockito.verify(mockDb).findQuestionById(testQuestion.user)
        }
      }
    }
    
    "when calling GETAll questions for when there are questions with JsonPayLoad" - {
      "should return all the quesitons" in {
        val testQuestion = Provision(None, "test3", "title", 7.0, createDate(new java.util.Date()), COUNCIL )
        val questions =  for(i <- 1 to 5) yield testQuestion.copy(description=s"title$i")
        println("Questions are:"  +questions)
        Mockito.when(mockDb.findAllQuestions).thenReturn(Future[Seq[Provision]]{questions})
        val future = questionService.getAllQuestions
        whenReady(future) {
          res =>
            println("Result is :" + res)
            res should equal(questions)
            Mockito.verify(mockDb).findAllQuestions
        }
      }
    }
    
    "when calling GETquestionByProvisionType " - {
      "should return all the questions available for that type" in {
        val provType = COUNCIL
        val testQuestion = Provision(None, "test3", "title", 8.0, createDate(new java.util.Date()), provType )
        val questions =  for(i <- 1 to 5) yield testQuestion.copy(description=s"title$i")
        println("Questions are:"  +questions)
        Mockito.when(mockDb.findQuestionsByProvisionType(provType)).thenReturn(Future[Seq[Provision]]{questions})
        val future = questionService.getQuestionsByProvisionType(provType)
        whenReady(future) {
          res =>
            println("Result is :" + res)
            res should equal(questions)
            Mockito.verify(mockDb).findAllQuestions
        }
      }
    }
    
    
  }
}