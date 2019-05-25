package net.azarquiel.fukkuapp.model

import java.util.*

data class Message (val text: String = "",
               val time: Date = Date(0),
               val senderID: String = "")