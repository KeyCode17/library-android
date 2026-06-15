package com.library.android.presentation.screens.lending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.library.android.domain.model.Loan
import com.library.android.domain.model.LoanStatus
import com.library.android.presentation.screens.auth.AuthTopBar
import com.library.android.presentation.theme.LibraryTheme
import com.library.android.presentation.theme.StacksColors
import com.library.android.presentation.theme.StacksType

/** Stateful lending screen (the gated "Borrowed" tab). */
@Composable
fun LendingScreen(
    onLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: LendingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LendingContent(
        state = state,
        onScanBorrow = viewModel::scanAndBorrow,
        onReturn = viewModel::returnLoan,
        onApprove = viewModel::approve,
        onLogin = onLogin,
        onBack = onBack,
        modifier = Modifier.systemBarsPadding(),
    )
}

/** Pure, previewable lending UI. */
@Composable
fun LendingContent(
    state: LendingUiState,
    onScanBorrow: () -> Unit = {},
    onReturn: (String) -> Unit = {},
    onApprove: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(StacksColors.Bg)) {
        AuthTopBar(if (state.isStaff) "All loans" else "My loans", onBack)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.isLoading -> LoadingLending()
                state.isAnonymous -> SignedOutLending(onLogin)
                else -> LoansBody(state, onScanBorrow, onReturn, onApprove)
            }
        }
    }
}

@Composable
private fun LoansBody(
    state: LendingUiState,
    onScanBorrow: () -> Unit,
    onReturn: (String) -> Unit,
    onApprove: (String) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Button(
            onClick = onScanBorrow,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StacksColors.Pine,
                contentColor = StacksColors.OnAccent,
            ),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 13.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("Scan to borrow", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }
        if (state.message != null) {
            Text(
                text = state.message,
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        if (state.loans.isEmpty()) {
            EmptyLoans()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.loans, key = { it.id }) { loan ->
                    LoanRow(loan, state.isStaff, onReturn, onApprove)
                }
            }
        }
    }
}

@Composable
private fun EmptyLoans() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "No loans yet — scan a book to borrow it.",
            color = StacksColors.Muted,
            fontFamily = StacksType.Body,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoadingLending() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = StacksColors.Pine,
            modifier = Modifier.testTag(LendingTestTags.LOADING),
        )
    }
}

@Composable
private fun SignedOutLending(onLogin: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = remember { lockIcon() },
                contentDescription = null,
                tint = StacksColors.Faint,
                modifier = Modifier.size(44.dp),
            )
            Text(
                text = "Sign in to view your loans",
                color = StacksColors.Ink,
                fontFamily = StacksType.Display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Borrowing, returns, and due dates live behind your library account.",
                color = StacksColors.Muted,
                fontFamily = StacksType.Body,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onLogin,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StacksColors.Pine,
                    contentColor = StacksColors.OnAccent,
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 13.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Log in", fontFamily = StacksType.Body, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
        }
    }
}

/** Padlock glyph for the sign-in gate, mirroring the design's outlined lock icon. */
private fun lockIcon(): ImageVector =
    ImageVector.Builder(
        name = "LendingLock",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        val stroke = SolidColor(Color.Black)
        path(
            stroke = stroke,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(5f, 11f)
            horizontalLineToRelative(14f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
            verticalLineToRelative(6f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
            horizontalLineTo(5f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
            verticalLineToRelative(-6f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
            close()
            moveTo(7f, 11f)
            verticalLineTo(7f)
            arcToRelative(5f, 5f, 0f, false, true, 10f, 0f)
            verticalLineToRelative(4f)
        }
    }.build()

private val sampleLoans = listOf(
    Loan("l1", "book-1111", "u1", LoanStatus.BORROWED, "2026-06-01T10:00:00Z", "2026-06-15T10:00:00Z"),
    Loan("l2", "book-2222", "u1", LoanStatus.RETURNED, "2026-05-01T10:00:00Z", "2026-05-15T10:00:00Z"),
)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun LendingMemberPreview() {
    LibraryTheme { LendingContent(LendingUiState(loans = sampleLoans, isStaff = false)) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun LendingStaffPreview() {
    LibraryTheme { LendingContent(LendingUiState(loans = sampleLoans, isStaff = true)) }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun LendingSignedOutPreview() {
    LibraryTheme { LendingContent(LendingUiState(isAnonymous = true)) }
}
