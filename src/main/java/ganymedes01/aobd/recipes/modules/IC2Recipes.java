package ganymedes01.aobd.recipes.modules;

import ganymedes01.aobd.AOBD;
import ganymedes01.aobd.ore.Ore;
import ganymedes01.aobd.recipes.RecipesModule;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class IC2Recipes extends RecipesModule {

	public static final String[] blacklist = { "iron", "gold", "copper", "tin", "silver", "lead" };

	public static void init() {
		ItemStack stoneDust = getICItem("stoneDust");

		label: for (Ore ore : Ore.ores)
			if (ore.shouldIC2()) {
				for (String bEntry : blacklist)
					if (ore.name().equalsIgnoreCase(bEntry))
						continue label;

				String name = ore.name();
				try {
					Recipes.macerator.addRecipe(new RecipeInputOreDict("ore" + name), null, getOreDictItem("crushed" + name, 2));
					Recipes.macerator.addRecipe(new RecipeInputOreDict("ingot" + name), null, getOreDictItem("dust" + name));

					addCentrifugeRecipe(new RecipeInputOreDict("crushed" + name), (int) ore.energy(1500), getOreDictItem("dust" + name), getOreDictItem("dustTiny" + ore.extra()), stoneDust.copy());
					addOreWashingRecipe(new RecipeInputOreDict("crushed" + name), getOreDictItem("crushedPurified" + name), getOreDictItem("dustTiny" + name, 2), stoneDust.copy());

					addCentrifugeRecipe(new RecipeInputOreDict("crushedPurified" + name), (int) ore.energy(1500), getOreDictItem("dust" + name, 1), getOreDictItem("dustTiny" + ore.extra()));

					GameRegistry.addRecipe(new ShapedOreRecipe(getOreDictItem("dust" + name), "xxx", "xxx", "xxx", 'x', "dustTiny" + name));
					GameRegistry.addSmelting(getOreDictItem("crushed" + name), getOreDictItem("ingot" + name), 0.2F);
					GameRegistry.addSmelting(getOreDictItem("crushedPurified" + name), getOreDictItem("ingot" + name), 0.2F);

					if (AOBD.enableMekanism && ore.shouldMekanism())
						Recipes.macerator.addRecipe(new RecipeInputOreDict("clump" + name), null, getOreDictItem("dustDirty" + name));

				} catch (Exception e) {
					continue;
				}
			}
	}

	private static void addCentrifugeRecipe(IRecipeInput input, int minHeat, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("minHeat", minHeat);

		Recipes.centrifuge.addRecipe(input, metadata, output);
	}

	private static void addOreWashingRecipe(IRecipeInput input, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("amount", 1000);

		Recipes.oreWashing.addRecipe(input, metadata, output);
	}

	private static ItemStack getICItem(String name) {
		try {
			Class<?> itemsClass = Class.forName("ic2.core.Ic2Items");
			return (ItemStack) itemsClass.getField(name).get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}