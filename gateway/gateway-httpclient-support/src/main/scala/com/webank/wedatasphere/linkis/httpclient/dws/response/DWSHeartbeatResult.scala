/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.linkis.httpclient.dws.response

import java.util

//import com.ning.http.client.Response
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import com.webank.wedatasphere.linkis.httpclient.discovery.HeartbeatResult

/**
  * created by cooperyang on 2019/5/22.
  */
//class DWSHeartbeatResult(response: Response, serverUrl: String) extends HeartbeatResult with DWSResult {
class DWSHeartbeatResult(response: HttpResponse, serverUrl: String) extends HeartbeatResult with DWSResult {

  var entity = response.getEntity
  var responseBody: String = null
  if (entity != null) {
    responseBody = EntityUtils.toString(entity, "UTF-8")
  }
  val statusCode: Int = response.getStatusLine.getStatusCode
  val url: String = serverUrl
  val contentType: String = entity.getContentType.getValue
  set(responseBody, statusCode, url, contentType)

//  set(response.getResponseBody, response.getStatusCode, response.getUri.toString, response.getContentType)
  if(getStatus != 0) warn(s"heartbeat to gateway $serverUrl failed! message: $getMessage.")
  override val isHealthy: Boolean = getData.get("isHealthy") match {
    case b: java.lang.Boolean => b
    case s if s != null => s.toString.toBoolean
    case _ => false
  }

  def getGatewayList: Array[String] = getData.get("gatewayList") match {
    case l: util.List[String] => l.toArray(new Array[String](l.size())).map("http://" + _)
    case array: Array[String] => array.map("http://" + _)
    case _ => Array.empty
  }

}
