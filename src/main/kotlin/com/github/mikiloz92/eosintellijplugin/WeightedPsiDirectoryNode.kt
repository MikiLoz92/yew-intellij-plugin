package com.github.mikiloz92.eosintellijplugin

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import java.nio.file.Paths

class WeightedPsiDirectoryNode(
    project: Project,
    directory: PsiDirectory,
    viewSettings: ViewSettings?,
    private val virtualFile: VirtualFile
) : PsiDirectoryNode(project, directory, viewSettings) {

    override fun getWeight(): Int {
        val name = virtualFile.name

        // Apply weighting *only* for top-level folders (directly under project root)
        if (!isTopLevelDirectory(project, virtualFile)) {
            return super.getWeight()
        }

        // Assign explicit weights based on name
        return when (name) {
            "Other" -> 0
            ".idea" -> 0
            ".cargo" -> 1
            ".kotlin" -> 1
            "gradle" -> 2
            "kotlin-js-store" -> 2
            "target" -> 3
            "build" -> 3
            ".gradle" -> 4
            "build-logic" -> 5
            "support" -> 6
            "crates" -> 6
            "libraries" -> 6
            "scripts" -> 6
            "apps" -> 6
            else -> {
                // If folder contains Cargo.toml or build.gradle
                val hasCargoToml = virtualFile.findChild("Cargo.toml") != null
                val hasGradleBuild = virtualFile.findChild("build.gradle") != null ||
                        virtualFile.findChild("build.gradle.kts") != null

                when {
                    hasCargoToml -> 7
                    hasGradleBuild -> 8
                    else -> 10 // Default weight for normal folders
                }
            }
        }
    }

    private fun isTopLevelDirectory(project: Project, vf: VirtualFile): Boolean {
        val projectBase = project.basePath ?: return false
        val parent = vf.parent ?: return false

        // Compare absolute paths (normalize for safety)
        val projectPath = Paths.get(projectBase).normalize()
        val parentPath = Paths.get(parent.path).normalize()

        return projectPath == parentPath
    }
}