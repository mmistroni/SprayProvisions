package com.mm.spray.provision.serializers

import java.sql.Timestamp

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JInt, JNull, JString}
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

object CustomSerializers {
  val all = List(CustomProvisionTypeEnumSerializer, CustomJodaLocalDateSerializer, CustomTimestampSerializer, CustomLocalDateSerializer)
}


case object CustomProvisionTypeEnumSerializer extends CustomSerializer[com.mm.spray.provision.entities.ProvisionTypeEnum.Value](
    format =>
  ({
    case JString(provisionTypeEnumString) =>  {
        com.mm.spray.provision.entities.ProvisionTypeEnum.withName(provisionTypeEnumString)
    }
    case JNull => null
  },
    {
      case provisionTypeValue: com.mm.spray.provision.entities.ProvisionTypeEnum.Value => {
        JString(provisionTypeValue.toString)
      }
    }))


case object CustomJodaLocalDateSerializer extends CustomSerializer[org.joda.time.LocalDate](format =>
  ({
    case JString(dateString) =>  {
        import org.joda.time.format.DateTimeFormatter
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        formatter.parseLocalDate(dateString)
    }
    case JNull => null
  },
    {
      case date: org.joda.time.LocalDate => {
        val fmt = DateTimeFormat.forPattern("yyyy-MM-dd")
        JString(date.toString(fmt))
      }
    }))



case object CustomLocalDateSerializer extends CustomSerializer[java.util.Date](format =>
  ({
    case JString(dateString) =>  {
        val formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
        val dt = formatter.parse(dateString)
        dt
        
    }
    case JNull => null
  },
    {
      case date: java.util.Date => {
        val dt =  new java.text.SimpleDateFormat("yyyy-MM-dd").format(date)
        JString(dt)
      }
    }))



case object CustomTimestampSerializer extends CustomSerializer[Timestamp](format =>
  ({
    case JInt(x) => new Timestamp(x.longValue * 1000)
    case JNull => null
  },
    {
      case date: Timestamp => JInt(date.getTime / 1000)
    }))