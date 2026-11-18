package com.reiko45.furniture.core.modded;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.Nullable;

/** A hidden, always-on pack holding the resources for modded-wood furniture, made in memory by {@link WoodResources}. */
public final class ModdedWoodPack implements PackResources {
    private static final String ID = ReikosFurniture.MODID + "_modded_woods";
    private static final Map<PackType, Map<ResourceLocation, byte[]>> CACHE = new EnumMap<>(PackType.class);

    private final PackLocationInfo location;
    private final PackType type;

    private ModdedWoodPack(PackLocationInfo location, PackType type) {
        this.location = location;
        this.type = type;
    }

    /** Mod bus listener: adds the pack for client resources and server data. */
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (ModdedWoods.detected() && FurnitureWood.modded().isEmpty()) {
            return;
        }
        PackType type = event.getPackType();
        PackLocationInfo info = new PackLocationInfo(ID, Component.literal("Reiko's Furniture: modded woods"), PackSource.BUILT_IN,
                Optional.empty());
        Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo location) {
                return new ModdedWoodPack(location, type);
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                return new ModdedWoodPack(location, type);
            }
        };
        Pack pack = Pack.readMetaAndCreate(info, resources, type, new PackSelectionConfig(true, Pack.Position.TOP, false));
        if (pack != null) {
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }
    }

    private Map<ResourceLocation, byte[]> files() {
        synchronized (CACHE) {
            return CACHE.computeIfAbsent(type, side -> WoodResources.build(side, FurnitureWood.modded()));
        }
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getRootResource(String... elements) {
        return null;
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        byte[] bytes = packType == type ? files().get(location) : null;
        return bytes == null ? null : () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
        if (packType != type) {
            return;
        }
        String prefix = path.endsWith("/") ? path : path + "/";
        files().forEach((location, bytes) -> {
            if (location.getNamespace().equals(namespace) && location.getPath().startsWith(prefix)) {
                output.accept(location, () -> new ByteArrayInputStream(bytes));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType == type ? files().keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()) : Set.of();
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        if (deserializer.getMetadataSectionName().equals("pack")) {
            return (T) new PackMetadataSection(Component.literal("Furniture in other mods' woods"),
                    SharedConstants.getCurrentVersion().getPackVersion(type));
        }
        return null;
    }

    @Override
    public PackLocationInfo location() {
        return location;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void close() {
    }
}
