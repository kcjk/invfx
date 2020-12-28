/*
 * Copyright (c) 2020 Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.noonmaru.invfx.plugin

import com.github.noonmaru.invfx.InvFX
import com.github.noonmaru.invfx.openWindow
import com.github.noonmaru.invfx.window
import com.github.noonmaru.kommand.kommand
import com.github.noonmaru.tap.util.updateFromGitHubMagically
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class InvFXPlugin : JavaPlugin() {
    override fun onEnable() {
        server.apply {
            pluginManager.registerEvents(InvListener(), this@InvFXPlugin)
        }
        setupCommands()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.openInventory.topInventory.window?.run { player.closeInventory(InventoryCloseEvent.Reason.PLUGIN) } //InvWindow 닫기
        }
    }

    private fun setupCommands() {
        kommand {
            register("invfx") {
                then("version") {
                    executes {
                        it.sender.sendMessage("${description.name} ${description.version}")
                    }
                }
                then("update") {
                    executes {
                        updateFromGitHubMagically("noonmaru", "invfx", "InvFX.jar", it.sender::sendMessage)
                    }
                }
                then("test") {
                    require { this is Player }
                    executes {
                        (it.sender as Player).openWindow(testWindow())
                    }
                }
            }
        }
    }

    private fun testWindow() = InvFX.scene(5, "Example") {
        panel(0, 0, 9, 5) {
            listView(1, 1, 7, 3, false, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map { it.toString() }) {
                transform { item -> ItemStack(Material.BOOK).apply { lore = listOf(item) } }
                onClickItem { _, _, _, item, _ -> Bukkit.broadcastMessage("CLICK_ITEM $item") }
                onUpdateItems { _, _, displayList -> Bukkit.broadcastMessage("UPDATE $displayList") }
            }.let { view ->
                button(0, 2) {
                    onClick { _, _ -> view.page-- }
                }
                button(8, 2) {
                    onClick { _, _ -> view.page++ }
                }
            }
        }
    }
}