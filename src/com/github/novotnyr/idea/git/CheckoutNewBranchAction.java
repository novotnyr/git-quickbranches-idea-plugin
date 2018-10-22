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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CheckoutNewBranchAction extends AnAction {
    public CheckoutNewBranchAction() {
        super("Checkout New Branch...", "Checkout and switch to a new branch", null);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        List<SelectedModule> selectedModules = SelectedModule.manyOf(anActionEvent);
        if (selectedModules.isEmpty()) {
            return;
        }

        for (SelectedModule selectedModule : selectedModules) {
            switchBranch(project, selectedModule);
        }
    }

    private void switchBranch(Project project, SelectedModule selectedModule) {
        GitBrancher gitBrancher = GitBrancher.getInstance(project);
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);

        GitRepository repo = repositoryManager.getRepositoryForFile(selectedModule.getFile());
        if (repo == null) {
            return;
        }

        List<GitRepository> repositories = Collections.singletonList(repo);
        Map<GitRepository, String> repos = Collections.singletonMap(repo, GitUtil.HEAD);
        GitNewBranchOptions options = getNewBranchNameFromUser(project, repositories);
        if (options != null) {
            if (options.shouldCheckout()) {
                gitBrancher.checkoutNewBranch(options.getName(), repositories);
            } else {
                gitBrancher.createBranch(options.getName(), repos);
            }
        }

    }

    private GitNewBranchOptions getNewBranchNameFromUser(Project project, List<GitRepository> repositories) {
        String dialogTitle = "Create New Branch";
        try {
            return GitBranchUtil.getNewBranchNameFromUser(project, repositories, dialogTitle);
        } catch (NoSuchMethodError e) {
            try {
                Method getNewBranchNameFromUser = GitBranchUtil.class.getMethod("getNewBranchNameFromUser", Project.class, Collection.class, String.class, String.class);
                return (GitNewBranchOptions) getNewBranchNameFromUser.invoke(null, project, repositories, dialogTitle, "");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
