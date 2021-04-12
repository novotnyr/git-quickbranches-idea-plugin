package com.github.novotnyr.idea.git

import git4idea.branch.GitBranchUtil
import git4idea.repo.GitRepository

fun Collection<GitRepository>.getRepositoriesWithBranch(branch: String) = filter { repo ->
    repo.getLocalBranchNames().contains(branch)
}

fun GitRepository.getLocalBranchNames(): List<String> =
    branches.localBranches.map { it.name }


fun Collection<GitRepository>.getOverlappingBranches(): Collection<String> =
    GitBranchUtil.getCommonBranches(this, true)