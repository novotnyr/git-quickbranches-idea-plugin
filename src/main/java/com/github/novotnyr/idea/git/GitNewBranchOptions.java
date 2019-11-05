package com.github.novotnyr.idea.git;

/**
 * Mirrors an internal class {@code git4idea.branch.GitNewBranchOptions}
 */
public class GitNewBranchOptions {
    private String name;

    private boolean checkout;

    public GitNewBranchOptions(String name, boolean checkout) {
        this.name = name;
        this.checkout = checkout;
    }

    public String getName() {
        return name;
    }

    public boolean shouldCheckout() {
        return this.checkout;
    }
}
