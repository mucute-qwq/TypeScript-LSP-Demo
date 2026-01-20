package io.github.mucute.qwq.lsp.demo.editor

import android.content.Context
import io.github.rosemoe.sora.lang.styling.HighlightTextContainer
import io.github.rosemoe.sora.widget.CodeEditor

class CodeEditorFixed(context: Context) : CodeEditor(context) {

    override fun setHighlightTexts(highlightTexts: HighlightTextContainer?) {
        post {
            super.setHighlightTexts(highlightTexts)
        }
    }

}