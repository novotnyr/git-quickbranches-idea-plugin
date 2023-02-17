package com.github.novotnyr.idea.git

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresEdt
import git4idea.GitUtil
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

const val CAN_BE_CANCELLED = true

class RetrieveGitBranchesTask(
    project: Project,
    private val selectedModule: Collection<SelectedModule>,
    private val onSuccess: (Map<GitRepository, String>) -> Unit
) : Task.Backgroundable(project, "Retrieving Git branches", CAN_BE_CANCELLED) {

    private val repositoryManager: GitRepositoryManager

    private val branchMapping: ConcurrentMap<GitRepository, String>

    init {
        repositoryManager = GitRepositoryManager.getInstance(project)
        branchMapping = ConcurrentHashMap()
    }

    @RequiresBackgroundThread
    override fun run(indicator: ProgressIndicator) {
        for (module in selectedModule) {
            val repo: GitRepository = repositoryManager.getRepositoryForFile(module.file) ?: return
            this.branchMapping[repo] = GitUtil.HEAD
        }
    }

    @RequiresEdt
    override fun onSuccess() {
        onSuccess.invoke(branchMapping)
    }
}