package buffer;

import net.fabricmc.api.ModInitializer;
import buffer.registry.BlockRegistry;
import buffer.registry.EntityRegistry;
import buffer.registry.ItemRegistry;
import buffer.registry.ScreenRegistryServer;

/*

	TODO: FIX THE MOTHERFUCKING DESYC I SWEAR IT'S THE ONLY FUCKING THING REMAINING FOR COMPLETE BASIC FUNCTIONALITY YET THIS PIECE OF SHIT IS STILL FAILING FUCKING HELL WHY YOU DO THIS TO ME LIFE IT WAS WORKING JUST FINE WITH SERVER THREAD PAUSED BUT WHEN YOU UNPAUSE IT BECAUSE GUESS WHAT MINECRAFT KINDA NEEDS A SERVER IT JUST NUKES ITSELF AND IT FUCKING SUCKS AND I WISH WHOEVER CREATED THIS SHIT COULD JUST HAVE DONE PROPERLY SO THAT IT ISN'T THIS MUCH OF A PAIN FUCK CONTAINERS FUCK SLOTS FUCK ITEMSTACKS AND MOST IMPORTANTLY FUCK NETTY AND FUCK NETWORKING THANKS FOR COMING TO MY TED TALK

*/

public class BufferMod implements ModInitializer {
	@Override
	public void onInitialize() {
		BlockRegistry.registerBlocks();
		ItemRegistry.registerItems();
		EntityRegistry.registerBlocks();
		ScreenRegistryServer.registerScreens();
	}
}
