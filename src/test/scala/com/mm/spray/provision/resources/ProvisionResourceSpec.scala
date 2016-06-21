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
        val testProvision = Provision(None, "1", "title", 1.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.createProvision(testProvision)).thenReturn(Future[Option[String]]{Some("1")})
        
        Post("/provisions", testProvision) ~> provisionRoutes ~> check {
          status should equal(spray.http.StatusCodes.Created)
        }
      }
    }
    
    "when calling PUT provisions for an existing provision with JsonPayLoad" - {
      "should return 406" in {
        val testProvision = Provision(None, "2", "title", 2.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.createProvision(testProvision)).thenReturn(Future[Option[String]]{None})
        Post("/provisions", testProvision) ~> provisionRoutes ~> check {
          status should equal(spray.http.StatusCodes.Conflict)
        }
      }
    }
    "when calling GET provisions for an existing provision with JsonPayLoad" - {
      "should return the stored provision" in {
        val testProvision = Provision(None, "3", "title", 3.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.getProvisionById(testProvision.user.toInt)).thenReturn(Future[Option[Provision]]{Some(testProvision)})
        Get(s"/provisions/${testProvision.user}") ~> provisionRoutes ~> check {
          val returnedprovision  =responseAs[Provision]
          returnedprovision should equal(testProvision)
        }
      }
    }
    
    "when calling GET provisions for a non existing provision with JsonPayLoad" - {
      "should return No Content" in {
        val testProvision = Provision(None, "4", "title", 4.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.getProvisionById(testProvision.user.toInt)).thenReturn(Future[Option[Provision]]{None})
        Get(s"/provisions/${testProvision.user}") ~> provisionRoutes ~> check {
          
          status should equal(spray.http.StatusCodes.NotFound)
          
        }
      }
    }
    
    "when calling GET provisions for all provisions" - {
      "should return all persisted provisions" in {
        val testProvision = Provision(None, "5", "title", 5.0, createDate(new java.util.Date()), COUNCIL )
        val testProvision2 = testProvision.copy(user="fo")
        val allProvisions  = Vector(testProvision, testProvision2)
        Mockito.when(provisionService.getAllProvisions).thenReturn(Future[Seq[Provision]]{allProvisions})
        Get("/provisions") ~> provisionRoutes ~> check {
          val returnedprovisions  =responseAs[Seq[Provision]]
          returnedprovisions should equal(allProvisions)
        }
      }
    }
    
    "when calling GET provisions with a provisionType" - {
      "should return all persisted questtions for that type" in {
        val provType  = COUNCIL
        val testProvision = Provision(None, "6", "title", 6.0, createDate(new java.util.Date()), provType)
        val testProvision2 = testProvision.copy(user="fo")
        val allProvisions  = Vector(testProvision, testProvision2)
        Mockito.when(provisionService.getProvisionsByProvisionType(provType)).thenReturn(Future[Seq[Provision]]{allProvisions})
        Get("/provisions/provisionType/" + provType.toString()) ~> provisionRoutes ~> check {
          val returnedprovisions  =responseAs[Seq[Provision]]
          returnedprovisions should equal(allProvisions)
        }
      }
    }
    
    "when calling GET provisions with a provisionDate" - {
      "should return all persisted provisions newer than the date" in {
        val provDateStr  = "2015-01-01"
        import org.joda.time.LocalDate
        val locDate = LocalDate.parse(provDateStr)
        val testProvision = Provision(None, "6", "title", 6.0, createDate(new java.util.Date()), COUNCIL)
        val testProvision2 = testProvision.copy(user="fo")
        val allProvisions  = Vector(testProvision, testProvision2)
        Mockito.when(provisionService.getProvisionsByProvisionDate(locDate)).thenReturn(Future[Seq[Provision]]{allProvisions})
        Get("/provisions/provisionDate/" + provDateStr) ~> provisionRoutes ~> check {
          val returnedprovisions  =responseAs[Seq[Provision]]
          returnedprovisions should equal(allProvisions)
        }
      }
    }
    
    
    
    "when calling DELETE provisions for aN existing provision with JsonPayLoad" - {
      "should return Failure when delete operation returns false" in {
        val testProvisions = Provision(None ,"7", "title", 7.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.deleteProvision(testProvisions.user)).thenReturn(Future[Boolean]{false})
        Delete(s"/provisions/${testProvisions.user}") ~> provisionRoutes ~> check {
          println(s"Status for false  is:$status")
          
          status should equal(spray.http.StatusCodes.NotFound)

        }
        Mockito.verify(provisionService).deleteProvision(testProvisions.user)
        
      }
    }
    
    "when calling DELETE provisions for aN existing provision with JsonPayLoad" - {
      "should return Success when delete operation returns true" in {
        val testProvision = Provision(None ,"8", "title", 8.0, createDate(new java.util.Date()), COUNCIL )
        Mockito.when(provisionService.deleteProvision(testProvision.user)).thenReturn(Future[Boolean]{true})
        Delete(s"/provisions/${testProvision.user}") ~> provisionRoutes ~> check {
          println(s"Status for true  is:$status")
          status should equal(spray.http.StatusCodes.NoContent)

        }
        Mockito.verify(provisionService).deleteProvision(testProvision.user)
        
      }
    }
    
    
    "when calling UPDATE provisions for a non-existing provision with JsonPayLoad" - {
      "should return No Content" in {
        val testProvision = Provision(None ,"9", "title", 9.0, createDate(new java.util.Date()), COUNCIL )
        val provisionUpdate = ProvisionUpdate(Some("updateTitle"), Some(9.1))
        Mockito.when(provisionService.updateProvision(testProvision.user, provisionUpdate)).thenReturn(Future{None})
        Put(s"/provisions/${testProvision.user}", provisionUpdate) ~> provisionRoutes ~> check {
          status should equal(spray.http.StatusCodes.NotFound)
        }
        
        
      }
    }
    
    "when calling UPDATE provisions for an existing provision with JsonPayLoad" - {
      "should return the updated provision" in {
        val dt = createDate(new java.util.Date())
        val testProvision = Provision(None ,"10", "title", 10.0, dt, COUNCIL )
        val provisionUpdate = ProvisionUpdate(Some("updateTitle"), Some(10.1))
        val updatedProvision = testProvision.copy(description=provisionUpdate.description.get, amount=provisionUpdate.amount.get, provisionDate=dt)
        
        Mockito.when(provisionService.updateProvision(testProvision.user, provisionUpdate)).thenReturn(Future[Option[Provision]]{Some(updatedProvision)})
        Put(s"/provisions/${testProvision.user}", provisionUpdate) ~> provisionRoutes ~> check {
          val returnedprovision  =responseAs[Provision]
          returnedprovision should equal(updatedProvision)
          status should equal(spray.http.StatusCodes.OK)
        }
        
        
      }
    }
    
  }
}