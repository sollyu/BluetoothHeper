package com.sollyu.android.bluetooth.helper.fragment

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.github.ahmadaghazadeh.editor.processor.language.Language
import com.google.android.material.snackbar.Snackbar
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.sollyu.android.bluetooth.helper.R
import com.sollyu.android.bluetooth.helper.app.Application
import com.sollyu.android.bluetooth.helper.bean.Constant
import com.sollyu.android.bluetooth.helper.bean.YamlSettingBean
import kotlinx.android.synthetic.main.fragment_settings.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.regex.Pattern

class SettingsFragment : BaseFragment() {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    // 复制配置文件
    private val yamlSettingsFile = File(Application.Instance.getExternalFilesDir(null), Constant.YAML_SETTINGS_FILE_NAME)

    override fun onCreateView(): View =
        LayoutInflater.from(requireContext()).inflate(R.layout.fragment_settings, baseFragmentActivity.fragmentContainerView, false) as View

    override fun onViewCreated(rootView: View) {
        super.onViewCreated(rootView)
        qmuiTopBarLayout.setTitle(R.string.fragment_settings_title)
        qmuiTopBarLayout.addLeftBackImageButton().setOnClickListener { this.popBackStackResult(resultCode = Activity.RESULT_CANCELED, data = null) }

        qmuiTopBarLayout.addRightImageButton(R.drawable.ic_file_save, R.id.menu_more).setOnClickListener(this::onClickListenerSave)

        appCompatEditText.language = YamlLanguage()
        appCompatEditText.isHorizontalScrollBarEnabled = false
        appCompatEditText.setText(yamlSettingsFile.readText(), 1)
        appCompatEditText.refreshEditor()
    }

    private fun onClickListenerSave(view: View) {
        val context: Context = requireContext()
        try {
            Application.Instance.yamlSettingBean = Yaml().loadAs(appCompatEditText.text, YamlSettingBean::class.java)
            yamlSettingsFile.writeText(appCompatEditText.text, Charsets.UTF_8)
            Snackbar.make(view, "保存完成", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            logger.error("LOG:Application:onCreate", e)
            QMUIDialog.MessageDialogBuilder(context)
                .setTitle("错误")
                .setMessage(e.localizedMessage)
                .addAction(android.R.string.ok) { dialog: QMUIDialog, _: Int -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private inner class YamlLanguage : Language() {
        override fun getSyntaxNumbers(): Pattern = Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)")

        override fun getSyntaxSymbols(): Pattern = Pattern.compile("(!|\\+|-|\\*|<|>|=|\\?|\\||:|%|&)")

        override fun getSyntaxBrackets(): Pattern = Pattern.compile("(\\(|\\)|\\{|\\}|\\[|\\])")

        override fun getSyntaxKeywords(): Pattern = Pattern.compile(
            "(?<=\\b)((enterKeySend)|(onSendAppend)|(onSendClean)|(stringCharset)|(hideNoNameDevice)|(shortcut)|(serverUuid)|(name)" +
            "|(hex)|(text)|(serverName)|(return)|(case)|(catch)|(of)|(typeof)" +
            "|(const)|(default)|(do)|(switch)|(try)|(null)|(true)" +
            "|(false)|(eval)|(let))(?=\\b)"
        )

        override fun getSyntaxMethods(): Pattern = Pattern.compile("(?<=(function) )(\\w+)", Pattern.CASE_INSENSITIVE)

        override fun getSyntaxStrings(): Pattern = Pattern.compile("\"(.*?)\"|'(.*?)'")

        override fun getSyntaxComments(): Pattern = Pattern.compile("/#.*")

        override fun getLanguageBrackets(): CharArray = charArrayOf()

        override fun getAllCompletions(): Array<String> = arrayOf("enterKeySend", "onSendAppend", "onSendClean", "stringCharset", "hideNoNameDevice", "serverName", "serverUuid", "shortcut", "name", "hex", "text", "UTF-8", "GB2312", "GBK", "true", "false")

    }


}