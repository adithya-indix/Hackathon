import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator

val url =  "http://www.amazonsupply.com/dp/B003OBZ05W;jsessionid=AAF6FD89AE32CADA6E6AF4425E97B4F9?ref_=sr_31158_50_txt"
//val regex: String = "(.*);jsessionid=.*$"
val regex: Regex = "(.*)b".r

val matcher: MatchIterator = regex findAllIn "aaabc"

matcher.group(1)