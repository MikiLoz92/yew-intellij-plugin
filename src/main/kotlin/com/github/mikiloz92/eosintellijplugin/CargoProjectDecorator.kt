package com.github.mikiloz92.eosintellijplugin

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.IconManager
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import javax.swing.Icon

class CargoProjectDecorator : ProjectViewNodeDecorator {

    private val cargoProjectIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/ideaProject.svg", CargoProjectDecorator::class.java)
    }

    private val cratesFolderIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/cratesFolder.svg", CargoProjectDecorator::class.java)
    }

    private val librariesFolderIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/librariesFolder.svg", CargoProjectDecorator::class.java)
    }

    private val libraryFolderIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/libraryFolder.svg", CargoProjectDecorator::class.java)
    }

    private val buildLogicFolderIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/taskGroup.svg", CargoProjectDecorator::class.java)
    }

    private val cargoIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/cargo.svg", CargoProjectDecorator::class.java)
    }

    private val kotlinIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/kotlinToolWindow.svg", CargoProjectDecorator::class.java)
    }

    private val rustModuleIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/rustModule.svg", CargoProjectDecorator::class.java)
    }

    private val kotlinModuleIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/kotlinModule.svg", CargoProjectDecorator::class.java)
    }

    private val astraGatewayIcon: Icon by lazy {
        IconManager.getInstance().getIcon("/icons/astra.svg", CargoProjectDecorator::class.java)
    }

    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        val file: VirtualFile = node.virtualFile ?: return
        if (!file.isDirectory) return

        val project = node.project ?: return
        val parent = file.parent
        val isRootLevel = (parent == null || parent.name == project.name)


        val isRootLevelBuildLogic = file.name == "build-logic" && (parent == null || parent.name == project.name)
        if (isRootLevelBuildLogic) {
            data.setIcon(buildLogicFolderIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
            return
        }

        val cargoToml = file.findChild("Cargo.toml")
        if (cargoToml != null && !cargoToml.isDirectory && project.basePath != file.path) {
            data.setIcon(rustModuleIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
            return
        }

        val buildGradleKts = file.findChild("build.gradle.kts")
        if (buildGradleKts != null && !buildGradleKts.isDirectory && project.basePath != file.path) {
            data.setIcon(kotlinModuleIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
            return
        }

        if (isRootLevel && file.name == "crates") {
            data.setIcon(cratesFolderIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD or SimpleTextAttributes.STYLE_ITALIC, JBColor.DARK_GRAY))
            return
        }

        if (isRootLevel && file.name == "libraries") {
            data.setIcon(librariesFolderIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD or SimpleTextAttributes.STYLE_ITALIC, JBColor.DARK_GRAY))
            return
        }

        if (isRootLevel && file.name == "scripts") {
            data.setIcon(AllIcons.Nodes.LogFolder)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD or SimpleTextAttributes.STYLE_ITALIC, JBColor.DARK_GRAY))
            return
        }

        if (isRootLevel && file.name == "apps") {
            data.setIcon(AllIcons.RunConfigurations.Compound)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD or SimpleTextAttributes.STYLE_ITALIC, JBColor.DARK_GRAY))
            return
        }

        /*val isRootLevelSupport = file.name == "support" && (parent == null || parent.name == project.name)
        if (isRootLevelSupport) {
            data.setIcon(libraryFolderIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
            return
        }*/

        /*// 3. "support" folder at project root → library folder icon
        val isRootLevel = parent == null || parent.name == project.name
        if (isRootLevel && file.name == "nerv") {
            data.setIcon(astraGatewayIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
            return
        }
        if (isRootLevel && file.name == "vehicles") {
            data.setIcon(astraGatewayIcon)
            data.clearText()
            data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
            return
        }*/

        // 4. Inside "support" → apply custom icons to certain folders
        if (parent != null && parent.name == "support") {
            when (file.name) {
                "crates" -> {
                    data.setIcon(cargoIcon)
                    data.clearText()
                    data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
                }
                "libraries" -> {
                    data.setIcon(kotlinIcon)
                    data.clearText()
                    data.addText(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, null))
                }
            }
        }
    }

    private fun hasCargoToml(file: VirtualFile): Boolean {
        val cargoToml = file.findChild("Cargo.toml")
        return cargoToml != null && !cargoToml.isDirectory
    }
}