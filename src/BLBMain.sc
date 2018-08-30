import java.io._

import com.google.gson._

val newData = 0
val basePath = "C:/Users/TJ/Scala/BanterLeagueBotFiles/"
//if (newData == 1) {
//  //read config params
//
//  def getConfig(path: String) = scala.io.Source.fromFile(path).mkString
//
//  val path = "C:/Users/TJ/Scala/BanterLeagueBotFiles/Config_Params.txt"
//  val configParams = new JsonParser().parse(getConfig(path)).getAsJsonObject
//  val tokenID = configParams.get("tokenID").getAsString
//  val groupID = configParams.get("groupID").getAsString
//  val limit = configParams.get("messageLimit").getAsString
//
//  //read data from api
//  def getData(url: String) = scala.io.Source.fromURL(url, "UTF-8").mkString
//
//  val requestURL = "https://api.groupme.com/v3/groups/" + groupID + "/messages?limit=" + limit + "&token=" + tokenID
//  //val requestURL = "https://api.groupme.com/v3/groups/" + groupID + "?token=" + tokenID
//  val groupData = getData(requestURL)
//  val groupDataJson = new JsonParser().parse(groupData).getAsJsonObject
//  var totalMsgCount = groupDataJson.get("response").getAsJsonObject.get("count").getAsInt
//  val messages = groupDataJson.get("response").getAsJsonObject.get("messages").getAsJsonArray
//  var lastMsgID = messages.get(messages.size()-1).getAsJsonObject.get("id").getAsBigInteger
//
//  //read in the next however many records, 100 at a time
//  while (totalMsgCount > 100){
//    println(totalMsgCount)
//    println(lastMsgID)
//    val newRequestURL = "https://api.groupme.com/v3/groups/" + groupID + "/messages?limit=" + limit + "&before_id=" + lastMsgID + "&token=" + tokenID
//    val data = new JsonParser().parse(getData(newRequestURL)).getAsJsonObject
//    val newMsgs = data.get("response").getAsJsonObject.get("messages").getAsJsonArray
//
//    lastMsgID = newMsgs.get(newMsgs.size()-1).getAsJsonObject.get("id").getAsBigInteger
//    messages.addAll(newMsgs)
//
//    totalMsgCount -= 100
//  }
//
//  //pretty print
//  val outputJSON = new GsonBuilder().setPrettyPrinting().create().toJson(messages)
//  val outName = "C:/Users/TJ/Scala/BanterLeagueBotFiles/messages.txt"
//  val file = new File(outName)
//  val bw = new BufferedWriter(new FileWriter(file))
//  bw.write(outputJSON)
//  bw.close()
//} else {
//
//
//}


//case class structure for messages
//case class attachment(loci: Option[Array[Int]] , mtype: String, user_ids: option[Array[String]], url: Option[String])
//case class message(attachment: attachment, avatarURL: String, created_at: BigInt, favorited_by: )
//case class response()

def getFileAsJsonObject( path: String ) = new JsonParser().parse(scala.io.Source.fromFile(path).mkString).getAsJsonObject
def getFileAsJsonArray( path: String ) = new JsonParser().parse(scala.io.Source.fromFile(path).mkString).getAsJsonArray
val members = getFileAsJsonObject(basePath + "members.txt")

val memberList = members.get("response").getAsJsonObject.get("members").getAsJsonArray

val curMembers = (0 until memberList.size()).map{ e =>
                                  memberList.get(e).getAsJsonObject.get("user_id") ->
                                  memberList.get(e).getAsJsonObject.get("nickname")}.toMap

val messages = getFileAsJsonArray(basePath + "messages.txt")

def getAttr(x: JsonObject, attr: String) = x.get(attr).getAsString

def jsonMessageToArray(message : JsonObject): Array[String] = {
  val message_id = getAttr(message, "id")
  val user_id = getAttr(message, "user_id")
  val user_name = getAttr(message, "name")
  val created_at = getAttr(message, "created_at")
  val source_type = getAttr(message, "source_guid")
  //val text = getAttr(message, "text")

  var likes = 0.toString
  if (message.get("favorited_by").getAsJsonArray.contains(message.get("user_id"))){
    likes = (message.get("favorited_by").getAsJsonArray.size() - 2).toString
  }else{
    likes = message.get("favorited_by").getAsJsonArray.size().toString
  }

  var linked_gif = 0.toString
  var linked_video = 0.toString
  var linked_image = 0.toString
  if ( message.get("attachments").getAsJsonArray.size() > 0 ) {
    if (message.get("attachments").getAsJsonArray.get(0).getAsJsonObject.get("type").getAsString == "image"
      || message.get("attachments").getAsJsonArray.get(0).getAsJsonObject.get("type").getAsString == "linked_image") {
      if (message.get("attachments").getAsJsonArray.get(0).getAsJsonObject.get("url").getAsString.contains(".gif.")) {
        linked_gif = 1.toString
      } else {
        linked_image = 1.toString
      }
    } else if (message.get("attachments").getAsJsonArray.get(0).getAsJsonObject.get("type").getAsString == "video") {
      linked_video = 1.toString
    } else {

    }
  }
  Array(message_id, user_id, user_name, created_at, source_type, likes, linked_video, linked_gif, linked_image)

}

val messageArrayArray = (0 until messages.size()).map{e => jsonMessageToArray(messages.get(e).getAsJsonObject)}.toArray

val messageArray = messageArrayArray.map{ _.mkString(" | ")}

val outName = basePath + "messages_delimited.txt"
val file = new File(outName)
val bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
//for ( x <- messageArray){
//  bw.write(x + "\n")
//}
messageArray.foreach(e => bw.write(e + "\n"))
bw.close()