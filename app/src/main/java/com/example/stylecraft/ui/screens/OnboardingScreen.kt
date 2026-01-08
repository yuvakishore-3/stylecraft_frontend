package com.example.stylecraft.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylecraft.R
import com.example.stylecraft.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavController) {

    val pages = listOf(
        OnboardingPage(
            title = "Scan Your Face",
            description = "Unlock secure and personalized access with a quick, private Face Scan. Your data is protected.",
            imageRes = R.drawable.scan_your_face
        ),
        OnboardingPage(
            title = "AI Face Shape Detection",
            description = "Analyze your unique facial contour to recommend the perfect frame.",
            imageRes = R.drawable.ai_face_shape_detect
        ),
        OnboardingPage(
            title = "AI Hairstyle Suggestions",
            description = "Get personalized haircut recommendations. Our AI analyzes your unique face shape to find styles that complement features.",
            imageRes = R.drawable.ai_face_shape_detect   // replace with 3rd image asset
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Top bar: back icon, fake status bar spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                IconButton(onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else {
                Spacer(Modifier.size(48.dp))
            }
        }

        val page = pages[pagerState.currentPage]

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(32.dp))

        // Image area exactly like client: centered with fixed height
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val current = pages[pageIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main image
                Image(
                    painter = painterResource(id = current.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .width(220.dp)
                        .height(260.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Page‑2: Scan / Recommend icons
                if (pageIndex == 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_scan),
                                contentDescription = "Scan",
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Scan", style = MaterialTheme.typography.bodyMedium)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_recommend),
                                contentDescription = "Recommend",
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Recommend", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Page‑3: small circular hairstyle avatars
                if (pageIndex == 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarImage(R.drawable.img_style_1)
                        Spacer(Modifier.width(16.dp))
                        AvatarImage(R.drawable.img_style_2)
                        Spacer(Modifier.width(16.dp))
                        AvatarImage(R.drawable.img_style_3)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        OnboardingIndicators(
            totalDots = pages.size,
            selectedIndex = pagerState.currentPage
        )

        Spacer(Modifier.height(24.dp))

        val isLastPage = pagerState.currentPage == pages.lastIndex
        Button(
            onClick = {
                if (isLastPage) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5A2DFF)   // purple from client design
            )
        ) {
            Text(
                text = if (isLastPage) "Get Started" else "Next",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AvatarImage(@DrawableRes resId: Int) {
    Surface(
        shape = CircleShape,
        shadowElevation = 2.dp
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
    }
}
