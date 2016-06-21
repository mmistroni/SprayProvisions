package com.mm.spray.provision

import com.mm.spray.provision.resources.ProvisionResource
import com.mm.spray.provision.services._
import spray.routing._

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import com.mm.spray.provision.persistence.PersistenceService
class RestInterface(productionFlag:Boolean)(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val persistence = new PersistenceService
  if (!productionFlag) {
    println("Not in prod. creating Schema..")
    persistence.createSchema() onSuccess { 
      case _ => persistence.createDataset()
    }
  }
  val provisionService = new PersistedProvisionService(persistence)

  val routes: Route = provisionRoutes

}

trait Resources extends ProvisionResource