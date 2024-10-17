package com.openwar.openwarfaction.handler;

import com.openwar.openwarfaction.Main;
import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarfaction.factions.Permission;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimChunk implements Listener {

    private final FactionManager factionManager;
    private JavaPlugin main;
    private final Map<Player, Chunk> playerLastChunk;
    private List<String> container;
    private List<String> door;
    private List<String> interact;

    public ClaimChunk(FactionManager factionManager, Main main) {
        this.factionManager = factionManager;
        this.main = main;
        this.playerLastChunk = new HashMap<>();
        container = new ArrayList<>();
        door = new ArrayList<>();
        interact = new ArrayList<>();
        loadContainerAndDoor();
    }

    private void loadContainerAndDoor() {
        container.add("HBM_DUMMY_BLOCK_ASSEMBLER");
        container.add("HBM_DUMMY_PORT_ASSEMBLER");
        container.add("HBM_DUMMY_BLOCK_CHEMPLANT");
        container.add("HBM_DUMMY_PORT_CHEMPLANT");
        container.add("HBM_DUMMY_BLOCK_REACTOR_SMALL");
        container.add("HBM_DUMMY_PORT_REACTOR_SMALL");
        container.add("HBM_DUMMY_BLOCK_CENTRIFUGE");
        container.add("HBM_DUMMY_BLOCK_GASCENT");
        container.add("HBM_DUMMY_BLOCK_UF6");
        container.add("HBM_DUMMY_BLOCK_PUF6");
        container.add("HBM_DUMMY_BLOCK_FLUIDTANK");
        container.add("HBM_DUMMY_PORT_FLUIDTANK");
        container.add("HBM_DUMMY_BLOCK_REFINERY");
        container.add("HBM_DUMMY_PORT_REFINERY");
        container.add("HBM_DUMMY_BLOCK_CYCLOTRON");
        container.add("HBM_DUMMY_PORT_CYCLOTRON");
        container.add("HBM_DUMMY_BLOCK_VAULT");
        container.add("HBM_DUMMY_BLOCK_BLAST");
        container.add("HBM_DUMMY_BLOCK_SILO_HATCH");
        container.add("HBM_DUMMY_BLOCK_RADGEN");
        container.add("HBM_DUMMY_PORT_RADGEN");
        container.add("HBM_DUMMY_BLOCK_WELL");
        container.add("HBM_DUMMY_PORT_WELL");
        container.add("HBM_DUMMY_BLOCK_PUMPJACK");
        container.add("HBM_DUMMY_PORT_PUMPJACK");
        container.add("HBM_DUMMY_BLOCK_FLARE");
        container.add("HBM_DUMMY_PORT_FLARE");
        container.add("HBM_DUMMY_BLOCK_TURBOFAN");
        container.add("HBM_DUMMY_PORT_TURBOFAN");
        container.add("HBM_DUMMY_PLATE_COMPACT_LAUNCHER");
        container.add("HBM_DUMMY_PORT_COMPACT_LAUNCHER");
        container.add("HBM_DUMMY_PLATE_LAUNCH_TABLE");
        container.add("HBM_DUMMY_PORT_LAUNCH_TABLE");
        container.add("HBM_DUMMY_BLOCK_AMS_LIMITER");
        container.add("HBM_DUMMY_PORT_AMS_LIMITER");
        container.add("HBM_DUMMY_BLOCK_AMS_EMITTER");
        container.add("HBM_DUMMY_PORT_AMS_EMITTER");
        container.add("HBM_DUMMY_BLOCK_AMS_BASE");
        container.add("HBM_DUMMY_PORT_AMS_BASE");
        container.add("HBM_DUMMY_PLATE_CARGO");
        container.add("HBM_RAILGUN_PLASMA");
        container.add("HBM_CRATE");
        container.add("HBM_CRATE_WEAPON");
        container.add("HBM_CRATE_LEAD");
        container.add("HBM_CRATE_METAL");
        container.add("HBM_CRATE_RED");
        container.add("HBM_CRATE_IRON");
        container.add("HBM_CRATE_STEEL");
        container.add("HBM_CRATE_DESH");
        container.add("HBM_CRATE_TUNGSTEN");
        container.add("HBM_CRATE_CAN");
        container.add("HBM_CRATE_JUNGLE");
        container.add("HBM_CRATE_AMMO");
        container.add("HBM_SAFE");
        container.add("HBM_MACHINE_KEYFORGE");
        container.add("HBM_MACHINE_SOLAR_BOILER");
        container.add("HBM_SOLAR_MIRROR");
        container.add("HBM_MACHINE_TELELINKER");
        container.add("HBM_MACHINE_SATLINKER");
        container.add("HBM_SAT_DOCK");
        container.add("HBM_SOYUZ_CAPSULE");
        container.add("HBM_BOOK_GUIDE");
        container.add("HBM_MACHINE_BOILER_OFF");
        container.add("HBM_MACHINE_BOILER_ON");
        container.add("HBM_MACHINE_BOILER_ELECTRIC_OFF");
        container.add("HBM_MACHINE_BOILER_ELECTRIC_ON");
        container.add("HBM_MACHINE_BOILER_RTG_OFF");
        container.add("HBM_MACHINE_BOILER_RTG_ON");
        container.add("HBM_MACHINE_BATTERY_POTATO");
        container.add("HBM_MACHINE_BATTERY");
        container.add("HBM_MACHINE_LITHIUM_BATTERY");
        container.add("HBM_MACHINE_DESH_BATTERY");
        container.add("HBM_MACHINE_SATURNITE_BATTERY");
        container.add("HBM_MACHINE_SCHRABIDIUM_BATTERY");
        container.add("HBM_MACHINE_EUPHEMIUM_BATTERY");
        container.add("HBM_MACHINE_RADSPICE_BATTERY");
        container.add("HBM_MACHINE_DINEUTRONIUM_BATTERY");
        container.add("HBM_MACHINE_ELECTRONIUM_BATTERY");
        container.add("HBM_MACHINE_FENSU");
        container.add("HBM_MACHINE_TRANSFORMER");
        container.add("HBM_MACHINE_TRANSFORMER_20");
        container.add("HBM_MACHINE_TRANSFORMER_DNT");
        container.add("HBM_MACHINE_TRANSFORMER_DNT_20");
        container.add("HBM_MACHINE_CONVERTER_HE_RF");
        container.add("HBM_MACHINE_CONVERTER_RF_HE");
        container.add("HBM_MACHINE_PRESS");
        container.add("HBM_MACHINE_EPRESS");
        container.add("HBM_MACHINE_DIFURNACE_ON");
        container.add("HBM_MACHINE_DIFURNACE_OFF");
        container.add("HBM_MACHINE_DIFURNACE_EXT");
        container.add("HBM_MACHINE_DIFURNACE_RTG_ON");
        container.add("HBM_MACHINE_DIFURNACE_RTG_OFF");
        container.add("HBM_MACHINE_COAL_OFF");
        container.add("HBM_MACHINE_COAL_ON");
        container.add("HBM_MACHINE_DIESEL");
        container.add("HBM_MACHINE_INDUSTRIAL_GENERATOR");
        container.add("HBM_MACHINE_GENERATOR");
        container.add("HBM_MACHINE_REACTOR_SMALL");
        container.add("HBM_MACHINE_CONTROLLER");
        container.add("HBM_MACHINE_REACTOR");
        container.add("HBM_MACHINE_REACTOR_ON");
        container.add("HBM_RBMK_BLANK");
        container.add("HBM_RBMK_BLANK");
        container.add("HBM_RBMK_ROD");
        container.add("HBM_RBMK_ELEMENT");
        container.add("HBM_RBMK_ROD_MOD");
        container.add("HBM_RBMK_ELEMENT_MOD");
        container.add("HBM_RBMK_ROD_REASIM");
        container.add("HBM_RBMK_ELEMENT_REASIM");
        container.add("HBM_RBMK_ROD_REASIM_MOD");
        container.add("HBM_RBMK_ELEMENT_REASIM_MOD");
        container.add("HBM_RBMK_CONTROL");
        container.add("HBM_RBMK_CONTROL");
        container.add("HBM_RBMK_CONTROL_MOD");
        container.add("HBM_RBMK_CONTROL_MOD");
        container.add("HBM_RBMK_CONTROL_AUTO");
        container.add("HBM_RBMK_CONTROL_AUTO");
        container.add("HBM_RBMK_BOILER");
        container.add("HBM_RBMK_BOILER");
        container.add("HBM_RBMK_HEATER");
        container.add("HBM_RBMK_HEATER");
        container.add("HBM_RBMK_REFLECTOR");
        container.add("HBM_RBMK_REFLECTOR");
        container.add("HBM_RBMK_ABSORBER");
        container.add("HBM_RBMK_ABSORBER");
        container.add("HBM_RBMK_MODERATOR");
        container.add("HBM_RBMK_MODERATOR");
        container.add("HBM_RBMK_OUTGASSER");
        container.add("HBM_RBMK_OUTGASSER");
        container.add("HBM_RBMK_COOLER");
        container.add("HBM_RBMK_COOLER");
        container.add("HBM_RBMK_STORAGE");
        container.add("HBM_RBMK_STORAGE");
        container.add("HBM_RBMK_CONSOLE");
        container.add("HBM_RBMK_CRANE_CONSOLE");
        container.add("HBM_RBMK_LOADER");
        container.add("HBM_RBMK_STEAM_INLET");
        container.add("HBM_RBMK_STEAM_OUTLET");
        container.add("HBM_PRIBRIS");
        container.add("HBM_PRIBRIS_BURNING");
        container.add("HBM_PRIBRIS_RADIATING");
        container.add("HBM_PRIBRIS_DIGAMMA");
        container.add("HBM_BLOCK_CORIUM");
        container.add("HBM_BLOCK_CORIUM_COBBLE");
        container.add("HBM_MACHINE_ASSEMBLER");
        container.add("HBM_MACHINE_CHEMPLANT");
        container.add("HBM_MACHINE_CHEMFAC");
        container.add("HBM_MACHINE_MIXER");
        container.add("HBM_MACHINE_RTG_GREY");
        container.add("HBM_MACHINE_TURBINE");
        container.add("HBM_MACHINE_LARGE_TURBINE");
        container.add("HBM_MACHINE_CHUNGUS");
        container.add("HBM_MACHINE_CONDENSER");
        container.add("HBM_MACHINE_TOWER_SMALL");
        container.add("HBM_MACHINE_TOWER_LARGE");
        container.add("HBM_MACHINE_DEUTERIUM_EXTRACTOR");
        container.add("HBM_MACHINE_DEUTERIUM_TOWER");
        container.add("HBM_ANVIL_IRON");
        container.add("HBM_ANVIL_LEAD");
        container.add("HBM_ANVIL_STEEL");
        container.add("HBM_ANVIL_METEORITE");
        container.add("HBM_ANVIL_STARMETAL");
        container.add("HBM_ANVIL_FERROURANIUM");
        container.add("HBM_ANVIL_BISMUTH");
        container.add("HBM_ANVIL_SCHRABIDATE");
        container.add("HBM_ANVIL_DNT");
        container.add("HBM_ANVIL_OSMIRIDIUM");
        container.add("HBM_ANVIL_MURKY");
        container.add("HBM_CONVEYOR");
        container.add("HBM_CONVEYOR_DOUBLE");
        container.add("HBM_CONVEYOR_TRIPLE");
        container.add("HBM_CONVEYOR_EXPRESS");
        container.add("HBM_CONVEYOR_CHUTE");
        container.add("HBM_CONVEYOR_LIFT");
        container.add("HBM_CRANE_EJECTOR");
        container.add("HBM_CRANE_INSERTER");
        container.add("HBM_CRANE_SPLITTER");
        container.add("HBM_CRANE_BOXER");
        container.add("HBM_CRANE_UNBOXER");
        container.add("HBM_CRANE_ROUTER");
        container.add("HBM_CRANE_GRABBER");
        container.add("HBM_MACHINE_NUKE_FURNACE_OFF");
        container.add("HBM_MACHINE_NUKE_FURNACE_ON");
        container.add("HBM_MACHINE_RTG_FURNACE_OFF");
        container.add("HBM_MACHINE_RTG_FURNACE_ON");
        container.add("HBM_MACHINE_SELENIUM");
        container.add("HBM_LAUNCH_PAD");
        container.add("HBM_MACHINE_CENTRIFUGE");
        container.add("HBM_MACHINE_GASCENT");
        container.add("HBM_MACHINE_SILEX");
        container.add("HBM_MACHINE_FEL");
        container.add("HBM_MACHINE_CRYSTALLIZER");
        container.add("HBM_MACHINE_SHREDDER");
        container.add("HBM_MACHINE_WASTE_DRUM");
        container.add("HBM_MACHINE_STORAGE_DRUM");
        container.add("HBM_MACHINE_WELL");
        container.add("HBM_MACHINE_PUMPJACK");
        container.add("HBM_MACHINE_FRACKING_TOWER");
        container.add("HBM_OIL_PIPE");
        container.add("HBM_MACHINE_FLARE");
        container.add("HBM_DRILL_PIPE");
        container.add("HBM_MACHINE_EXCAVATOR");
        container.add("HBM_MACHINE_MINING_LASER");
        container.add("HBM_BARRICADE");
        container.add("HBM_MACHINE_TURBOFAN");
        container.add("HBM_MACHINE_SCHRABIDIUM_TRANSMUTATOR");
        container.add("HBM_MACHINE_COMBINE_FACTORY");
        container.add("HBM_MACHINE_TELEPORTER");
        container.add("HBM_FIELD_DISTURBER");
        container.add("HBM_MACHINE_FORCEFIELD");
        container.add("HBM_MACHINE_RADAR");
        container.add("HBM_RADIOBOX");
        container.add("HBM_RADIOREC");
        container.add("HBM_BM_POWER_BOX");
        container.add("HBM_TESLA");
        container.add("HBM_MACHINE_FRACTION_TOWER");
        container.add("HBM_FRACTION_SPACER");
        container.add("HBM_MACHINE_CATALYTIC_CRACKER");
        container.add("HBM_MACHINE_REFINERY");
        container.add("HBM_MACHINE_ELECTRIC_FURNACE_OFF");
        container.add("HBM_MACHINE_ELECTRIC_FURNACE_ON");
        container.add("HBM_MACHINE_ARC_FURNACE_OFF");
        container.add("HBM_MACHINE_ARC_FURNACE_ON");
        container.add("HBM_MACHINE_MICROWAVE");
        container.add("HBM_MACHINE_CYCLOTRON");
        container.add("HBM_MACHINE_RADGEN");
        container.add("HBM_HEATER_FIREBOX");
        container.add("HBM_HEATER_OVEN");
        container.add("HBM_HEATER_OILBURNER");
        container.add("HBM_HEATER_ELECTRIC");
        container.add("HBM_HEATER_HEATEX");
        container.add("HBM_HEATER_RT");
        container.add("HBM_FURNACE_IRON");
        container.add("HBM_FURNACE_STEEL");
        container.add("HBM_HEAT_BOILER");
        container.add("HBM_RADSENSOR");
        container.add("HBM_MACHINE_AMGEN");
        container.add("HBM_MACHINE_GEO");
        container.add("HBM_MACHINE_MINIRTG");
        container.add("HBM_RTG_POLONIUM");
        container.add("HBM_MACHINE_SPP_BOTTOM");
        container.add("HBM_MACHINE_SPP_TOP");
        container.add("HBM_MARKER_STRUCTURE");
        container.add("HBM_MUFFLER");
        container.add("HBM_STRUCT_LAUNCHER");
        container.add("HBM_STRUCT_SCAFFOLD");
        container.add("HBM_STRUCT_LAUNCHER_CORE");
        container.add("HBM_STRUCT_LAUNCHER_CORE_LARGE");
        container.add("HBM_STRUCT_SOYUZ_CORE");
        container.add("HBM_STRUCT_ITER_CORE");
        container.add("HBM_STRUCT_PLASMA_CORE");
        container.add("HBM_FACTORY_TITANIUM_HULL");
        container.add("HBM_FACTORY_TITANIUM_FURNACE");
        container.add("HBM_FACTORY_TITANIUM_CONDUCTOR");
        container.add("HBM_FACTORY_TITANIUM_CORE");
        container.add("HBM_FACTORY_ADVANCED_HULL");
        container.add("HBM_FACTORY_ADVANCED_FURNACE");
        container.add("HBM_FACTORY_ADVANCED_CONDUCTOR");
        container.add("HBM_FACTORY_ADVANCED_CORE");
        container.add("HBM_REACTOR_ELEMENT");
        container.add("HBM_REACTOR_CONTROL");
        container.add("HBM_REACTOR_HATCH");
        container.add("HBM_REACTOR_EJECTOR");
        container.add("HBM_REACTOR_INSERTER");
        container.add("HBM_REACTOR_CONDUCTOR");
        container.add("HBM_REACTOR_COMPUTER");
        container.add("HBM_FUSION_CONDUCTOR");
        container.add("HBM_FUSION_CENTER");
        container.add("HBM_FUSION_MOTOR");
        container.add("HBM_FUSION_HEATER");
        container.add("HBM_FUSION_HATCH");
        container.add("HBM_FUSION_CORE_BLOCK");
        container.add("HBM_PLASMA");
        container.add("HBM_ITER");
        container.add("HBM_PLASMA_HEATER");
        container.add("HBM_WATZ_ELEMENT");
        container.add("HBM_WATZ_CONTROL");
        container.add("HBM_WATZ_COOLER");
        container.add("HBM_WATZ_END");
        container.add("HBM_WATZ_HATCH");
        container.add("HBM_WATZ_CONDUCTOR");
        container.add("HBM_WATZ_CORE");
        container.add("HBM_FWATZ_CONDUCTOR");
        container.add("HBM_FWATZ_COOLER");
        container.add("HBM_FWATZ_TANK");
        container.add("HBM_FWATZ_SCAFFOLD");
        container.add("HBM_FWATZ_HATCH");
        container.add("HBM_FWATZ_COMPUTER");
        container.add("HBM_FWATZ_CORE");
        container.add("HBM_FWATZ_PLASMA");
        container.add("HBM_AMS_BASE");
        container.add("HBM_AMS_EMITTER");
        container.add("HBM_AMS_LIMITER");
        container.add("HBM_DFC_EMITTER");
        container.add("HBM_DFC_INJECTOR");
        container.add("HBM_DFC_RECEIVER");
        container.add("HBM_DFC_STABILIZER");
        container.add("HBM_DFC_CORE");
        container.add("HBM_UU_GIGAFACTORY");
        container.add("HBM_HADRON_PLATING");
        container.add("HBM_HADRON_PLATING_BLUE");
        container.add("HBM_HADRON_PLATING_BLACK");
        container.add("HBM_HADRON_PLATING_YELLOW");
        container.add("HBM_HADRON_PLATING_STRIPED");
        container.add("HBM_HADRON_PLATING_VOLTZ");
        container.add("HBM_HADRON_PLATING_GLASS");
        container.add("HBM_HADRON_COIL_ALLOY");
        container.add("HBM_HADRON_COIL_GOLD");
        container.add("HBM_HADRON_COIL_NEODYMIUM");
        container.add("HBM_HADRON_COIL_MAGTUNG");
        container.add("HBM_HADRON_COIL_SCHRABIDIUM");
        container.add("HBM_HADRON_COIL_SCHRABIDATE");
        container.add("HBM_HADRON_COIL_STARMETAL");
        container.add("HBM_HADRON_COIL_CHLOROPHYTE");
        container.add("HBM_HADRON_COIL_MESE");
        container.add("HBM_HADRON_DIODE");
        container.add("HBM_HADRON_ANALYSIS");
        container.add("HBM_HADRON_ANALYSIS_GLASS");
        container.add("HBM_HADRON_ACCESS");
        container.add("HBM_HADRON_CORE");
        container.add("HBM_HADRON_POWER");
        container.add("HBM_MACHINE_MISSILE_ASSEMBLY");
        container.add("HBM_COMPACT_LAUNCHER");
        container.add("HBM_LAUNCH_TABLE");
        container.add("HBM_SOYUZ_LAUNCHER");
        container.add("HBM_NUKE_GADGET");
        container.add("HBM_NUKE_BOY");
        container.add("HBM_NUKE_MAN");
        container.add("HBM_NUKE_MIKE");
        container.add("HBM_NUKE_TSAR");
        container.add("HBM_NUKE_FLEIJA");
        container.add("HBM_NUKE_PROTOTYPE");
        container.add("HBM_NUKE_SOLINIUM");
        container.add("HBM_NUKE_N2");
        container.add("HBM_NUKE_FSTBMB");
        container.add("HBM_NUKE_CUSTOM");
        container.add("HBM_BOMB_MULTI");
        container.add("HBM_CRASHED_BOMB");
        container.add("HBM_FIREWORKS");
        container.add("HBM_MINE_AP");
        container.add("HBM_MINE_HE");
        container.add("HBM_MINE_SHRAP");
        container.add("HBM_MINE_FAT");
        container.add("HBM_FLAME_WAR");
        container.add("HBM_FLOAT_BOMB");
        container.add("HBM_EMP_BOMB");
        container.add("HBM_THERM_ENDO");
        container.add("HBM_THERM_EXO");
        container.add("HBM_DET_CORD");
        container.add("HBM_DET_MINER");
        container.add("HBM_DET_CHARGE");
        container.add("HBM_BLOCK_SEMTEX");
        container.add("HBM_DET_N2");
        container.add("HBM_DET_NUKE");
        container.add("HBM_DET_BALE");
        container.add("HBM_RED_BARREL");
        container.add("HBM_PINK_BARREL");
        container.add("HBM_YELLOW_BARREL");
        container.add("HBM_VITRIFIED_BARREL");
        container.add("HBM_LOX_BARREL");
        container.add("HBM_TAINT_BARREL");
        container.add("HBM_RED_CABLE");
        container.add("HBM_RED_WIRE_COATED");
        container.add("HBM_RED_WIRE_SEALED");
        container.add("HBM_CABLE_SWITCH");
        container.add("HBM_CABLE_DETECTOR");
        container.add("HBM_MACHINE_DETECTOR");
        container.add("HBM_CABLE_DIODE");
        container.add("HBM_RED_CABLE_GAUGE");
        container.add("HBM_RED_PYLON");
        container.add("HBM_RED_PYLON_LARGE");
        container.add("HBM_SUBSTATION");
        container.add("HBM_BARREL_PLASTIC");
        container.add("HBM_BARREL_CORRODED");
        container.add("HBM_BARREL_IRON");
        container.add("HBM_BARREL_STEEL");
        container.add("HBM_BARREL_TCALLOY");
        container.add("HBM_BARREL_ANTIMATTER");
        container.add("HBM_MACHINE_UF6_TANK");
        container.add("HBM_MACHINE_PUF6_TANK");
        container.add("HBM_MACHINE_FLUIDTANK");
        container.add("HBM_MACHINE_BAT9000");
        container.add("HBM_MACHINE_ORBUS");
        container.add("HBM_MACHINE_ARMOR_TABLE");
        container.add("HBM_TURRET_LIGHT");
        container.add("HBM_TURRET_HEAVY");
        container.add("HBM_TURRET_ROCKET");
        container.add("HBM_TURRET_FLAMER");
        container.add("HBM_TURRET_TAU");
        container.add("HBM_TURRET_SPITFIRE");
        container.add("HBM_TURRET_CWIS");
        container.add("HBM_TURRET_CHEAPO");
        container.add("HBM_TURRET_CHEKHOV");
        container.add("HBM_TURRET_FRIENDLY");
        container.add("HBM_TURRET_JEREMY");
        container.add("HBM_TURRET_TAUON");
        container.add("HBM_TURRET_RICHARD");
        container.add("HBM_TURRET_HOWARD");
        container.add("HBM_TURRET_HOWARD_DAMAGED");
        container.add("HBM_TURRET_MAXWELL");
        container.add("HBM_TURRET_FRITZ");
        container.add("HBM_TURRET_BRANDON");
        container.add(Material.ARMOR_STAND.name());
        container.add(Material.DISPENSER.name());
        container.add(Material.DROPPER.name());
        container.add(Material.TRAPPED_CHEST.name());
        container.add(Material.CHEST.name());
        container.add(Material.FURNACE.name());
        container.add("MWC_AMMO_PRESS");
        container.add("MWC_WEAPON_WORKBENCH");
        container.add("CFM");
        container.add("STORAGEDRAWERS_BASICDRAWERS");
        container.add("STORAGEDRAWERS_CUSTOMDRAWERS");
        container.add("STORAGEDRAWERS_CONTROLLER");
        container.add("STORAGEDRAWERS_COMPDRAWERS");
        container.add("STORAGEDRAWERS_CONTROLLERSLAVE");
        container.add("IRONCHEST_IRON_CHEST");

        door.add(Material.TRAP_DOOR.name());
        door.add(Material.FENCE.name());
        door.add("HBM_SEAL_FRAME");
        door.add("HBM_SEAL_CONTROLLER");
        door.add("HBM_SEAL_HATCH");
        door.add("HBM_SILO_HATCH");
        door.add("HBM_VAULT_DOOR");
        door.add("HBM_BLAST_DOOR");
        door.add("HBM_SLIDING_BLAST_DOOR");
        door.add("HBM_SLIDING_BLAST_DOOR_2");
        door.add("HBM_SLIDING_BLAST_DOOR_KEYPAD");
        door.add("HBM_SMALL_HATCH");
        door.add("HBM_SLIDING_SEAL_DOOR");
        door.add("HBM_SLIDING_GATE_DOOR");
        door.add("HBM_QE_CONTAINMENT");
        door.add("HBM_QE_SLIDING");
        door.add("HBM_FIRE_DOOR");
        door.add("HBM_WATER_DOOR");
        door.add("HBM_LARGE_VEHICLE_DOOR");
        door.add("HBM_ROUND_AIRLOCK_DOOR");
        door.add("HBM_SECURE_ACCESS_DOOR");
        door.add("HBM_TRANSITION_SEAL");
        door.add("HBM_KEYPAD_TEST");
        door.add("HBM_DOOR_METAL");
        door.add("HBM_DOOR_OFFICE");
        door.add("HBM_DOOR_BUNKER");
        door.add("WOODEN_DOOR");
        door.add("BIRCH_DOOR");
        door.add("SPRUCE_DOOR");
        door.add("JUNGLE_DOOR");
        door.add("ACACIA_DOOR");
        door.add("DARK_OAK_DOOR");
        door.add("TRAP_DOOR");
        door.add("FENCE_GATE");
        door.add("SPRUCE_FENCE_GATE");
        door.add("BIRCH_FENCE_GATE");
        door.add("JUNGLE_FENCE_GATE");
        door.add("DARK_OAK_FENCE_GATE");

        interact.add(Material.LEVER.name());
        interact.add(Material.WOOD_PLATE.name());
        interact.add(Material.STONE_PLATE.name());
        interact.add(Material.IRON_PLATE.name());
        interact.add(Material.GOLD_PLATE.name());
        interact.add(Material.WOOD_BUTTON.name());
        interact.add(Material.STONE_BUTTON.name());
        interact.add(Material.CAULDRON.name());
        interact.add(Material.WORKBENCH.name());
        interact.add(Material.BOAT.name());
        interact.add(Material.ITEM_FRAME.name());
        interact.add(Material.PAINTING.name());
        interact.add(Material.ARMOR_STAND.name());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getPlayer().getLocation().getChunk();
        String command = event.getMessage().toLowerCase();
        Faction faction = factionManager.getFactionByPlayer(player.getUniqueId());
        if (command.equals("/f claim")) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                if (factionManager.getFactionByChunk(chunk) == faction) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou entered faction claim of §c" + faction.getName() + " §8«"));
                    playerLastChunk.put(player, chunk);
                }
            }, 10L);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getPlayer().getLocation().getChunk();
        Faction faction = factionManager.getFactionByChunk(chunk);
        if (faction != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou entered faction claim of §c" + faction.getName() + " §8«"));
            playerLastChunk.put(player, chunk);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("faction")) {
            Chunk fromChunk = event.getFrom().getChunk();
            Chunk toChunk = event.getTo().getChunk();
            if (!fromChunk.equals(toChunk)) {
                Faction fromFaction = factionManager.getFactionByChunk(fromChunk);
                Faction toFaction = factionManager.getFactionByChunk(toChunk);
                if (toFaction != null && (fromFaction == null || !fromFaction.getFactionUUID().equals(toFaction.getFactionUUID()))) {
                    if (!playerLastChunk.containsKey(player) || !playerLastChunk.get(player).equals(toChunk)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou entered faction claim of §c" + toFaction.getName() + " §8«"));
                        playerLastChunk.put(player, toChunk);
                    }
                } else if (toFaction == null && fromFaction != null) {
                    if (playerLastChunk.containsKey(player)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou left faction claim of §c" + fromFaction.getName() + " §8«"));
                        playerLastChunk.remove(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleportInChunk(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Chunk toChunk = event.getTo().getChunk();
        Faction faction = factionManager.getFactionByChunk(toChunk);
        Faction facplayer = factionManager.getFactionByPlayer(player.getUniqueId());
        System.out.println("1");
        if (faction != null && facplayer != null) {
            System.out.println("2");
            if (faction.getName().equals(facplayer.getName())) {
                System.out.println("3");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §fYou entered faction claim of §c" + faction.getName() + " §8«"));
                playerLastChunk.put(player, toChunk);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlaceLaunch(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().equals("HBM_LAUNCH_TABLE") || event.getBlock().equals("HBM_LAUNCH_PAD") || event.getBlock().equals("HBM_COMPACT_LAUNCHER")){
            Chunk chunk = event.getBlock().getChunk();
            Faction chunkOwner = factionManager.getFactionByChunk(chunk);
            if (chunkOwner == null || !(chunkOwner == factionManager.getFactionByPlayer(player.getUniqueId()))){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou can't place that outside your claim."));
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals("faction")) {return;}
        if (event.getBlock() != null) {
            Chunk blockChunk = event.getBlock().getChunk();
            Faction chunkOwner = factionManager.getFactionByChunk(blockChunk);
            if (chunkOwner == null) {
                return;
            }
            Faction playerFaction = factionManager.getFactionByPlayer(player.getUniqueId());
            if (chunkOwner == playerFaction) {
                if (!factionManager.hasPermissionInFaction(player.getUniqueId(), playerFaction, Permission.BUILD)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to build here."));
                    event.setCancelled(true);
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to build here."));
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals("faction")) {return;}
        if (event.getBlockPlaced() != null) {
            Chunk blockChunk = event.getBlock().getChunk();
            Faction chunkOwner = factionManager.getFactionByChunk(blockChunk);
            if (chunkOwner == null) {
                return;
            }
            Faction playerFaction = factionManager.getFactionByPlayer(player.getUniqueId());
            if (chunkOwner == playerFaction) {
                if (!factionManager.hasPermissionInFaction(player.getUniqueId(), playerFaction, Permission.BUILD)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to build here."));
                    event.setCancelled(true);
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to build here."));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals("faction")) {return;}
        if (event.getClickedBlock() != null) {
            Chunk blockChunk = event.getClickedBlock().getChunk();
            Faction chunkOwner = factionManager.getFactionByChunk(blockChunk);
            if (chunkOwner == null) {
                return;
            }
            Faction playerFaction = factionManager.getFactionByPlayer(player.getUniqueId());
            //PLAYER IN FACTION
            if (chunkOwner == playerFaction) {
                //CONTAINER
                if (isContainer(event.getClickedBlock())) {
                    if (!factionManager.hasPermissionInFaction(player.getUniqueId(), playerFaction, Permission.CONTAINERS)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to use container."));
                        event.setCancelled(true);
                    }
                }

                //PORTE
                if (isDoor(event.getClickedBlock())) {
                    if (!factionManager.hasPermissionInFaction(player.getUniqueId(), playerFaction, Permission.DOORS)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to open door."));
                        event.setCancelled(true);
                    }
                }

                //INTERACT
                if (isContainer(event.getClickedBlock()) || isDoor(event.getClickedBlock()) || isInteract(event.getClickedBlock())) {
                    if (!factionManager.hasPermissionInFaction(player.getUniqueId(), playerFaction, Permission.INTERACT)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to interact with this."));
                        event.setCancelled(true);
                    }
                }
                //PLAYER NOT IN FACTION
            } else if (isContainer(event.getClickedBlock()) || isDoor(event.getClickedBlock()) || isInteract(event.getClickedBlock())) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to interact here."));
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals("faction")) {return;}
        if (event.getRightClicked() instanceof ArmorStand || event.getRightClicked() instanceof ItemFrame) {
            Entity entity = event.getRightClicked();
            Chunk entityChunk = entity.getLocation().getChunk();
            Faction chunkOwner = factionManager.getFactionByChunk(entityChunk);
            if (chunkOwner == null) {
                return;
            }

            Faction playerFaction = factionManager.getFactionByPlayer(player.getUniqueId());
            if (chunkOwner == playerFaction) {
                if (!factionManager.hasPermissionInFaction(player.getUniqueId(), playerFaction, Permission.CONTAINERS)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to interact with this entity."));
                    event.setCancelled(true);
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8» §cYou don't have the permission to interact here."));
                event.setCancelled(true);
            }
        }
    }


    public boolean isInteract(Block bloc) {
        String name = bloc.getType().name();
        return interact.contains(name);
    }

    public boolean isContainer(Block bloc) {
        String name = bloc.getType().name();
        return container.contains(name);
    }

    public boolean isDoor(Block bloc) {
        String name = bloc.getType().name();
        return door.contains(name);
    }
}