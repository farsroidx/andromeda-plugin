@file:Suppress("ActionPresentationInstantiatedInCtor")

package ir.farsroidx.m31.actions

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import ir.farsroidx.m31.extensions.errorNotification
import ir.farsroidx.m31.extensions.infoNotification
import ir.farsroidx.m31.extensions.warningNotification
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class NewScreenAction : AnAction("New Compose Screen") {

    private val pathRegex = "src/.*/(kotlin|java).+".toRegex()

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(event: AnActionEvent) {

        val virtualFile: VirtualFile? = event.getData(CommonDataKeys.VIRTUAL_FILE)

        if (virtualFile != null) {

            event.presentation.isEnabledAndVisible = pathRegex.containsMatchIn(virtualFile.path)

        } else {

            event.presentation.isEnabledAndVisible = false

        }
    }

    override fun actionPerformed(event: AnActionEvent) {

        val virtualFile: VirtualFile? = event.getData(CommonDataKeys.VIRTUAL_FILE)

        if (virtualFile != null && virtualFile.isDirectory) {

            if (virtualFile.children.isEmpty()) {

                buildForm(event, virtualFile)

            } else {

                val responseId = Messages.showOkCancelDialog(
                    "This package folder is not empty. Should the files be created anyway?",
                    "Reconfirmation",
                    "CREATE",
                    "CANCEL",
                    Messages.getQuestionIcon()
                )

                if (responseId == 0) {

                    buildForm(event, virtualFile)

                } else {

                    event.project.warningNotification("The build process was canceled.")

                }
            }

        } else {

            event.project.errorNotification("Please select a folder.")

        }
    }

    private fun buildForm(event: AnActionEvent, virtualFile: VirtualFile) {

        val dialog = FormInputDialog()

        if (dialog.showAndGet()) {

            val screenName = dialog.getInputValue()

            if (event.project != null) {

                buildFiles(screenName, event, event.project!!, virtualFile)

            } else {

                event.project.errorNotification("Can not create templates.")

            }
        }
    }

    private fun buildFiles(screenName: String, event: AnActionEvent, project: Project, virtualFile: VirtualFile) {

        try {

            val fileName0 = "${screenName}Screen.kt"
            val fileName1 = "${screenName}UiAction.kt"
            val fileName2 = "${screenName}UiEvent.kt"
            val fileName3 = "${screenName}UiState.kt"
            val fileName4 = "${screenName}ViewModel.kt"

            createFileTemplate(
                templateName = "ComposeScreen.screen",
                screenName = screenName,
                directory = virtualFile,
                fileName = fileName0,
                project = project
            )

            createFileTemplate(
                templateName = "ComposeScreen.ui_state",
                screenName = screenName,
                directory = virtualFile,
                fileName = fileName1,
                project = project
            )

            createFileTemplate(
                templateName = "ComposeScreen.ui_event",
                screenName = screenName,
                directory = virtualFile,
                fileName = fileName2,
                project = project
            )

            createFileTemplate(
                templateName = "ComposeScreen.ui_state",
                screenName = screenName,
                directory = virtualFile,
                fileName = fileName3,
                project = project
            )

            createFileTemplate(
                templateName = "ComposeScreen.viewmodel",
                screenName = screenName,
                directory = virtualFile,
                fileName = fileName4,
                project = project
            )

            event.project.infoNotification(
                buildString {
                    append("$fileName0, ")
                    append("$fileName1, ")
                    append("$fileName2, ")
                    append("$fileName3, ")
                    append("$fileName4 Successfully Created.")
                }
            )

        } catch (e: Exception) {

            e.printStackTrace()

            event.project.errorNotification(
                e.message ?: e.localizedMessage
            )
        }
    }

    private fun createFileTemplate(
        templateName: String,
        screenName: String,
        directory: VirtualFile,
        fileName: String,
        project: Project
    ) {

        val psiDirectory = PsiManager.getInstance(project).findDirectory(directory)
            ?: throw IllegalAccessException("Can not create instance of PsiManager.")

        val template = FileTemplateManager.getInstance(project)
            .getInternalTemplate(templateName)

        val fileContent = template.getText(
            mapOf(
                "PACKAGE_NAME" to getPackageName(directory, project),
                "NAME"         to screenName,
            )
        )

        WriteCommandAction.runWriteCommandAction(project) {

            val psiFile = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName, Language.findLanguageByID("kotlin")!!, fileContent)

            psiDirectory.add(psiFile)

        }
    }

    private fun getPackageName(virtualFile: VirtualFile, project: Project): String? {

        val projectBasePath = project.basePath ?: return null

        val relativePath = virtualFile.path.removePrefix("$projectBasePath/")

        val pathParts = relativePath.split("/")

        val languageIndex = pathParts.indexOfFirst { it in listOf("java", "kotlin", "scala", "groovy") }

        if (languageIndex == -1 || languageIndex >= pathParts.lastIndex) return null

        val packageParts = pathParts.drop(languageIndex + 1)

        return packageParts.joinToString(".")
    }


    private class FormInputDialog : DialogWrapper(true) {

        private val nameRegex: Regex = "^[a-zA-Z][a-zA-Z0-9_]*$".toRegex()

        private val jTextField: JTextField = JTextField(20)

        init {

            title = "Create..."

            isOKActionEnabled = false

            init()

            initValidationListener()

        }

        override fun createCenterPanel(): JComponent {

            val jPanel = JPanel().apply {

                add(
                    JLabel(
                        "Screen name:"
                    )
                )

                add(
                    jTextField
                )
            }

            jTextField.requestFocus()

            return jPanel
        }

        override fun doValidate(): ValidationInfo? {

            val input = getInputValue()

            return if (!input.matches(nameRegex)) {

                ValidationInfo("Only ( A-Z, a-z, 0-9 and _ ) allowed.", jTextField)

            } else null
        }

        private fun initValidationListener() {
            jTextField.document.addDocumentListener(
                object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent?) = validateInput()
                    override fun removeUpdate(e: DocumentEvent?) = validateInput()
                    override fun changedUpdate(e: DocumentEvent?) = validateInput()
                    private fun validateInput() {
                        isOKActionEnabled = doValidate() == null
                    }
                }
            )
        }

        fun getInputValue(): String = jTextField.text.trim()
    }
}