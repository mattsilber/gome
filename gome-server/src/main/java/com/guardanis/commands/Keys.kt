package com.guardanis.commands

import java.awt.event.KeyEvent

object Keys {

    val vkGeneral: Map<String, Pair<Int, Boolean>> = mapOf(
            Pair("~", Pair(KeyEvent.VK_DEAD_TILDE, false)),
            Pair("`", Pair(KeyEvent.VK_BACK_QUOTE, false)),
            Pair("!", Pair(KeyEvent.VK_1, true)),
            Pair("@", Pair(KeyEvent.VK_2, true)),
            Pair("#", Pair(KeyEvent.VK_3, true)),
            Pair("$", Pair(KeyEvent.VK_4, true)),
            Pair("%", Pair(KeyEvent.VK_5, true)),
            Pair("^", Pair(KeyEvent.VK_6, true)),
            Pair("&", Pair(KeyEvent.VK_7, true)),
            Pair("*", Pair(KeyEvent.VK_8, true)),
            Pair("(", Pair(KeyEvent.VK_9, true)),
            Pair(")", Pair(KeyEvent.VK_0, true)),
            Pair("_", Pair(KeyEvent.VK_MINUS, true)),
            Pair("-", Pair(KeyEvent.VK_MINUS, false)),
            Pair("+", Pair(KeyEvent.VK_EQUALS, true)),
            Pair("=", Pair(KeyEvent.VK_EQUALS, false)),
            Pair("/", Pair(KeyEvent.VK_SLASH, false)),
            Pair("?", Pair(KeyEvent.VK_SLASH, true)),
            Pair("\\", Pair(KeyEvent.VK_BACK_SLASH, false)),
            Pair("|", Pair(KeyEvent.VK_BACK_SLASH, true)),
            Pair(":", Pair(KeyEvent.VK_SEMICOLON, true)),
            Pair(";", Pair(KeyEvent.VK_SEMICOLON, false)),
            Pair("'", Pair(KeyEvent.VK_QUOTE, false)),
            Pair("\"", Pair(KeyEvent.VK_QUOTE, true)),
            Pair(",", Pair(KeyEvent.VK_COMMA, false)),
            Pair("<", Pair(KeyEvent.VK_COMMA, true)),
            Pair(".", Pair(KeyEvent.VK_PERIOD, false)),
            Pair(">", Pair(KeyEvent.VK_PERIOD, true))
    )

    val vkWrappables: Map<String, Int> = mapOf(
            Pair("ALT", KeyEvent.VK_ALT),
            Pair("CTRL", KeyEvent.VK_CONTROL),
            Pair("SHIFT", KeyEvent.VK_SHIFT)
    )

    val vkActions: Map<String, Int> = mapOf(
            Pair("LEFT", KeyEvent.VK_LEFT),
            Pair("UP", KeyEvent.VK_UP),
            Pair("RIGHT", KeyEvent.VK_RIGHT),
            Pair("DOWN", KeyEvent.VK_DOWN)
    )

    fun bestVkCode(value: Char): Pair<Int, Boolean> {
        return vkGeneral[value.toString()]
                ?: Pair(java.lang.Character.toUpperCase(value).toInt(), Character.isUpperCase(value))
    }

    fun bestActionCode(value: String): Int {
        return vkActions[value]
                ?: value.firstOrNull()?.let(::bestVkCode)?.first
                ?: KeyEvent.VK_ESCAPE
    }
}