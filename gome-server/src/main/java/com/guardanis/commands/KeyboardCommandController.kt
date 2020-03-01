package com.guardanis.commands

import com.guardanis.JsonHelper
import org.json.simple.JSONArray

import java.awt.*
import java.awt.event.KeyEvent
import java.util.HashMap
import java.util.Locale

class KeyboardCommandController @Throws(Exception::class)
constructor(): CommandController {

    private var robot: Robot = Robot()

    @Throws(Exception::class)
    override fun process(command: Command) {
        val wrappedKeys = command.getData()
                ?.getOrDefault("wrapped", null)
                .let({ it as? JSONArray ?: JSONArray() })
                .mapNotNull({ Keys.vkWrappables[it] })

        wrappedKeys.forEach({ robot.keyPress(it) })

        when (JsonHelper.getString("type", command.getData())) {
            "string" -> {
                JsonHelper.getString("value", command.getData())
                        .map(Keys::bestVkCode)
                        .forEach(::press)
            }
            "action" -> {
                JsonHelper.getString("value", command.getData())
                        .let(Keys::bestActionCode)
                        .also({ robot.keyPress(it) })
                        .also({ robot.keyRelease(it) })
            }
            "key_code_action" -> {
                JsonHelper.getInt("value", command.getData())
                        .also({ robot.keyPress(it) })
                        .also({ robot.keyRelease(it) })
            }
        }

        wrappedKeys.forEach({ robot.keyRelease(it) })
    }

    private fun press(key: Pair<Int, Boolean>) {
        if (key.second)
            robot.keyPress(KeyEvent.VK_SHIFT)

        robot.keyPress(key.first)
        robot.keyRelease(key.first)

        if (key.second)
            robot.keyRelease(KeyEvent.VK_SHIFT)
    }
}
