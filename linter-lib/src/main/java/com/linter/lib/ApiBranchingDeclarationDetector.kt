package com.linter.lib

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMember
import org.jetbrains.uast.*

class ApiBranchingDeclarationDetector : Detector(), SourceCodeScanner {

    companion object {
        // Полный путь к аннотации в вашем проекте
        private const val ANNOTATION_NAME = "com.example.composeapp.utils.ApiVersionDependent"

        private val IMPLEMENTATION = Implementation(
            ApiBranchingDeclarationDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE_DECLARATION = Issue.create(
            id = "MethodUsesApiBranching",
            briefDescription = "Метод использует ветвление по API",
            explanation = "Этот метод содержит проверку Build.VERSION.SDK_INT. Это требует особого внимания при тестировании.",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )

        val ISSUE_CALL_SITE = Issue.create(
            id = "ApiDependentCallSite",
            briefDescription = "Вызов API-зависимого метода",
            explanation = "Вы вызываете метод, поведение которого меняется в зависимости от версии Android. Убедитесь в корректности работы на всех Sdk.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf(USimpleNameReferenceExpression::class.java, UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            
            // 1. Проверка внутри метода (поиск SDK_INT)
            override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
                if (node.identifier == "SDK_INT") {
                    val resolved = node.resolve()
                    if (context.evaluator.isMemberInClass(resolved as? PsiMember, "android.os.Build.VERSION")) {
                        val method = node.getParentOfType(UMethod::class.java)
                        if (method != null) {
                            val hasAnnotation = context.evaluator.getAnnotation(method, ANNOTATION_NAME) != null
                            val message = if (hasAnnotation) {
                                "Метод '${method.name}' помечен как API-зависимый. Требуется тестирование на разных версиях ОС."
                            } else {
                                "Метод '${method.name}' содержит ветвление по SDK_INT. Добавьте @ApiVersionDependent, чтобы предупредить коллег в местах вызова."
                            }
                            context.report(
                                ISSUE_DECLARATION,
                                method,
                                context.getNameLocation(method),
                                message
                            )
                        }
                    }
                }
            }

            // 2. Проверка места ВЫЗОВА метода
            override fun visitCallExpression(node: UCallExpression) {
                val method = node.resolve() ?: return
                if (context.evaluator.getAnnotation(method, ANNOTATION_NAME) != null) {
                    context.report(
                        ISSUE_CALL_SITE,
                        node,
                        context.getCallLocation(node, includeReceiver = true, includeArguments = true),
                        "ВНИМАНИЕ: Вызов API-зависимого метода '${method.name}'. Проверьте работу на устройствах с разными версиями ОС!"
                    )
                }
            }
        }
    }
}
