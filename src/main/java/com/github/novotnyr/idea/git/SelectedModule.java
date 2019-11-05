package com.github.novotnyr.idea.git;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectedModule {
    private Project project;

    private VirtualFile file;

    public static SelectedModule fromEvent(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return null;
        }
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file == null) {
            return null;
        }
        SelectedModule selectedModule = new SelectedModule();
        selectedModule.file = file;
        selectedModule.project = project;
        return selectedModule;
    }

    public static List<SelectedModule> manyOf(@Nullable AnActionEvent event) {
        if (event == null) {
            return Collections.emptyList();
        }
        List<SelectedModule> selectedModules = new ArrayList<>();

        Project project = event.getProject();
        if (project == null) {
            return selectedModules;
        }
        VirtualFile[] files = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (files == null) {
            return selectedModules;
        }
        for (VirtualFile file : files) {
            SelectedModule selectedModule = new SelectedModule();
            selectedModule.file = file;
            selectedModule.project = project;

            selectedModules.add(selectedModule);
        }
        return selectedModules;
    }

    public Project getProject() {
        return project;
    }

    public VirtualFile getFile() {
        return file;
    }
}
