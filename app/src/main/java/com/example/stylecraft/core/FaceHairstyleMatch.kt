package com.example.stylecraft.core

/**
 * Calculates face shape to hairstyle match percentage.
 * Based on facial proportion theory and hairstyle suitability.
 */
object FaceHairstyleMatch {
    
    /**
     * Compatibility matrix: Map of hairstyle keywords to face shape scores
     * Scores are 0-100 representing match percentage
     */
    private val compatibilityMatrix = mapOf(
        // OVAL - Most versatile, suits almost everything
        "OVAL" to mapOf(
            "layered" to 95, "bob" to 92, "waves" to 93, "pixie" to 88,
            "shag" to 90, "classic" to 92, "textured" to 91, "curly" to 89,
            "side" to 90, "messy" to 88, "neon" to 85, "swept" to 91,
            "default" to 88
        ),
        // ROUND - Needs length and volume on top
        "ROUND" to mapOf(
            "layered" to 92, "bob" to 65, "waves" to 78, "pixie" to 72,
            "shag" to 88, "classic" to 75, "textured" to 85, "curly" to 80,
            "side" to 82, "messy" to 78, "neon" to 75, "swept" to 85,
            "default" to 78
        ),
        // SQUARE - Needs softening, avoid blunt cuts
        "SQUARE" to mapOf(
            "layered" to 90, "bob" to 70, "waves" to 88, "pixie" to 68,
            "shag" to 85, "classic" to 72, "textured" to 88, "curly" to 85,
            "side" to 90, "messy" to 82, "neon" to 78, "swept" to 88,
            "default" to 80
        ),
        // HEART - Needs balance at chin, avoid volume at crown
        "HEART" to mapOf(
            "layered" to 85, "bob" to 92, "waves" to 88, "pixie" to 75,
            "shag" to 82, "classic" to 85, "textured" to 80, "curly" to 85,
            "side" to 88, "messy" to 78, "neon" to 76, "swept" to 86,
            "default" to 82
        ),
        // OBLONG - Needs width, avoid extra length
        "OBLONG" to mapOf(
            "layered" to 78, "bob" to 88, "waves" to 90, "pixie" to 82,
            "shag" to 85, "classic" to 80, "textured" to 82, "curly" to 92,
            "side" to 85, "messy" to 88, "neon" to 80, "swept" to 82,
            "default" to 82
        )
    )
    
    /**
     * Calculate match percentage for a hairstyle based on detected face shape.
     * 
     * @param faceShape Detected face shape (OVAL, ROUND, SQUARE, HEART, OBLONG)
     * @param hairstyleName Name of the hairstyle to match
     * @return Match percentage 0-100
     */
    fun calculateMatch(faceShape: String?, hairstyleName: String): Int {
        val shape = faceShape?.uppercase() ?: "OVAL"
        val shapeScores = compatibilityMatrix[shape] ?: compatibilityMatrix["OVAL"]!!
        
        // Find matching keyword in hairstyle name
        val nameLower = hairstyleName.lowercase()
        
        for ((keyword, score) in shapeScores) {
            if (keyword != "default" && nameLower.contains(keyword)) {
                return score
            }
        }
        
        return shapeScores["default"] ?: 80
    }
    
    /**
     * Get description explaining the match.
     */
    fun getMatchDescription(faceShape: String?, hairstyleName: String, matchPercentage: Int): String {
        val shape = faceShape?.uppercase() ?: "OVAL"
        
        return when {
            matchPercentage >= 90 -> "Excellent match! This style complements your $shape face shape beautifully."
            matchPercentage >= 80 -> "Great choice! This hairstyle works well with your $shape face shape."
            matchPercentage >= 70 -> "Good match. This style can work nicely with your $shape face shape."
            matchPercentage >= 60 -> "Moderate match. Consider styles that add more balance to your $shape face."
            else -> "This style may not be ideal for your $shape face shape. Try exploring other options."
        }
    }
}
