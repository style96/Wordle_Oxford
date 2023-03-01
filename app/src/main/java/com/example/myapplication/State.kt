package com.example.myapplication

import android.widget.Button

enum class Signal {
    NOTAWORD,
    NEEDLETTER,
    NEXTTRY,
    GAMEOVER,
    WIN
}

enum class Status {
    DEFAULT,
    CORRECT,
    WRONGPOSITION,
    INCORRECT,
}

sealed class SStatus {
    class DEFAULT(var button: Button) : SStatus()
    class CORRECT(var button: Button) : SStatus()
    class WRONGPOSITION(var button: Button) : SStatus()
    class INCORRECT(var button: Button) : SStatus()

}