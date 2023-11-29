package org.playgrounddispatcher

import LinkManager
import android.content.Context
import androidx.startup.Initializer
import linkManager

class LinkManagerInitializer : Initializer<LinkManager> {
    override fun create(context: Context): LinkManager {
        val linkManager = linkManager
        linkManager.setUp(context)
        return linkManager
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
