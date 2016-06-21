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
  
  override def createProvision(question: Provision): Future[Option[String]] =  {
    try {
      db.createProvision(question).map(q => Some(q.user))
    } catch {
      case e:Exception => println(e.toString());throw e
    }
    
  }
  
  override def updateProvision(id: String, update: ProvisionUpdate): Future[Option[Provision]] = {
    println("UPdating for Id:" + id + "updateQst:" + update)
    db.updateProvision(id.toInt, update.description, update.amount)
  }
  
  override def deleteProvision(id: String): Future[Boolean] =  {
    db.deleteProvisionById(id.toInt)
  }
  
  override def getProvision(id: String): Future[Option[Provision]] =  {
    db.findProvisionById(id)
  }
  
  override def getProvisionById(id: Int): Future[Option[Provision]] =  {
    db.findProvisionByProvisionId(id)
  
  }
  
  
  override def getProvisionsByProvisionDate(provisionDate:LocalDate): Future[Seq[Provision]] = {
    db.findProvisionByDate(provisionDate)
  }
  
  override def getAllProvisions:Future[Seq[Provision]] =  {
    db.findAllProvisions
  }

  override def getProvisionsByProvisionType(provisionType:ProvisionTypeEnum): Future[Seq[Provision]] = {
    db.findProvisionsByProvisionType(provisionType)
  }
  
  
  
}
