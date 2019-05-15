package net.azarquiel.fukkuapp.Class

import java.io.Serializable

data class Categoria(
    val id : String,
    val nombre : String,
    val icono : String
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
)

data class Producto(
    val id : String,
    val nombre : String,
    val descripcion : String,
    val precio : String,
    val fecha : String,
    val latitud : String,
    val longitud : String,
    val categoriaId : String,
    val usuarioId : String
)

data class Imagen(
    val id : String,
    val imagen : String,
    val productoId : String
)