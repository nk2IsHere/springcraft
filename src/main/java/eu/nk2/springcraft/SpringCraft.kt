package eu.nk2.springcraft

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(modid = SpringCraft.MODID, name = SpringCraft.NAME, version = SpringCraft.VERSION)
class SpringCraft {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        runSpringApplication()
    }

    companion object {
        const val MODID = "springcraft"
        const val NAME = "SpringCraft"
        const val VERSION = "1.0.0+0"
        lateinit var logger: Logger
    }
}
