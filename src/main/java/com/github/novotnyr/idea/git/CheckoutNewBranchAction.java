package com.github.novotnyr.idea.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import git4idea.GitUtil;
import git4idea.branch.GitBrancher;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import git4idea.validators.GitNewBranchNameValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return getNewBranchNameFromUser(project, repositories, dialogTitle, "");
    }

    /**
     * <p>Shows a message dialog to enter the name of new branch.</p>
     * <p>Optionally allows to not checkout this branch, and just create it.</p>
     *
     * <p>Mirrors {@code git4idea.branch.GitBranchUtil#getNewBranchNameFromUser(com.intellij.openapi.project.Project, java.util.Collection, java.lang.String, java.lang.String)}</p>
     *
     * @return the name of the new branch and whether it should be checked out, or {@code null} if user has cancelled the dialog.
     */
    @Nullable
    public static GitNewBranchOptions getNewBranchNameFromUser(@NotNull Project project,
                                                               @NotNull Collection<GitRepository> repositories,
                                                               @NotNull String dialogTitle,
                                                               @Nullable String initialName) {
        return new GitNewBranchDialog(project, dialogTitle, initialName, GitNewBranchNameValidator.newInstance(repositories)).showAndGetOptions();
    }
}
