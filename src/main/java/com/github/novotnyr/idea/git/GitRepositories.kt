package com.github.novotnyr.idea.git

import git4idea.GitUtil
import git4idea.repo.GitRepository
import java.util.concurrent.CopyOnWriteArrayList


class GitRepositories(private val repositories: MutableList<GitRepository> = CopyOnWriteArrayList()) : MutableList<GitRepository> by repositories {
    fun getBranchesOnHead(): Map<GitRepository, String> {
        val branchMappings = mutableMapOf<GitRepository, String>()
        forEach { repo ->
            branchMappings[repo] = GitUtil.HEAD
        }
        return branchMappings
    }
}