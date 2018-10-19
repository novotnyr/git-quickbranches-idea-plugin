package com.github.novotnyr.idea.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import git4idea.GitUtil;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBrancher;
import git4idea.branch.GitNewBranchOptions;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import one.util.streamex.StreamEx;

import java.util.Collections;
import java.util.List;

public class CheckoutNewBranchAction extends AnAction {
    public CheckoutNewBranchAction() {
        super("Checkout New Branch...", "Checkout and switch to a new branch", null);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        GitBrancher gitBrancher = GitBrancher.getInstance(project);

        SelectedModule selectedModule = SelectedModule.fromEvent(anActionEvent);
        if (selectedModule == null) {
            return;
        }

        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        GitRepository repo = repositoryManager.getRepositoryForFile(selectedModule.getFile());
        if (repo == null) {
            return;
        }

        List<GitRepository> repositories = Collections.singletonList(repo);
        GitNewBranchOptions options = GitBranchUtil.getNewBranchNameFromUser(project, repositories, "Create New Branch");
        if (options != null) {
            if (options.shouldCheckout()) {
                gitBrancher.checkoutNewBranch(options.getName(), repositories);
            } else {
                gitBrancher.createBranch(options.getName(), StreamEx.of(repositories).toMap((position) -> {
                    return "HEAD";
                }));
            }
        }
    }
}
