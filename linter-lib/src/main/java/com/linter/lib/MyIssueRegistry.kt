package com.linter.lib

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class MyIssueRegistry : IssueRegistry() {
    override val issues: List<Issue> = listOf(
        ApiBranchingDeclarationDetector.ISSUE_DECLARATION,
        ApiBranchingDeclarationDetector.ISSUE_CALL_SITE,
    )

    override val api: Int = CURRENT_API
    override val minApi: Int = 10 // Минимальная версия Lint API
}