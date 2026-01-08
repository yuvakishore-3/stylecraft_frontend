package com.example.stylecraft.data.api

data class FaceShapeResponse(
    val faceShape: String,  // "OVAL", "SQUARE", etc.
    val confidence: Float
)
