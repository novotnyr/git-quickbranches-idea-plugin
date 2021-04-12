package com.github.novotnyr.idea.git

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator
import git4idea.branch.GitBranchUtil
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository


class BranchListActionGroup : ActionGroup("Checkout Branch", true) {
    val NO_ACTIONS = emptyArray<AnAction>()

    override fun getChildren(event: AnActionEvent?): Array<AnAction> {
        event ?: return NO_ACTIONS

        val project = event.project ?: return NO_ACTIONS

        val selectedModules = SelectedModule.manyOf(event)
        if (selectedModules.isEmpty()) return NO_ACTIONS

        val gitBrancher = GitBrancher.getInstance(project)

        val actions = mutableListOf<AnAction>()
        actions.add(CheckoutNewBranchAction())
        actions.add(Separator.getInstance());

        val repositories: List<GitRepository> = selectedModules.mapNotNull(this::getRepository)
        repositories
            .getOverlappingBranches()
            .forEach { localBranchName ->
                actions.add(object : AnAction(localBranchName) {
                    override fun actionPerformed(e: AnActionEvent?) {
                        val repositoriesWithBranch = repositories.getRepositoriesWithBranch(localBranchName)
                        gitBrancher.checkout(localBranchName, false, repositoriesWithBranch, null)
                    }
                })
            }
        return actions.toTypedArray()
    }

    private fun getRepository(selectedModule: SelectedModule): GitRepository? {
        return if (selectedModule.file == null) {
            GitBranchUtil.getCurrentRepository(selectedModule.project)
        } else {
            GitBranchUtil.getRepositoryOrGuess(selectedModule.project, selectedModule.file)
        }
    }

}

