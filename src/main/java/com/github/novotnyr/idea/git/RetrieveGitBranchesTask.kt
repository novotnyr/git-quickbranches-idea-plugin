package com.github.novotnyr.idea.git

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresEdt
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

const val CAN_BE_CANCELLED = true

class RetrieveGitBranchesTask(
    project: Project,
    private val selectedModule: Collection<SelectedModule>,
    private val onSuccess: (GitRepositories) -> Unit
) : Task.Backgroundable(project, "Retrieving Git branches", CAN_BE_CANCELLED) {

    private val repositoryManager: GitRepositoryManager

    private val repositories = GitRepositories()

    init {
        repositoryManager = GitRepositoryManager.getInstance(project)
    }

    @RequiresBackgroundThread
    override fun run(indicator: ProgressIndicator) {
        selectedModule.mapNotNullTo(repositories) {
            repositoryManager.getRepositoryForFile(it.file)
        }
    }

    @RequiresEdt
    override fun onSuccess() {
        onSuccess.invoke(repositories)
    }
}