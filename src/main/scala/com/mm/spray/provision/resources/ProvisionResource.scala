package com.mm.spray.provision.resources

import scala.util.{Failure, Success}
import com.mm.spray.provision.entities.{ProvisionUpdate, Provision}
import com.mm.spray.provision.routing.MyHttpService
import com.mm.spray.provision.services.ProvisionService
import spray.routing._
import spray.http._
import MediaTypes._
import com.mm.spray.provision.entities.ProvisionTypeEnum._
import com.mm.spray.provision.entities.ProvisionTypeEnum
import spray.http.StatusCodes._

trait ProvisionResource extends MyHttpService {
  val provisionService: ProvisionService
  
  def provisionRoutes: Route = pathPrefix("provisions") {
    pathEnd {
      post {
        entity(as[Provision]) { question =>
          completeWithLocationHeader(
            resourceId = provisionService.createProvision(question),
            ifDefinedStatus = Created.intValue, ifEmptyStatus = 409)
        }
      } ~
      get {
        complete(provisionService.getAllProvisions)
      }
    } ~
    path ("provisionType" / Segment){provisionType =>
      get {
        complete(provisionService.getProvisionsByProvisionType(ProvisionTypeEnum.withName(provisionType)))
      }
    } ~
    path(Segment) {id =>
      get {
        complete(provisionService.getProvisionById(id.toInt))
      } ~
      put {
        entity(as[ProvisionUpdate]) {update =>
          val q = provisionService.updateProvision(id, update)
          complete(q)
        }
      } ~
      delete {
        complete {
            provisionService.deleteProvision(id) map {
              case true => println("Its true. returning no content");spray.http.StatusCodes.NoContent
              case false => println("It sfalse. returning not found");  spray.http.StatusCodes.NotFound
              
            }
          }
        
        
      }
    }
    
    
  }
  
}