/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.doru.reptomin.playground.ui.dataitemtype

import com.doru.reptomin.playground.util.ExifUtil
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.repeatOnLifecycle
import com.doru.reptomin.playground.ui.theme.MyApplicationTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import com.doru.reptomin.playground.ui.MultiPreviews

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DataItemTypeScreen(
    modifier: Modifier = Modifier,
    viewModel: DataItemTypeViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val items by produceState<DataItemTypeUiState>(
        initialValue = DataItemTypeUiState.Loading,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = STARTED) {
            viewModel.uiState.collect { value = it }
        }
    }
    if (items is DataItemTypeUiState.Success) {
        DataItemTypeScreen(
            items = (items as DataItemTypeUiState.Success).data,
            onSave = viewModel::addDataItemType,
            modifier = modifier
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DataItemTypeScreen(
    items: List<String>,
    onSave: (name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var infos by remember { mutableStateOf("") }
    var isPermissionGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            if (!isGranted.containsValue(false)) {
                // パーミッションが許可された場合の処理
                isPermissionGranted = true
            } else {
                // パーミッションが拒否された場合の処理
                Toast.makeText(context, "パーミッションくれ！！！！！！", Toast.LENGTH_SHORT).show()
            }
        }

    val pickMedia =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val exifInterface =
                        ExifUtil.getExifData(
                            context,
                            photoUri = uri
                        )
                    exifInterface?.let { exif ->
                        exif.latLong?.forEach { latLong -> infos += " /  $latLong" }
                    }
                }
            } else {
                // no-op
            }

        }

    Column(modifier) {
        Button(onClick = {
            if (!isPermissionGranted) {
                PermissionUtils.requestAccessMediaLocationPermission(context, permissionLauncher)
            }
            if (PermissionUtils.isAccessMediaLocationPermissionGranted(context)) {
                // パーミッションが既に付与されている場合の処理
                pickMedia.launch(arrayOf("image/*"))
            }
        }) {
            Text(text = "写真を選んでね")
        }
        Text(text = infos)

        var nameDataItemType by remember { mutableStateOf("Compose") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = nameDataItemType,
                onValueChange = { nameDataItemType = it }
            )

            Button(
                modifier = Modifier.width(96.dp),
                onClick = { onSave(nameDataItemType) }) {
                Text("Save")
            }
        }
        items.forEach {
            Text("Saved item: $it")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@MultiPreviews
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        DataItemTypeScreen(listOf("Compose", "Room", "Kotlin"), onSave = {})
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@MultiPreviews
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        DataItemTypeScreen(listOf("Compose", "Room", "Kotlin"), onSave = {})
    }
}
