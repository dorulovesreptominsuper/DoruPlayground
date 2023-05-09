package com.doru.reptomin.playground.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

// 参考元: https://qiita.com/rnk0085/items/636839ee9d5b72322620
@Preview(
    name = "Light Mode",
    group = "ui modes",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Preview(
    name = "Night Mode",
    group = "ui modes",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Full Screen Preview",
    showSystemUi = true
)
annotation class MultiPreviews