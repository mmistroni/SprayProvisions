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

  val provisionService = new PersistedProvisionService(mockDb)

  "The PersistedProvisionService" - {
    "when calling createProvision it should call ProvisionService.creteProvision" - {
      "should return the created Provision" in {
        val testProvision = Provision(None, "1", "title", 1.0, createDate(new java.util.Date()), COUNCIL)
        Mockito.when(mockDb.createProvision(testProvision)).thenReturn(Future[Provision] { testProvision })
        val future = provisionService.createProvision(testProvision)
        whenReady(future) {
          res => res should equal(Some(testProvision.user))
        }
        Mockito.verify(mockDb).createProvision(testProvision)
      }
    }
    "when calling updateProvision it should call ProvisionService.updatedProvision" - {
      "should return the updatedProvision Provision" in {
        val questionId = 11
        val testProvision = Provision(Some(questionId), "2", "title", 2.0, createDate(new java.util.Date()), COUNCIL)
        val updatedQuestion = ProvisionUpdate(Some("updatetitle"), Some(2.1))
        val returnedQuestion = testProvision.copy(description = updatedQuestion.description.get, amount = updatedQuestion.amount.get)
        Mockito.when(mockDb.updateProvision(questionId, updatedQuestion.description, updatedQuestion.amount))
          .thenReturn(Future[Option[Provision]] { Some(returnedQuestion) })

        val future = provisionService.updateProvision(questionId.toString(), updatedQuestion)
        whenReady(future) {
          res => res should equal(Some(returnedQuestion))
        }
        Mockito.verify(mockDb).updateProvision(questionId, updatedQuestion.description, updatedQuestion.amount)
      }
    }
    
    "when calling DELETE provisions for aN existing provision with JsonPayLoad" - {
      "should return false when delete operation returns false" in {
        val id = 21
        val testProvision = Provision(Some(id), "test", "title", 4.0, createDate(new java.util.Date()), COUNCIL)
        val futureResult = false
        Mockito.when(mockDb.deleteProvisionById(testProvision.provisionId.get)).thenReturn(Future[Boolean] { futureResult })

        val future = provisionService.deleteProvision(id.toString)
        whenReady(future) {
          res =>
            res should equal(futureResult)
            Mockito.verify(mockDb).deleteProvisionById(id)
        }
      }
    }
    
    "when calling DELETE provisions for aN existing provision with JsonPayLoad" - {
      "should return true when delete operation returns true" in {
        val id = 22
        val testProvision = Provision(Some(id), "11", "title", 1.0, createDate(new java.util.Date()), COUNCIL)
        val futureResult = true
        Mockito.when(mockDb.deleteProvisionById(testProvision.provisionId.get)).thenReturn(Future[Boolean] { futureResult })

        val future = provisionService.deleteProvision(id.toString())
        whenReady(future) {
          res =>
            res should equal(futureResult)
            Mockito.verify(mockDb).deleteProvisionById(id)
        }
      }
    }
    
    "when calling GET provisions for an existing provision with JsonPayLoad" - {
      "should return the stored provision" in {
        val testProvision = Provision(None, "test", "title",5.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(mockDb.findProvisionById(testProvision.user)).thenReturn(Future[Option[Provision]]{Some(testProvision)})
        val future = provisionService.getProvision(testProvision.user)
        whenReady(future) {
          res =>
            res.get should equal(testProvision)
            Mockito.verify(mockDb).findProvisionById(testProvision.user)
        }
      }
    }
    
    "when calling GET provisions for a non existing provision with JsonPayLoad" - {
      "should return None" in {
        val testProvision = Provision(None, "test3", "title", 6.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(mockDb.findProvisionById(testProvision.user)).thenReturn(Future[Option[Provision]]{None})
        val future = provisionService.getProvision(testProvision.user)
        whenReady(future) {
          res =>
            res should equal(None)
            Mockito.verify(mockDb).findProvisionById(testProvision.user)
        }
      }
    }
    
    "when calling GETAll provisions for when there are provisons with JsonPayLoad" - {
      "should return all the provisions" in {
        val testProvision = Provision(None, "test3", "title", 7.0, createDate(new java.util.Date()), COUNCIL )
        val provisions =  for(i <- 1 to 5) yield testProvision.copy(description=s"title$i")
        println("Questions are:"  +provisions)
        Mockito.when(mockDb.findAllProvisions).thenReturn(Future[Seq[Provision]]{provisions})
        val future = provisionService.getAllProvisions
        whenReady(future) {
          res =>
            println("Result is :" + res)
            res should equal(provisions)
            Mockito.verify(mockDb).findAllProvisions
        }
      }
    }
    
    "when calling GETprovisionByProvisionType " - {
      "should return all the provisions available for that type" in {
        val provType = COUNCIL
        val testProvision = Provision(None, "test3", "title", 8.0, createDate(new java.util.Date()), provType )
        val provisions =  for(i <- 1 to 5) yield testProvision.copy(description=s"title$i")
        println("provisions are:"  +provisions)
        Mockito.when(mockDb.findProvisionsByProvisionType(provType)).thenReturn(Future[Seq[Provision]]{provisions})
        val future = provisionService.getProvisionsByProvisionType(provType)
        whenReady(future) {
          res =>
            println("Result is :" + res)
            res should equal(provisions)
            Mockito.verify(mockDb).findAllProvisions
        }
      }
    }
    
    "when calling GETprovisionByDate " - {
      "should return all the provisions newer than supplied date" in {
        val provType = COUNCIL
        val provDateStr = "2015-04-04"
        val locDate = LocalDate.parse(provDateStr)
        val testProvision = Provision(None, "test3", "title", 8.0, createDate(new java.util.Date()), provType )
        val provisions =  for(i <- 1 to 5) yield testProvision.copy(description=s"title$i")
        println("provisions are:"  +provisions)
        Mockito.when(mockDb.findProvisionByDate(locDate)).thenReturn(Future[Seq[Provision]]{provisions})
        val future = provisionService.getProvisionsByProvisionDate(locDate)
        whenReady(future) {
          res =>
            println("Result is :" + res)
            res should equal(provisions)
            Mockito.verify(mockDb).findAllProvisions
        }
      }
    }
    
  }
}