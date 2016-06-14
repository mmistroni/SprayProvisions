package com.mm.spray.provision.resources

import scala.concurrent.Future
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest
import com.mm.spray.provision.entities._ 
import com.mm.spray.provision.services.ProvisionService
import org.mockito.{ Mockito, Matchers=>MockitoMatchers}
import org.joda.time.LocalDate
import com.mm.spray.provision.serializers.JsonSupport
import com.mm.spray.provision.entities.ProvisionTypeEnum._
    

class ProvisionResourceSpec extends FreeSpec with  ScalatestRouteTest with ProvisionResource with Matchers
      with JsonSupport {
  def actorRefFactory = system
  val provisionService = Mockito.mock(classOf[ProvisionService]) //
  val executionContext = scala.concurrent.ExecutionContext.Implicits.global
  
  def realValue:Future[Option[String]] = Future{
    Some("1")
  }
  
  def createDate(dt:java.util.Date):org.joda.time.LocalDate =  {
    new LocalDate(dt)    
  }
  
  
  "The ProvisionResource" - {
    "when calling PUT provisions with JsonPayLoad" - {
      "should return 201" in {
        val testQuestion = Provision(None, "1", "title", 1.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.createQuestion(testQuestion)).thenReturn(Future[Option[String]]{Some("1")})
        
        Post("/provisions", testQuestion) ~> questionRoutes ~> check {
          status should equal(spray.http.StatusCodes.Created)
        }
      }
    }
    
    "when calling PUT provisions for an existing question with JsonPayLoad" - {
      "should return 406" in {
        val testQuestion = Provision(None, "2", "title", 2.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.createQuestion(testQuestion)).thenReturn(Future[Option[String]]{None})
        Post("/provisions", testQuestion) ~> questionRoutes ~> check {
          status should equal(spray.http.StatusCodes.Conflict)
        }
      }
    }
    "when calling GET provisions for an existing question with JsonPayLoad" - {
      "should return the stored Question" in {
        val testQuestion = Provision(None, "3", "title", 3.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.getQuestionById(testQuestion.user.toInt)).thenReturn(Future[Option[Provision]]{Some(testQuestion)})
        Get(s"/provisions/${testQuestion.user}") ~> questionRoutes ~> check {
          val returnedQuestion  =responseAs[Provision]
          returnedQuestion should equal(testQuestion)
        }
      }
    }
    
    "when calling GET provisions for a non existing question with JsonPayLoad" - {
      "should return No Content" in {
        val testQuestion = Provision(None, "4", "title", 4.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.getQuestionById(testQuestion.user.toInt)).thenReturn(Future[Option[Provision]]{None})
        Get(s"/provisions/${testQuestion.user}") ~> questionRoutes ~> check {
          
          status should equal(spray.http.StatusCodes.NotFound)
          
        }
      }
    }
    
    "when calling GET provisions for all provisions" - {
      "should return all persisted provisions" in {
        val testQuestion = Provision(None, "5", "title", 5.0, createDate(new java.util.Date()), COUNCIL )
        val testQuestion2 = testQuestion.copy(user="fo")
        val allQuestions  = Vector(testQuestion, testQuestion2)
        Mockito.when(provisionService.getAllQuestions).thenReturn(Future[Seq[Provision]]{allQuestions})
        Get("/provisions") ~> questionRoutes ~> check {
          val returnedQuestions  =responseAs[Seq[Provision]]
          returnedQuestions should equal(allQuestions)
        }
      }
    }
    
    "when calling GET provisions with a provisionType" - {
      "should return all persisted questtions for that type" in {
        val provType  = COUNCIL
        val testQuestion = Provision(None, "6", "title", 6.0, createDate(new java.util.Date()), provType)
        val testQuestion2 = testQuestion.copy(user="fo")
        val allQuestions  = Vector(testQuestion, testQuestion2)
        Mockito.when(provisionService.getQuestionsByProvisionType(provType)).thenReturn(Future[Seq[Provision]]{allQuestions})
        Get("/provisions/provisionType/" + provType.toString()) ~> questionRoutes ~> check {
          val returnedQuestions  =responseAs[Seq[Provision]]
          returnedQuestions should equal(allQuestions)
        }
      }
    }
    
    
    "when calling DELETE provisions for aN existing question with JsonPayLoad" - {
      "should return Failure when delete operation returns false" in {
        val testQuestion = Provision(None ,"7", "title", 7.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.deleteQuestion(testQuestion.user)).thenReturn(Future[Boolean]{false})
        Delete(s"/provisions/${testQuestion.user}") ~> questionRoutes ~> check {
          println(s"Status for false  is:$status")
          
          status should equal(spray.http.StatusCodes.NotFound)

        }
        Mockito.verify(provisionService).deleteQuestion(testQuestion.user)
        
      }
    }
    
    "when calling DELETE provisions for aN existing question with JsonPayLoad" - {
      "should return Success when delete operation returns true" in {
        val testQuestion = Provision(None ,"8", "title", 8.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.deleteQuestion(testQuestion.user)).thenReturn(Future[Boolean]{true})
        Delete(s"/provisions/${testQuestion.user}") ~> questionRoutes ~> check {
          println(s"Status for true  is:$status")
          status should equal(spray.http.StatusCodes.NoContent)

        }
        Mockito.verify(provisionService).deleteQuestion(testQuestion.user)
        
      }
    }
    
    
    "when calling UPDATE provisions for a non-existing question with JsonPayLoad" - {
      "should return No Content" in {
        val testQuestion = Provision(None ,"9", "title", 9.0, createDate(new java.util.Date()), COUNCIL )
        val questionUpdate = ProvisionUpdate(Some("updateTitle"), Some(9.1))
        Mockito.when(provisionService.updateQuestion(testQuestion.user, questionUpdate)).thenReturn(Future{None})
        Put(s"/provisions/${testQuestion.user}", questionUpdate) ~> questionRoutes ~> check {
          status should equal(spray.http.StatusCodes.NotFound)
        }
        
        
      }
    }
    
    "when calling UPDATE provisions for an existing question with JsonPayLoad" - {
      "should return the updated question" in {
        val dt = createDate(new java.util.Date())
        val testQuestion = Provision(None ,"10", "title", 10.0, dt, COUNCIL )
        val questionUpdate = ProvisionUpdate(Some("updateTitle"), Some(10.1))
        val updatedQuestion = testQuestion.copy(description=questionUpdate.description.get, amount=questionUpdate.amount.get, questionDate=dt)
        
        Mockito.when(provisionService.updateQuestion(testQuestion.user, questionUpdate)).thenReturn(Future[Option[Provision]]{Some(updatedQuestion)})
        Put(s"/provisions/${testQuestion.user}", questionUpdate) ~> questionRoutes ~> check {
          val returnedQuestion  =responseAs[Provision]
          returnedQuestion should equal(updatedQuestion)
          status should equal(spray.http.StatusCodes.OK)
        }
        
        
      }
    }
    
  }
}