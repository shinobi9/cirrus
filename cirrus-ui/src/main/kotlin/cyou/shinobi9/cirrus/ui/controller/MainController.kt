package cyou.shinobi9.cirrus.ui.controller

import cyou.shinobi9.cirrus.ui.model.Danmaku
import cyou.shinobi9.cirrus.ui.model.DanmakuModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.Controller
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlin.random.nextInt

class MainController : Controller(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
//    private val backend = Cirrus()

    fun connect(danmakuModel: DanmakuModel) {
        launch {
            while (true) {
                delay(2000)
                danmakuModel.observableDanmakuList.add(Danmaku("shinobi", Random.nextInt(1..10).toString()))
            }
        }
    }
}
