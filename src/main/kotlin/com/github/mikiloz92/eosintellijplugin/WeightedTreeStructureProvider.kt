package com.github.mikiloz92.eosintellijplugin

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ProjectViewModuleNode
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.psi.PsiManager

class OtherGroupNode(
    project: Project,
    private val grouped: List<AbstractTreeNode<*>>
) : AbstractTreeNode<String>(project, "Other") {

    override fun getName(): String = value
    override fun getChildren(): Collection<AbstractTreeNode<*>> = grouped

    override fun update(presentation: PresentationData) {
        presentation.presentableText = "Other"
        presentation.setIcon(AllIcons.General.Groups)
    }
}

class WeightedTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children
        if (ProjectView.getInstance(project).currentViewId != "ProjectPane") return children
        //thisLogger().warn("WeightedTreeStructureProvider runs for parent ${parent.name}")

        if (parent is PsiDirectoryNode) {
            if (parent.virtualFile?.path == project.basePath) {
                val toGroup = children.filter {
                    val vf = (it as? PsiDirectoryNode)?.virtualFile
                    //thisLogger().warn("Virtual file: ${vf?.path}")
                    vf != null && vf.name in listOf(".idea", ".kotlin", "gradle", "build", "target", "test", ".github", ".cargo", ".gradle", ".readme")
                }

                if (toGroup.isNotEmpty()) {
                    // Remove them from root children
                    children.removeAll(toGroup.toSet())

                    // Create the "imaginary" folder node
                    val otherNode = OtherGroupNode(project, toGroup.toList())

                    children.add(otherNode)
                }


                val modifiedChildren = children.map { node ->
                    val dir = when (node) {
                        is ProjectViewModuleNode -> PsiManager.getInstance(parent.project!!).findDirectory(node.value.guessModuleDir()!!)!!
                        is PsiDirectoryNode -> node.value
                        else -> { return@map node }
                    }
                    val file = when (node) {
                        is ProjectViewModuleNode -> node.virtualFile
                        is PsiDirectoryNode -> node.virtualFile
                        else -> { return@map node }
                    }

                    if (file != null) {
                        return@map WeightedPsiDirectoryNode(project, dir, settings, file)
                    }

                    node
                }

                return modifiedChildren.toMutableList()
            }
        }

        return children
    }
}