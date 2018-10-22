package com.github.novotnyr.idea.git;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.MultiMap;
import git4idea.GitLocalBranch;
import git4idea.GitUtil;
import git4idea.branch.GitBrancher;
import git4idea.branch.GitBranchesCollection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BranchListActionGroup extends ActionGroup {
    public static final AnAction[] NO_ACTIONS = new AnAction[0];

    public BranchListActionGroup() {
        super("Checkout Branch", true);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return NO_ACTIONS;
        }
        GitBrancher gitBrancher = GitBrancher.getInstance(project);

        List<SelectedModule> selectedModules = SelectedModule.manyOf(anActionEvent);
        if (selectedModules.isEmpty()) {
            return NO_ACTIONS;
        }

        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);

        Helper repoBranches = new Helper();
        for (SelectedModule selectedModule : selectedModules) {
            GitRepository repo = repositoryManager.getRepositoryForFile(selectedModule.getFile());
            System.out.println("Found repo " + repo + " for " + selectedModule);
            if (repo == null) {
                continue;
            }
            repoBranches.addLocalBranches(repo);
        }

        List<AnAction> actions = new ArrayList<>();
        actions.add(new CheckoutNewBranchAction());
        actions.add(Separator.getInstance());

        for (String localBranch : repoBranches.getOverlappingBranches()) {
            actions.add(new AnAction(localBranch) {
                @Override
                public void actionPerformed(AnActionEvent anActionEvent) {
                    List<GitRepository> repositoriesWithBranch = repoBranches.getRepositoriesWithBranch(localBranch);
                    gitBrancher.checkout(localBranch, false, repositoriesWithBranch, null);
                }
            });

        }
        return actions.toArray(new AnAction[0]);
    }

    public static class Helper {
        private MultiMap<GitRepository, String> mapping = new MultiMap<>();

        public void add(GitRepository repo, String branchName) {
            this.mapping.putValue(repo, branchName);
        }

        public void addLocalBranches(GitRepository repository) {
            GitBranchesCollection branches = repository.getBranches();
            for (GitLocalBranch localBranch : branches.getLocalBranches()) {
                this.mapping.putValue(repository, localBranch.getName());
            }
        }

        public List<String> getOverlappingBranches() {
            List<String> overlappingBranches = null;
            for (Map.Entry<GitRepository, Collection<String>> entry : mapping.entrySet()) {
                Collection<String> entryBranches = entry.getValue();
                if (overlappingBranches == null) {
                    overlappingBranches = new ArrayList<>(entryBranches);
                } else {
                    overlappingBranches.retainAll(entryBranches);
                }
            }
            return overlappingBranches;
        }

        public List<GitRepository> getRepositoriesWithBranch(String branch) {
            List<GitRepository> repositories = new ArrayList<>();
            for (Map.Entry<GitRepository, Collection<String>> entry : mapping.entrySet()) {
                GitRepository repo = entry.getKey();
                Collection<String> repoBranches = entry.getValue();
                if (repoBranches.contains(branch)) {
                    repositories.add(repo);
                }
            }
            return repositories;
        }
    }
}

