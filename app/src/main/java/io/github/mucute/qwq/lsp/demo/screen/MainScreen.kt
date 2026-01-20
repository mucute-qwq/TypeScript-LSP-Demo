package io.github.mucute.qwq.lsp.demo.screen

import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import io.github.mucute.qwq.lsp.demo.R
import io.github.mucute.qwq.lsp.demo.component.ExtractDialog
import io.github.mucute.qwq.lsp.demo.component.LoadingContent
import io.github.mucute.qwq.lsp.demo.util.ExtractState
import io.github.mucute.qwq.lsp.demo.util.extractBinariesFlow
import io.github.rosemoe.sora.langs.monarch.MonarchColorScheme
import io.github.rosemoe.sora.langs.monarch.MonarchLanguage
import io.github.rosemoe.sora.langs.monarch.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.flow.onCompletion
import androidx.compose.runtime.remember
import io.github.mucute.qwq.lsp.demo.editor.CodeEditorFixed
import io.github.mucute.qwq.lsp.demo.lsp.ProcessStreamConnectionProvider
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition
import io.github.rosemoe.sora.lsp.editor.LspEditor
import io.github.rosemoe.sora.lsp.editor.LspProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams
import org.eclipse.lsp4j.WorkspaceFolder
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences =
        retain { context.getSharedPreferences("typescript_lsp_demo", Context.MODE_PRIVATE) }
    var isBootsrapInstalled = retain {
        sharedPreferences.getBoolean(
            "bootstrap_installed",
            false
        )
    }

    var extractState: ExtractState by retain {
        mutableStateOf(
            if (isBootsrapInstalled) ExtractState.Idle else ExtractState.Processing(
                "Setup...",
                0f
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        }
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LoadingContent(
                isLoading = extractState is ExtractState.Processing
            ) {
                MainScreenContent()
            }
        }
    }

    if (!isBootsrapInstalled) {
        LaunchedEffect(Unit) {
            extractBinariesFlow(
                context,
                context.assets.open("bootstrap/nodejs.tgz")
            ).onCompletion {
                if (it == null) {
                    sharedPreferences.edit {
                        putBoolean("bootstrap_installed", true)
                    }
                    isBootsrapInstalled = true
                }
            }.collect {
                extractState = it
            }
        }
    }

    ExtractDialog(
        extractState = extractState
    )
}

@Composable
private fun MainScreenContent() {
    CodeEditorNode()
}

@Composable
private fun CodeEditorNode() {
    val context = LocalContext.current
    val codeEditor = remember {
        CodeEditor(context).apply {
            val typeface =
                Typeface.createFromAsset(context.assets, "font/JetBrainsMono-Regular.ttf")
            typefaceLineNumber = typeface
            typefaceText = typeface
            colorScheme = MonarchColorScheme.create(ThemeRegistry.currentTheme)
        }
    }
    var isLspConnected by remember { mutableStateOf(false) }

    AndroidView(
        factory = {
            codeEditor
        },
        modifier = Modifier
            .fillMaxSize()
    )

    if (!isLspConnected) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    context.toast("(Kotlin Activity) Starting Language Server...")
                    codeEditor.isEditable = false
                }

                val serverDefinition =
                    object : CustomLanguageServerDefinition(
                        "ts",
                        ServerConnectProvider {
                            ProcessStreamConnectionProvider()
                        }
                    ) {}

                val typescriptLspDemoFolder = context.filesDir.resolve("typescript-lsp-demo")
                val mainFile = typescriptLspDemoFolder.resolve("index.ts")

                val lspProject = LspProject(typescriptLspDemoFolder.absolutePath)
                lspProject.addServerDefinition(serverDefinition)

                var lspEditor: LspEditor

                withContext(Dispatchers.Main) {
                    lspEditor = lspProject.createEditor(mainFile.absolutePath)
                    lspEditor.wrapperLanguage = MonarchLanguage.create("source.ts", true)
                    lspEditor.editor = codeEditor
                    lspEditor.isEnableInlayHint = true
                    lspEditor.isEnableHover = true
                    lspEditor.isEnableSignatureHelp = true
                }

                try {
                    lspEditor.connectWithTimeout()
                    lspEditor.requestManager.didChangeWorkspaceFolders(
                        DidChangeWorkspaceFoldersParams().apply {
                            this.event = WorkspaceFoldersChangeEvent().apply {
                                added =
                                    listOf(
                                        WorkspaceFolder(
                                            "file://${typescriptLspDemoFolder}",
                                            "typescript-lsp-demo"
                                        )
                                    )
                            }
                        }
                    )

                    isLspConnected = true

                } catch (e: Exception) {
                    isLspConnected = false
                    e.printStackTrace()
                }

                withContext(Dispatchers.Main) {
                    if (isLspConnected) {
                        context.toast("Initialized Language server")
                    } else {
                        context.toast("Unable to connect language server")
                    }

                    codeEditor.editable = true
                }

            }
        }
    }

}

private fun Context.toast(text: String) {
    Toast.makeText(
        this,
        text,
        Toast.LENGTH_SHORT
    ).show()
}