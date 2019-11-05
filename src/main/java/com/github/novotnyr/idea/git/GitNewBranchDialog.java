package com.github.novotnyr.idea.git;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages.InputDialog;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import static com.intellij.util.ui.UIUtil.DEFAULT_HGAP;
import static com.intellij.util.ui.UIUtil.DEFAULT_VGAP;

/**
 * Mirrors an internal class {@code git4idea.branch.GitNewBranchDialog}.
 */
public class GitNewBranchDialog extends InputDialog {
    private JBCheckBox checkoutCheckbox;

    public GitNewBranchDialog(@Nullable Project project,
                              @Nls(capitalization = Nls.Capitalization.Title) String title,
                              String message,
                              @Nullable InputValidator validator)
    {
        super(project, message, title, null, null, validator);
    }

    public GitNewBranchOptions showAndGetOptions() {
        if (!showAndGet()) {
            return null;
        }
        String inputString = getInputString();
        if (inputString != null) {
            inputString = inputString.trim();
        }
        return new GitNewBranchOptions(inputString, checkoutCheckbox.isSelected());
    }

    @Override
    protected JComponent createCenterPanel() {
        this.checkoutCheckbox = new JBCheckBox("Checkout branch", true);
        this.checkoutCheckbox.setMnemonic(KeyEvent.VK_C);

        BorderLayoutPanel panel = JBUI.Panels.simplePanel(DEFAULT_HGAP, DEFAULT_VGAP);
        panel.add(checkoutCheckbox, BorderLayout.WEST);
        return panel;
    }
}
