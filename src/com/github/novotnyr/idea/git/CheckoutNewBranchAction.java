package com.github.novotnyr.idea.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import git4idea.GitUtil;
import git4idea.branch.GitBrancher;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;

import java.util.Collections;

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

        String branchName = Messages.showInputDialog(project, "Create a new branch", "New Branch", null);
        if (branchName == null) {
            return;
        }
        gitBrancher.checkoutNewBranch(branchName, Collections.singletonList(repo));
    }
}
