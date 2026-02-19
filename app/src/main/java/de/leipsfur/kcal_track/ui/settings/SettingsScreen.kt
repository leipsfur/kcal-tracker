package de.leipsfur.kcal_track.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import de.leipsfur.kcal_track.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val successMessage = stringResource(R.string.settings_bmr_saved)
    val permissionDeniedMessage = stringResource(R.string.settings_scan_permission_denied)

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            val bitmap = context.contentResolver.openInputStream(photoUri!!)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
            if (bitmap != null) {
                viewModel.onImageCaptured(bitmap)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createScanPhotoUri(context)
            photoUri = uri
            cameraLauncher.launch(uri)
        } else {
            viewModel.dismissScanResult()
        }
    }

    val launchCamera = {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasCameraPermission) {
            val uri = createScanPhotoUri(context)
            photoUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onBackupShared()
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.onRestoreFileSelected(uri)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.dismissSaveSuccess()
        }
    }

    LaunchedEffect(uiState.backupShareIntent) {
        val intent = uiState.backupShareIntent
        if (intent != null) {
            shareLauncher.launch(Intent.createChooser(intent, null))
        }
    }

    if (uiState.showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRestoreConfirmDialog() },
            title = { Text(stringResource(R.string.settings_restore_confirm_title)) },
            text = { Text(stringResource(R.string.settings_restore_confirm_text)) },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmRestore() }) {
                    Text(stringResource(R.string.settings_restore_confirm_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRestoreConfirmDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Scan result dialog
    val scanState = uiState.scanState
    if (scanState is ScanState.Result) {
        ScanResultDialog(
            result = scanState.result,
            onDismiss = { viewModel.dismissScanResult() }
        )
    }
    if (scanState is ScanState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissScanResult() },
            title = { Text(stringResource(R.string.settings_scan_label)) },
            text = {
                Text(
                    stringResource(R.string.settings_scan_error_generic, scanState.message)
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissScanResult() }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_settings),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.settings_bmr_label),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.bmrInput,
                    onValueChange = { viewModel.onBmrInputChanged(it) },
                    label = { Text(stringResource(R.string.settings_bmr_hint)) },
                    suffix = { Text(stringResource(R.string.settings_bmr_unit)) },
                    isError = uiState.validationError != null,
                    supportingText = {
                        when {
                            uiState.validationError != null -> {
                                Text(
                                    text = uiState.validationError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            uiState.savedBmr != null -> {
                                Text(
                                    text = stringResource(
                                        R.string.settings_bmr_current,
                                        uiState.savedBmr!!
                                    )
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.saveBmr()
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.saveBmr()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.settings_bmr_save))
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.settings_backup_label),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.createBackup() },
                    enabled = !uiState.isBackupInProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isBackupInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = stringResource(R.string.settings_backup_create))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { restoreLauncher.launch(arrayOf("*/*")) },
                    enabled = !uiState.isRestoreInProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isRestoreInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = stringResource(R.string.settings_backup_restore))
                    }
                }

                if (uiState.backupError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.backupError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(24.dp))

                // Nährwert-Scan section
                Text(
                    text = stringResource(R.string.settings_scan_label),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { launchCamera() },
                    enabled = scanState !is ScanState.Scanning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (scanState is ScanState.Scanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = stringResource(R.string.settings_scan_loading))
                    } else {
                        Text(text = stringResource(R.string.settings_scan_button))
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun ScanResultDialog(
    result: NutritionScanResult,
    onDismiss: () -> Unit
) {
    val notRecognized = stringResource(R.string.settings_scan_not_recognized)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_scan_result_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NutritionRow(
                    label = stringResource(R.string.settings_scan_result_kcal),
                    value = result.kcal?.let {
                        stringResource(R.string.settings_scan_value_kcal, it.toString())
                    } ?: notRecognized
                )
                NutritionRow(
                    label = stringResource(R.string.settings_scan_result_protein),
                    value = result.proteinGrams?.let {
                        stringResource(R.string.settings_scan_value_g, formatGrams(it))
                    } ?: notRecognized
                )
                NutritionRow(
                    label = stringResource(R.string.settings_scan_result_carbs),
                    value = result.carbsGrams?.let {
                        stringResource(R.string.settings_scan_value_g, formatGrams(it))
                    } ?: notRecognized
                )
                NutritionRow(
                    label = stringResource(R.string.settings_scan_result_fat),
                    value = result.fatGrams?.let {
                        stringResource(R.string.settings_scan_value_g, formatGrams(it))
                    } ?: notRecognized
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatGrams(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        "%.1f".format(value)
    }
}

private fun createScanPhotoUri(context: android.content.Context): Uri {
    val dir = File(context.cacheDir, "scan_photos").apply { mkdirs() }
    val file = File(dir, "scan_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
