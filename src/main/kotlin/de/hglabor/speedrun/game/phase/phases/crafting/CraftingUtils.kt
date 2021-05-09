package de.hglabor.speedrun.game.phase.phases.crafting

import de.hglabor.speedrun.utils.stack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import java.util.*
import java.util.stream.Collectors

object CraftingUtils {
    private val random = Random()
    private val LOG_ITEMS = arrayOf("_planks", "stick", "_slab", "_planks")
    private val EXCLUDED_ITEMS = arrayOf(
        "jungle", "spruce", "birch", "acacia",
        "dark_oak", "crimson", "warped", "carpet", "pane", "terracotta", "glass",
        "banner", "bed", "wall", "slab", "stairs", "blackstone", "quartz", "sandstone",
        "polished", "chiseled", "pillar"
    )
    private val SPECIAL_ITEMS = arrayOf(Material.WARPED_FUNGUS_ON_A_STICK, Material.RED_BED)
    private val TO_CRAFT_MATERIALS = toCraftMaterials
    private fun getRecipes(itemStack: ItemStack): List<Recipe> {
        return Bukkit.getRecipesFor(itemStack)
    }

    private fun isShapedRecipe(material: Material): Boolean {
        var isShapedRecipe = false
        for (recipe in getRecipes(ItemStack(material))) {
            isShapedRecipe = recipe is ShapedRecipe
        }
        return isShapedRecipe
    }

    private fun isExcludedMaterial(material: Material): Boolean {
        return Arrays.stream(EXCLUDED_ITEMS).anyMatch { excludedItemName: String? ->
            material.name.toLowerCase().contains(
                excludedItemName!!
            )
        }
    }

    private val toCraftMaterials: List<Material>
        get() {
            val collect =
                Arrays.stream(Material.values()).filter { material: Material -> !isExcludedMaterial(material) }
                    .filter { material: Material? -> material?.let { isShapedRecipe(material) } ?: false }.collect(Collectors.toList())
            collect.addAll(listOf(*SPECIAL_ITEMS))
            return collect
        }

    private fun getOriginMaterial(itemStack: ItemStack): List<ItemStack>? {
        for (logItem in LOG_ITEMS) {
            if (itemStack.type.name.toLowerCase().endsWith(logItem)) {
                return listOf(Material.OAK_LOG.stack())
            }
        }
        val itemStacks: MutableList<ItemStack> = ArrayList()
        when (itemStack.type) {
            Material.FISHING_ROD -> {
                itemStacks.add(Material.OAK_LOG.stack())
                itemStacks.add(Material.STRING.stack(2))
                return itemStacks
            }
            Material.RED_BED -> {
                itemStacks.add(Material.OAK_LOG.stack())
                itemStacks.add(ItemStack(Material.RED_WOOL, 3))
                return itemStacks
            }
            Material.ARMOR_STAND -> {
                itemStacks.add(ItemStack(Material.OAK_LOG, 3))
                itemStacks.add(Material.SMOOTH_STONE_SLAB.stack())
                return itemStacks
            }
            Material.CHEST_MINECART -> {
                itemStacks.add(ItemStack(Material.OAK_LOG, 3))
                itemStacks.add(ItemStack(Material.IRON_BARS, 5))
                return itemStacks
            }
            Material.TNT_MINECART -> {
                itemStacks.add(ItemStack(Material.SAND, 4))
                itemStacks.add(ItemStack(Material.GUNPOWDER, 5))
                itemStacks.add(ItemStack(Material.IRON_BARS, 5))
                return itemStacks
            }
            else -> {}
        }
        return null
    }

    val randomItemToCraft: Material
        get() = TO_CRAFT_MATERIALS[random.nextInt(TO_CRAFT_MATERIALS.size)]

    fun getCraftingItems(material: Material?): List<ItemStack> {
        val recipes = getRecipes(
            ItemStack(
                material!!
            )
        )
        val itemStacksForCrafting: MutableList<ItemStack> = ArrayList()
        if (recipes[0] is ShapedRecipe) {
            for (value in (recipes[0] as ShapedRecipe).ingredientMap.values) {
                if (value != null) {
                    val originItems = getOriginMaterial(value)
                    itemStacksForCrafting.addAll(originItems ?: setOf(value))
                }
            }
        }
        return itemStacksForCrafting
    }
}