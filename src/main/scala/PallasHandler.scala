import com.typesafe.config.{Config, ConfigFactory}
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class PallasHandler extends TelegramLongPollingBot {
  override def onUpdateReceived(update: Update): Unit = ???

  override def getBotUsername: String = PallasHandler.getBotUsername

  override def getBotToken: String = PallasHandler.getBotToken
}


object PallasHandler {
  val conf: Config = ConfigFactory.load()

  def getBotUsername: String = conf.getString("bot-name")
  def getBotToken: String = conf.getString("bot-token")
}