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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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

        switchBranch(project, selectedModules);
    }

    private void switchBranch(Project project, Collection<SelectedModule> selectedModule) {
        GitBrancher gitBrancher = GitBrancher.getInstance(project);
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);

        Map<GitRepository, String> branchMapping = new LinkedHashMap<>();
        for (SelectedModule module : selectedModule) {
            GitRepository repo = repositoryManager.getRepositoryForFile(module.getFile());
            if (repo == null) {
                return;
            }
            branchMapping.put(repo, GitUtil.HEAD);
        }
        List<GitRepository> repositories = new ArrayList<>(branchMapping.keySet());
        GitNewBranchOptions options = getNewBranchNameFromUser(project, repositories);
        if (options != null) {
            if (options.shouldCheckout()) {
                gitBrancher.checkoutNewBranch(options.getName(), repositories);
            } else {
                gitBrancher.createBranch(options.getName(), branchMapping);
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
