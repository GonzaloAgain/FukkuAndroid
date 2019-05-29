package net.azarquiel.fukkuapp.Model

import java.io.Serializable

data class Categoria(
    val id : String = "",
    val nombre : String = "",
    val icono : String = ""
):Serializable

data class Usuario(
    val id : String,
    val nombre : String,
    val apellidos : String,
    val nick : String,
    val avatar : String,
    val email : String,
    val password : String,
    val nacimiento : String,
    val latitud : String,
    val longitud : String
):Serializable

data class Producto(
    val id : String = "",
    val nombre : String = "",
    val nombreUsuario : String = "",
    val descripcion : String = "",
    val precio : String = "",
    val fecha : String = "",
    val latitud : String = "",
    val longitud : String = "",
    val categoriaId : String = "",
    val nombreCategoria : String = "",
    val usuarioId : String = "",
    val imagen : String = ""
):Serializable