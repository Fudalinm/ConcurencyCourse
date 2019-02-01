package webcrawler
import scala.concurrent.duration._

object WebCrawler extends App {
  import scala.concurrent._
  import ExecutionContext.Implicits.global
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL


  def finAllSubLinks(url: String):Seq[String] = {
    try {
    //url address contains our start
    val cleaner = new HtmlCleaner
    val props = cleaner.getProperties
    //val rootNode = cleaner.clean(html.mkString)
    val rootNode = cleaner.clean(new URL(url))
    val elements = rootNode.getElementsByName("a", true) map {
      elem =>
      elem.getAttributeByName("href")
    } map {
      //it means that we will proceed this link later we need to add current url + what we find
      case elem if !(elem contains "http") => url + '/' + elem
      /// we don't want to proceed this element in future deep iterations
      case elem =>  elem
    }
    //we return our list of urls
    //print(elements.toString())
    elements
    } catch{
      case _ => Seq(url)
    }
  }

  //we will proceed deep asynhronously
  val beggining = "https://www.google.pl"
  val deepLevel = 3
  var urlsSequence = Seq(beggining) //we hold here every url found

  for ( currentDeep <- 1 to deepLevel){
    //for each of out url we find sub urls
    val futureUrls = urlsSequence.map( url => Future{ finAllSubLinks(url)} )
    //now we need to wait fot all of them
      //in order for this we need to create One bigger Futture
      val wholeFuture = Future.sequence( futureUrls ).map(e => e.flatten)
      // yes this one is blocking but we wait only per level
      urlsSequence = Await.result(wholeFuture, 1 minutes)
    //we want to print everything what we have found
    println("\n\n######### Deep: " + currentDeep + " ###########" )
    urlsSequence.foreach(e => println(e))
    //we want to proceed only elements that have start in their names
    urlsSequence = urlsSequence.filter( e => e.contains(beggining) )
  }
}