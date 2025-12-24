package com.example.handhophop.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.ui.res.dimensionResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import com.example.handhophop.R
import com.example.handhophop.data.ProfileState


@Composable
fun ProfileTopBanner() {
    val h = dimensionResource(R.dimen.top_banner_height)
    val radius0 = dimensionResource(R.dimen.radius_0)
    val elevation0 = dimensionResource(R.dimen.block_elevation)
    val bannerColor = colorResource(R.color.card_beige)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(h),
        shape = RoundedCornerShape(radius0),
        colors = CardDefaults.cardColors(containerColor = bannerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation0)
    ) {

    }
}

@Composable
private fun ProfileAvatar(
    avatarUri: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val size = dimensionResource(R.dimen.profile_avatar_size)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUri != null) {
            AsyncImage(
                model = avatarUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}



@Composable
fun ProfileScreen(navController: NavHostController) {
    val bg = colorResource(R.color.bg_beige)
    val vm: ProfileViewModel = viewModel()
    val state by vm.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        BackgroundPattern()

        Column(modifier = Modifier.fillMaxSize()) {

            // ── ВЕРХ: баннер + аватар ─────────────────
            Box {
                ProfileTopBanner()

                ProfileAvatar(
                    avatarUri = state.avatarUri,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = dimensionResource(R.dimen.profile_avatar_overlap)),
                    onClick = {
                        navController.navigate(Screen.ProfilePhoto.route)
                    }
                )
            }

            // ⬇️ компенсация наложения
            Spacer(
                modifier = Modifier.height(
                    dimensionResource(R.dimen.profile_banner_bottom_space)
                )
            )

            // ── ЦЕНТР: имя + поля ─────────────────────
            ProfileCenterContent(
                navController = navController,
                state = state,
                onNameChange = { vm.update { s -> s.copy(name = it) } },
                onLoginChange = { vm.update { s -> s.copy(login = it) } },
                onEmailChange = { vm.update { s -> s.copy(email = it) } },
                onPhoneChange = { vm.update { s -> s.copy(phone = it) } } // ✅ FIX
            )

            BottomBar(
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
private fun ColumnScope.ProfileCenterContent(
    navController: NavHostController,
    state: ProfileState,
    onNameChange: (String) -> Unit,
    onLoginChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    val w = dimensionResource(R.dimen.profile_stack_width)
    val gap = dimensionResource(R.dimen.profile_stack_gap)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── ВЕРХ ЦЕНТРА: имя (остается под аватаром) ──
            Text(
                text = state.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_dark)
            )

            // 🔽 свободное место до центра
            Spacer(modifier = Modifier.weight(0.7f))

            // ── ЦЕНТР ЭКРАНА: ПОЛЯ ───────────────────────
            Column(
                modifier = Modifier.width(w),
                verticalArrangement = Arrangement.spacedBy(gap),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileField(
                    label = stringResource(R.string.profile_label_name),
                    value = state.name,
                    onValueChange = onNameChange
                )
                ProfileField(
                    label = stringResource(R.string.profile_label_login),
                    value = state.login,
                    onValueChange = onLoginChange
                )
                ProfileField(
                    label = stringResource(R.string.profile_label_email),
                    value = state.email,
                    onValueChange = onEmailChange
                )
                ProfileField(
                    label = stringResource(R.string.profile_label_phone),
                    value = state.phone,
                    onValueChange = onPhoneChange
                )
            }

            // 🔼 свободное место после центра
            Spacer(modifier = Modifier.weight(1.3f))
        }
    }
}



@Composable
private fun ProfileHeader(
    avatarUri: String?,
    title: String,
    onAvatarClick: () -> Unit
) {
    val avatarSize = dimensionResource(R.dimen.profile_avatar_size)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri != null) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.default_avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val h = dimensionResource(R.dimen.profile_field_height)
    val r = dimensionResource(R.dimen.block_radius)
    val elevation0 = dimensionResource(R.dimen.block_elevation)

    val bg = colorResource(R.color.card_beige_2)
    val textDark = colorResource(R.color.text_dark)
    val labelColor = colorResource(R.color.text_muted)

    val hPad = dimensionResource(R.dimen.profile_field_hpad)
    val labelW = dimensionResource(R.dimen.profile_label_width)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(h),
        shape = RoundedCornerShape(r),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation0)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = hPad),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Лейбл слева фиксированной ширины
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(labelW)
            )

            // Поле ввода справа (без рамки, как на твоих плашках)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = textDark),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}