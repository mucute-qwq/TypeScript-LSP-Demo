package io.github.mucute.qwq.lsp.demo.application

import android.app.Application
import io.github.dingyi222666.monarch.languages.TypescriptLanguage
import io.github.rosemoe.sora.langs.monarch.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.monarch.registry.MonarchGrammarRegistry
import io.github.rosemoe.sora.langs.monarch.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.monarch.registry.dsl.monarchLanguages
import io.github.rosemoe.sora.langs.monarch.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.monarch.registry.model.ThemeSource
import io.github.rosemoe.sora.langs.monarch.registry.provider.AssetsFileResolver
import java.io.File

class AppContext : Application() {

    companion object {

        lateinit var instance: AppContext
            private set

    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        FileProviderRegistry.addProvider(AssetsFileResolver(assets))
        ThemeRegistry.loadTheme(ThemeModel(ThemeSource("theme/quietlight.json", "quietlight")))
        MonarchGrammarRegistry.INSTANCE.loadGrammars(monarchLanguages {
            language("typescript") {
                monarchLanguage = TypescriptLanguage
                scopeName = "source.ts"
                languageConfiguration = "textmate/typescript/language-configuration.json"
            }
        })

        filesDir.resolve("usr").mkdirs()
        filesDir.resolve("typescript-lsp-demo").mkdirs()

        val children = assets.list("template") ?: emptyArray()
        for (child in children) {
            filesDir.resolve("typescript-lsp-demo").resolve(child)
                .writeText(assets.open("template/$child").readBytes().decodeToString())
        }
    }

}