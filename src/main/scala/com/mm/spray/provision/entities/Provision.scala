package com.mm.spray.provision.entities
import org.joda.time.LocalDate
import com.mm.spray.provision.entities.ProvisionTypeEnum._

case class Provision(questionId:Option[Int], user:String, description:String, amount:Double, provisionDate:LocalDate,
                    provisionType:ProvisionTypeEnum)