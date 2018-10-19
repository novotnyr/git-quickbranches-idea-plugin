package com.github.novotnyr.idea.git;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import git4idea.GitLocalBranch;
import git4idea.GitUtil;
import git4idea.branch.GitBrancher;
import git4idea.branch.GitBranchesCollection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BranchListActionGroup extends ActionGroup {
    public static final AnAction[] NO_ACTIONS = new AnAction[0];

    public BranchListActionGroup() {
        super("Checkout Branch", true);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        GitBrancher gitBrancher = GitBrancher.getInstance(project);

        SelectedModule selectedModule = SelectedModule.fromEvent(anActionEvent);
        if (selectedModule == null) {
            return NO_ACTIONS;
        }

        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        GitRepository repo = repositoryManager.getRepositoryForFile(selectedModule.getFile());
        if (repo == null) {
            return NO_ACTIONS;
        }

        GitBranchesCollection branches = repo.getBranches();
        List<AnAction> actions = new ArrayList<>();
        actions.add(new CheckoutNewBranchAction());
        actions.add(Separator.getInstance());
        for (GitLocalBranch localBranch : branches.getLocalBranches()) {
            actions.add(new AnAction(localBranch.getName()) {
                @Override
                public void actionPerformed(AnActionEvent anActionEvent) {
                    gitBrancher.checkout(localBranch.getName(), false, Collections.singletonList(repo), null);
                }
            });
        }

        return actions.toArray(new AnAction[0]);
    }
}
