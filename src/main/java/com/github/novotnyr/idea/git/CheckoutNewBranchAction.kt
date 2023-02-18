package com.github.novotnyr.idea.git

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.Project
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository
import git4idea.validators.GitNewBranchNameValidator

class CheckoutNewBranchAction : AnAction("Checkout New Branch...", "Checkout and switch to a new branch", null) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val selectedModules = SelectedModule.manyOf(e)
        if (selectedModules.isEmpty()) {
            return
        }
        switchBranch(project, selectedModules)
    }

    private fun switchBranch(project: Project, selectedModule: Collection<SelectedModule>) {
        val gitBrancher = GitBrancher.getInstance(project)
        val task = RetrieveGitBranchesTask(project, selectedModule) { branchMapping ->
            val repositories = ArrayList(branchMapping.keys)
            val options = getNewBranchNameFromUser(project, repositories) ?: return@RetrieveGitBranchesTask
            if (options.shouldCheckout()) {
                gitBrancher.checkoutNewBranch(options.name, repositories)
            } else {
                gitBrancher.createBranch(options.name, branchMapping)
            }
        }
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
    }

    private fun getNewBranchNameFromUser(project: Project, repositories: List<GitRepository>): GitNewBranchOptions? {
        val dialogTitle = "Create New Branch"
        return getNewBranchNameFromUser(project, repositories, dialogTitle, "")
    }

    /**
     *
     * Shows a message dialog to enter the name of new branch.
     * Optionally allows to not checkout this branch, and just create it.
     * Mirrors `git4idea.branch.GitBranchUtil#getNewBranchNameFromUser(com.intellij.openapi.project.Project, java.util.Collection, java.lang.String, java.lang.String)`
     *
     * @return the name of the new branch and whether it should be checked out, or `null` if user has cancelled the dialog.
     */
    fun getNewBranchNameFromUser(
        project: Project,
        repositories: Collection<GitRepository>,
        dialogTitle: String,
        initialName: String?
    ): GitNewBranchOptions? {
        return GitNewBranchDialog(
            project,
            dialogTitle,
            initialName,
            GitNewBranchNameValidator.newInstance(repositories)
        ).showAndGetOptions()
    }
}