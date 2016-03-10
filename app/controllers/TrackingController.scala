package controllers

import java.security.MessageDigest

import play.api._
import play.api.libs.Codecs
import play.api.mvc._
// Java8 lib
import java.util.Base64

class TrackingController extends Controller {

  val COOKIE_KEY = "Z_SYSTEM_3RD_PARTY_COOKIE_ID_SCALA"
  val COOKIE_MAX_AFTER_AGE = Some(31622400 + 31622400)

  //ref https://css-tricks.com/snippets/html/base64-encode-of-1x1px-transparent-gif/
  val onePixelGifBase64 = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
  val onePixelGifBytes = Base64.getDecoder().decode(onePixelGifBase64)

  def pixelTracking = Action {request =>
    val cookies = request.cookies
    val cookieValue = cookies.get(COOKIE_KEY).map { cookie =>
      Logger.debug(s"Cookie Exist! ${cookie.value}")
      cookie.value
    }.getOrElse {
      val newValue = uniqueIdGenerator()
      Logger.debug("Cookie Generate! $newValue")
      newValue
    }
    Ok(onePixelGifBytes).withCookies(Cookie(COOKIE_KEY, cookieValue, COOKIE_MAX_AFTER_AGE)).as("image/gif")
  }

  val uniqueIdGenerator = () => {
    val milliTime = System.currentTimeMillis()
    val nanoTime = System.nanoTime()
    val baseString = s"$milliTime $nanoTime"
    Logger.debug(baseString)

    val md = MessageDigest.getInstance("SHA-256")
    md.update(baseString.getBytes())

    val id = Codecs.toHexString(md.digest())
    Logger.debug(id)
    id
  }
}