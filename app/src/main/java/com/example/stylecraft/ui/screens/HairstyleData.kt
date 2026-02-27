package com.example.stylecraft.ui.screens

import com.example.stylecraft.R

data class HairstyleUi(
    val id: String,
    val name: String,
    val bestFor: String,
    val mood: String,
    val imageRes: Int,
    val isPro: Boolean,
    val imageUrl: String = "",
    val gender: String = "Female" // "Male", "Female", or "Unisex"
)

fun sampleHairstyles(faceShape: String = "OVAL", gender: String = "Female"): List<HairstyleUi> {
    val allStyles = listOf(
        // --- Girl Styles (Using High-Quality Female Images) ---
        HairstyleUi(
            id = "f1", 
            name = "Copper Graduated", 
            bestFor = "Oval, Heart", 
            mood = "A stylish graduated cut with vibrant copper tones.", 
            imageRes = R.drawable.g_h1, 
            isPro = false, 
            gender = "Female"
        ),
        HairstyleUi(
            id = "f2", 
            name = "Easy Waves", 
            bestFor = "Oval, Square", 
            mood = "Effortless waves for a soft, natural feminine look.", 
            imageRes = R.drawable.g_h2, 
            isPro = false, 
            gender = "Female"
        ),
        HairstyleUi(
            id = "f3", 
            name = "The Shag", 
            bestFor = "Oval, Long", 
            mood = "A trendy layered cut that adds volume and character.", 
            imageRes = R.drawable.g_h3, 
            isPro = true, 
            gender = "Female"
        ),
        HairstyleUi(
            id = "f4", 
            name = "Long and Sleek", 
            bestFor = "Oval, Round", 
            mood = "Timeless straight hair that offers a polished appearance.", 
            imageRes = R.drawable.g_h4, 
            isPro = false, 
            gender = "Female"
        ),
        HairstyleUi(
            id = "f5", 
            name = "Blunt Bob", 
            bestFor = "Oval, Square", 
            mood = "A clean, modern bob that makes a bold statement.", 
            imageRes = R.drawable.g_h5, 
            isPro = true, 
            gender = "Female"
        ),

        // --- Boy Styles (Using the existing Male Assets) ---
        HairstyleUi(
            id = "m1", 
            name = "Classic Crew Cut", 
            bestFor = "Square, Oval", 
            mood = "A sharp, low-maintenance cut for a clean look.", 
            imageRes = R.drawable.pixie_cut_h6, 
            isPro = false, 
            gender = "Male"
        ),
        HairstyleUi(
            id = "m2", 
            name = "Textured Quiff", 
            bestFor = "Round, Oval", 
            mood = "High-volume top with short sides, trendy and modern.", 
            imageRes = R.drawable.layered_bob_h1, 
            isPro = false, 
            gender = "Male"
        ),
        HairstyleUi(
            id = "m3", 
            name = "Modern Buzz Cut", 
            bestFor = "Square, Diamond", 
            mood = "Minimalist and clean-shaven masculine aesthetic.", 
            imageRes = R.drawable.classic_bob_h4, 
            isPro = true, 
            gender = "Male"
        ),
        HairstyleUi(
            id = "m4", 
            name = "Gentleman's Side Part", 
            bestFor = "Oval, Square", 
            mood = "Professional and timeless style for any occasion.", 
            imageRes = R.drawable.side_swept_h5, 
            isPro = false, 
            gender = "Male"
        ),
        HairstyleUi(
            id = "m5", 
            name = "Wavy Undercut", 
            bestFor = "Oval, Long", 
            mood = "Combines natural texture with a sharp fade.", 
            imageRes = R.drawable.textured_waves_h2, 
            isPro = true, 
            gender = "Male"
        ),
        HairstyleUi(
            id = "m6", 
            name = "Rugged Messy Bun", 
            bestFor = "Heart, Oval", 
            mood = "Casual long hair look for a relaxed man.", 
            imageRes = R.drawable.messy_bun_h7, 
            isPro = false, 
            gender = "Male"
        )
    )

    // Filter by gender first (case insensitive match)
    val genderStyles = allStyles.filter { it.gender.equals(gender, ignoreCase = true) }

    if (genderStyles.isEmpty()) return emptyList()

    // Ensure we return a good mix of free and pro styles
    val freeStyles = genderStyles.filter { !it.isPro }.shuffled()
    val proStyles = genderStyles.filter { it.isPro }.shuffled()
    
    // Take up to 3 free and 2 pro for recommendations, or just return all for lists
    return (freeStyles.take(3) + proStyles.take(2)).shuffled()
}
