package withui

import cyou.shinobi9.cirrus.Cirrus
import cyou.shinobi9.cirrus.conf.CirrusConfig
import cyou.shinobi9.cirrus.handler.message.simpleMessageHandler
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.layout.HBox
import tornadofx.*

fun main() {

    val cirrus = Cirrus(
        CirrusConfig(
            messageHandler = simpleMessageHandler {
                onReceiveDanmaku { user, said ->
                    DanmakuView.observableList!!.add(Danmaku(user, said))
                }
            }
        )
    )
    cirrus.connectToBLive(958282)
    launch<TestApplication>()
}

class TestApplication : App(DanmakuView::class)

data class Danmaku(var user: String, var said: String)

class DanmakuView : View() {
    private val danmakus = FXCollections.observableArrayList<Danmaku>()

    companion object {
        var observableList: ObservableList<Danmaku>? = null
    }

    override val root: Parent = HBox(
        tableview(danmakus) {
            column("user", Danmaku::user)
            column("said", Danmaku::said)
            observableList = danmakus
        }

    )
}
