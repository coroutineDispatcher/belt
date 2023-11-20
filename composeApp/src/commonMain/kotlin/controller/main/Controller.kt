package controller.main

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Elements
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val PROPERTY_TITLE = "og:title"
private const val PROPERTY_IMAGE = "og:image"
private const val PROPERTY_URL = "og:url"

class MainScreenControllerImpl : MainScreenController {
    override fun getWebProperties() {
        GlobalScope.launch {
            val document =
                Ksoup.connect("https://kotlinlang.org/docs/multiplatform-add-dependencies.html")
            println(document.title())
            val headlines: Elements = document.select("meta")

            headlines.forEach { element ->
                when (element.attr("property")) {
                    PROPERTY_TITLE -> {
                        println("Extracting title: ${element.attr("content")}")
                    }

                    PROPERTY_IMAGE -> {
                        println("Extracting title: ${element.attr("content")}")
                    }

                    PROPERTY_URL -> {
                        println("Extracting title: ${element.attr("content")}")
                    }

                    else -> Unit
                }
            }
        }
    }
}
