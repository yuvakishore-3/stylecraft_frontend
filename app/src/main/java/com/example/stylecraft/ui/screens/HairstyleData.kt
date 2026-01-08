package com.example.stylecraft.ui.screens

import com.example.stylecraft.R

data class HairstyleUi(
    val id: String,
    val name: String,
    val bestFor: String,
    val mood: String,
    val imageRes: Int,
    val isPro: Boolean,
    val imageUrl: String = "" // Keep for compatibility if needed elsewhere
)

fun sampleHairstyles(faceShape: String = "OVAL"): List<HairstyleUi> {
    val allStyles = listOf(
        HairstyleUi("1", "Layered Bob", "Oval, Trendy", "Modern layered cut that adds volume and movement.", R.drawable.layered_bob_h1, false),
        HairstyleUi("2", "Textured Waves", "Oval, Classic", "Soft, natural-looking waves for a relaxed yet elegant look.", R.drawable.textured_waves_h2, false),
        HairstyleUi("3", "Curly Bob", "Oval, Playful", "Bouncy curls that bring life and texture to a classic bob.", R.drawable.curly_bob_h3, true),
        HairstyleUi("4", "Classic Bob", "Oval, Modern", "A timeless, straight-cut bob that works for any professional setting.", R.drawable.classic_bob_h4, false),
        HairstyleUi("5", "Side Swept", "Oval, Playful", "Elegant side-swept look that highlights facial features.", R.drawable.side_swept_h5, false),
        HairstyleUi("6", "Pixie Cut", "Oval, Bold", "A short, chic pixie cut for a bold and confident statement.", R.drawable.pixie_cut_h6, true),
        HairstyleUi("7", "Messy Bun", "Oval, Casual", "Perfect casual look for daily wear or quick styling.", R.drawable.messy_bun_h7, true),
        HairstyleUi("8", "Neon Style", "Oval, Edgy", "Vibrant colors for those who want to stand out from the crowd.", R.drawable.neon_style_h8, false)
    )

    // Filter logic: In a real app, this would use FaceHairstyleMatch.
    // For now, we'll ensure we return 3 FREE and 2 PRO items.
    val freeStyles = allStyles.filter { !it.isPro }.shuffled().take(3)
    val proStyles = allStyles.filter { it.isPro }.shuffled().take(2)
    
    return (freeStyles + proStyles).shuffled()
}

